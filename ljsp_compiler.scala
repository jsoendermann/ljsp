import scala.util.parsing.combinator._

//type Num = Int
type Idn = String



// TODO test case for later: ((if #f + *) 3 4)



// ################ Classes ####################

abstract class SExp

case class SProgram(ds: List[SDefine], e: SExp) extends SExp { override def toString = ds.mkString("\n") + "\n" + e.toString }

case class SDefine(name: SIdn, params: List[SIdn], e: SExp) extends SExp { override def toString = "(define (" + name.toString() + " " +  params.mkString(" ") + ") " + e.toString() + ")" }

case class SIdn(idn: Idn) extends SExp { override def toString = idn }
case class SInt(i: Int) extends SExp { override def toString = i.toString() }
// TODO double
case class SIf(e1: SExp, e2: SExp, e3: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + " " + e3.toString() + ")" }
case class SLambda(params: List[SIdn], e: SExp) extends SExp { override def toString = "(lambda (" + params.mkString(" ") + ") " + e.toString() + ")" }
// TODO begin
case class SAppl(proc: SExp, es: List[SExp]) extends SExp { override def toString = "(" + proc.toString() + " " + es.mkString(" ") + ")"}
case class SApplPrimitive(proc: SIdn, es: List[SExp]) extends SExp { override def toString = "(" + proc.toString() + " " + es.mkString(" ") + ")"}
case class SLet(idn: SIdn, e1: SExp, e2: SExp) extends SExp { override def toString = "(let ((" + idn.toString() + " " + e1.toString() + ")) " + e2.toString() + ")" }
// TODO more than one variable let



// fresh returns a new unique identifier that begins with s
val fresh = (() =>  {
  // This is a map that maps identifier prefixes to their counters
  // New prefixes get added to the map automatically, it's not necessary
  // to initialize this map with all prefixes that will be used
  var counters = scala.collection.mutable.Map("var" -> -1, "cont" -> -1)

  // this is the function that fresh will be bound to
  (s: String) => {
    counters get s match {
      case Some(counter) => {
        counters(s) = counter + 1
        SIdn(s ++ "_" ++ counters(s).toString())
      }
      case None => {
        counters ++ scala.collection.mutable.Map(s -> 0)
        SIdn(s ++ "_0")
      }
    }
 }
})()




// ################ Parser ####################



object JLispParsers extends JavaTokenParsers {
  def expression: Parser[SExp] = identifier | integer | _if | lambda | primitive_application | application | let

  def prog: Parser[SProgram] = rep(define)~expression ^^ {
    case ds~e => SProgram(ds, e)
  }

  def define: Parser[SDefine] = "("~>"define"~>"("~>identifier~rep(identifier)~")"~expression<~")" ^^ {
    case name~params~")"~e => SDefine(name, params, e)
  }

  def identifier: Parser[SIdn] = """[a-zA-Z=*+/<>!\?\-][a-zA-Z0-9=*+/<>!\?\-]*""".r ^^ (SIdn(_))
  // TODO: Add all primitive operations
  def primitive_proc: Parser[SIdn] = ("+" | "-" | "*" | "/" | "<" | ">" | "and" | "or" | "equal?" | "car" | "cdr") ^^ (SIdn(_))
  def integer: Parser[SInt] = wholeNumber ^^ (i => SInt(i.toInt))

  def _if: Parser[SIf] = "("~>"if"~>expression~expression~expression<~')' ^^ {
    case e1~e2~e3 => SIf(e1, e2, e3)
  }
    
  def lambda: Parser[SLambda] = "("~>"lambda"~>"("~>rep1(identifier, identifier)~")"~expression<~")" ^^ {
    case params~")"~e => SLambda(params, e)
  }

    def primitive_application: Parser[SApplPrimitive] = "("~>primitive_proc~rep1(expression, expression)<~")" ^^ {
    case proc~es => SApplPrimitive(proc, es)
  }
  def application: Parser[SAppl] = "("~>expression~rep1(expression, expression)<~")" ^^ {
    case proc~es => SAppl(proc, es)
  }
  def let: Parser[SLet] = "("~>"let"~>"("~"("~>identifier~expression~")"~")"~expression<~")" ^^ {
    case idn~e1~")"~")"~e2 => SLet(idn, e1, e2)
  }
  
  // TODO this throws an exception instead of printing a nice error message when the input isn't well formed
  def parseExpr(str: String) = parse(prog, str).get
}



// ################ CPS Translation ####################



// this function only exists for typecasting, maybe there is a better way to do this
def cps_trans_prog(p : SProgram, k : SExp => SExp) : SProgram = {
  cps_trans(p, k).asInstanceOf[SProgram]
}

def cps_trans(e: SExp, k: SExp => SExp) : SExp = e match {
  // CPS-translate all function definitions and the expression
  case SProgram(ds, e) => {
    SProgram(ds.map{d => 
        val c = fresh("cont")
        SDefine(d.name, c :: d.params, cps_tail_trans(d.e, c))
      }, cps_trans(e, k))
  }

  // For SDefine case see further down, as its CPS translation is very similar to that
  // of SLambda, the two are grouped together

  // For atomic values, no CPS-Translation is necessary. Simply apply k to e
  case SIdn(idn) => k(e)
  case SInt(i) => k(e)

  // For if, evaluate e1 first, then branch with two recursive calls
  case SIf(e1, e2, e3) => {
    val c = fresh("var")
    val p = fresh("var")
    cps_trans(e1, (ce1: SExp) =>
      SLet(c, SLambda(List(p), k(p)),
        SIf(ce1, cps_tail_trans(e2, c), cps_tail_trans(e3, c))))
  }

  // for lambdas and defines, add an additional parameter that will hold the continuation
  // and call it with the result when the function is done
  case SLambda(params, e) => {
    val c = fresh("cont")
    k(SLambda(c :: params, cps_tail_trans(e, c)))
  }
  case SDefine(name, params, e) => {
    val c = fresh("cont")
    k(SDefine(name, c :: params, cps_tail_trans(e, c)))
  }

  // For applications, evaluate proc first, then all the arguments
  case SAppl(proc, es) => {
    // the application will have (lambda (f) k(f)) added as additional parameter
    // which will be the continuation for the function that is being called
    val f = fresh("var")

    // CPS-Translate the expression for the procedure first
    cps_trans(proc, (cproc: SExp) => {

      // this function calls CPS recursively for all expressions in es and accumulates the result in ces
      // the last call with an empty es creates calls the function with the additional lambda mentioned above
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SAppl(cproc, SLambda(List(f), k(f)) :: ces) //SLet(f, SAppl(cproc, ces), k(f))
        case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
      aux(es, Nil)
    })
  }

  // For primitive applications, translate all the arguments
  // and continue with let f=SAppl in k(f)
  case SApplPrimitive(proc, es) => {
    val z = fresh("var")

    // this function calls CPS recursively for all expressions in es and accumulates the result in ces
    // the last call with an empty es creates calls the function with the additional lambda mentioned above
    def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
      case Nil => SLet(z, SApplPrimitive(proc, ces), k(z))
      case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
    }
    aux(es, Nil)
  }


  case SLet(idn, e1, e2) => {
    // c is a simple continuation lambda
    val f = fresh("var")
    val c = SLambda(List(f), k(f))

    val ce2 = cps_tail_trans(e2, c)

    // This creates a lambda with a parameter of the same name as the identifier in the let
    // e1 is CPS translated and applied to this new lambda
    cps_tail_trans(e1, SLambda(List(idn), ce2))
  }
}

// This function is very similar to CPS, the difference being that for CPSTail the second parameter is an
// identifier which will point to a continuation function that has to be called with the result of e.
// For explanations, please see the comments to CPS
def cps_tail_trans(e: SExp, c: SExp) : SExp = e match {
  case SIdn(idn) => SAppl(c, List(e))
  case SInt(i) => SAppl(c, List(e))

  case SIf(e1, e2, e3) => {
    val c_ = fresh("var")
    val p = fresh("var")
    cps_trans(e1, (ce1: SExp) =>
      SLet(c_, SLambda(List(p), SAppl(c, List(p))),
        SIf(ce1, cps_tail_trans(e2, c_), cps_tail_trans(e3, c_))))
  }

  case SLambda(params, e) => {
    val c_ = fresh("cont")
    SAppl(c, List((SLambda(c_ :: params, cps_tail_trans(e, c_)))))
  }

  case SAppl(proc, es) => {
    val f = fresh("var")
    cps_trans(proc, (cproc: SExp) => {
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SAppl(cproc, SLambda(List(f), SAppl(c, List(f))) :: ces)
        case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
    aux(es, Nil)
    })
  }

  case SApplPrimitive(proc, es) => {
    val z = fresh("var")

    def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
      case Nil => SLet(z, SApplPrimitive(proc, ces), SAppl(c, List(z)))
      case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
    }
    aux(es, Nil)
  }

  case SLet(idn, e1, e2) => {
    val f = fresh("var")
    val c_ = SLambda(List(f), SAppl(c, List(f)))

    val ce2 = cps_tail_trans(e2, c_)

    cps_tail_trans(e1, SLambda(List(idn), ce2))
  }
}



// ################ Closure conversion ####################

case class SMakeEnv(idns: List[SIdn]) extends SExp { override def toString = { "(make-env" + (if (idns.size == 0) ")" else " " + idns.mkString(" ") + ")") }}
case class SMakeLambda(lambda: SLambda, env: SExp) extends SExp { override def toString = "(make-lambda " + lambda.toString + " " + env.toString + ")" }
case class SNth(n: Int, e: SExp) extends SExp { override def toString = "(nth " + n.toString + " " + e.toString + ")" }
case class SGetEnv(e: SExp) extends SExp { override def toString = "(get-env " + e.toString + ")" }
case class SGetProc(e: SExp) extends SExp { override def toString = "(get-proc " + e.toString + ")" }

def free_vars(p: SProgram, e: SExp) : Set[Idn] = e match {
  case SDefine(name, params, e) => free_vars(p, e) -- Set(name.idn) -- params.map(sIdn => sIdn.idn)
  case SIdn(idn) => Set(idn)
  case SInt(_) => Set()
  case SIf(e1, e2, e3) => free_vars(p, e1) ++ free_vars(p, e2) ++ free_vars(p, e3)
  // idn.idn is necessary becase idn is of type SIdn but Idn is required
  case SLet(idn, e1, e2) => free_vars(p, e1) ++ (free_vars(p, e2) - idn.idn)
  case SLambda(params, e) => free_vars(p, e) -- params.map(sIdn => sIdn.idn)
  // If the proc is in the definitions block, it's not free
  // TODO is there a better way to give this case access to the name of definitions in the program
  // than to have p as an additional parameter to both free_vars and cl_conv?
  case SAppl(proc, es) => (free_vars(p, proc) -- p.ds.map{d => d.name.idn}) ++ es.flatMap{e => free_vars(p, e)}.toSet
  case SApplPrimitive(proc, es) => es.flatMap{e => free_vars(p, e)}.toSet
}

// this function only exists for typecasting, maybe there is a better way to do this
def cl_conv_prog(p: SProgram) : SProgram = {
  cl_conv(p, p).asInstanceOf[SProgram]
}

def cl_conv(p: SProgram, e: SExp) : SExp = e match {
  // Trivial cases
  case SProgram(ds, e) => SProgram(ds.map{d => SDefine(d.name, d.params, cl_conv(p, d.e))}, cl_conv(p, e))
  case SDefine(name, params, e) => SDefine(name, params, cl_conv(p, e))
  case SIdn(idn) => e
  case SInt(i) => e
  case SIf(e1, e2, e3) => SIf(cl_conv(p, e1), cl_conv(p, e2), cl_conv(p, e3))
  case SLet(idn, e1, e2) => SLet(idn, cl_conv(p, e1), cl_conv(p, e2))

  case SLambda(params, e) => {
    // free vars
    val fvs = free_vars(p, SLambda(params, e)).toList

    val env = fresh("env")
    val fvs_with_index = fvs.zipWithIndex
    val e_with_fvs_bound = fvs_with_index.foldRight(cl_conv(p, e)) {
      case ((x, n), e) => SLet(SIdn(x), SNth(n, env), e)
    }

    SMakeLambda(SLambda(env :: params, e_with_fvs_bound), SMakeEnv(fvs.map{SIdn(_)}))
  }

  case SAppl(proc, es) => {
    val converted_proc = cl_conv(p, proc)
    // if the proc is a lambda constructor, calling it is more complex
    converted_proc match {
      case SMakeLambda(lambda, env) => {
        val converted_lambda = SMakeLambda(lambda, env)
        val converted_lambda_var = fresh("converted_lambda")
        SLet(converted_lambda_var, converted_lambda, SAppl(SGetProc(converted_lambda_var), SGetEnv(converted_lambda_var) :: es.map{e => cl_conv(p, e)}))
      }
      case _ => SAppl(converted_proc, es.map{e => cl_conv(p, e)})
    }
  }

  case SApplPrimitive(proc, es) => SApplPrimitive(proc, es.map{e => cl_conv(p, e)})
}


//TODO better option handling

if (args.length == 0) {
  // TODO more helpful usage
  println("<cmd> <source>")
  System.exit(-1)
}

println("Parsed program:")
val progTree = JLispParsers.parseExpr(args(0))
println(progTree.toString)
println()

println("CPS translated program:")
val progCps = cps_trans_prog(progTree, (x: SExp) => x)//SAppl(SIdn("display"), List(x)))
println(progCps.toString)
println()

println("Closure converted program:")
val progCC = cl_conv_prog(progCps)
println(progCC.toString())
println()

