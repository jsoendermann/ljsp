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

  def define: Parser[SDefine] = "("~>"define"~>"("~>identifier~rep(identifier)~")"~expression<~")" ^^ {
    case name~params~")"~e => SDefine(name, params, e)
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
  def expression: Parser[SExp] = identifier | integer | _if | lambda | primitive_application | application | let
  def prog: Parser[SProgram] = rep(define)~expression ^^ {
    case ds~e => SProgram(ds, e)
  }

  
  // TODO this throws an exception instead of printing a nice error message when the input isn't well formed
  def parseExpr(str: String) = parse(prog, str).get
}



// ################ CPS Translation ####################


def CPS_translate_prog(p : SProgram, k : SExp => SExp) : SProgram = {
  SProgram(p.ds.map{d => 
    val c = fresh("cont")
    SDefine(d.name, c :: d.params, CPSTail(d.e, c))
  }, CPS(p.e, k))
}

def CPS(e: SExp, k: SExp => SExp) : SExp = e match {
  // For atomic values, no CPS-Translation is necessary. Simply apply k to e
  case SIdn(idn) => k(e)
  case SInt(i) => k(e)

  // For if, evaluate e1 first, then branch with two recursive calls
  case SIf(e1, e2, e3) => {
    val c = fresh("var")
    val p = fresh("var")
    CPS(e1, (ce1: SExp) =>
      SLet(c, SLambda(List(p), k(p)),
        SIf(ce1, CPSTail(e2, c), CPSTail(e3, c))))
  }

  // for lambdas and defines, add an additional parameter that will hold the continuation
  // and call it with the result when the function is done
  case SLambda(params, e) => {
    val c = fresh("cont")
    k(SLambda(c :: params, CPSTail(e, c)))
  }
  case SDefine(name, params, e) => {
    val c = fresh("cont")
    k(SDefine(name, c :: params, CPSTail(e, c)))
  }

  // For applications, evaluate proc first, then all the arguments
  case SAppl(proc, es) => {
    // the application will have (lambda (f) k(f)) added as additional parameter
    // which will be the continuation for the function that is being called
    val f = fresh("var")

    // CPS-Translate the expression for the procedure first
    CPS(proc, (cproc: SExp) => {

      // this function calls CPS recursively for all expressions in es and accumulates the result in ces
      // the last call with an empty es creates calls the function with the additional lambda mentioned above
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SAppl(cproc, SLambda(List(f), k(f)) :: ces) //SLet(f, SAppl(cproc, ces), k(f))
        case (e::es) => CPS(e, (ce: SExp) => aux(es, ces ::: List(ce)))
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
      case (e::es) => CPS(e, (ce: SExp) => aux(es, ces ::: List(ce)))
    }
    aux(es, Nil)
  }


  case SLet(idn, e1, e2) => {
    // c is a simple continuation lambda
    val f = fresh("var")
    val c = SLambda(List(f), k(f))

    val ce2 = CPSTail(e2, c)

    // This creates a lambda with a parameter of the same name as the identifier in the let
    // e1 is CPS translated and applied to this new lambda
    CPSTail(e1, SLambda(List(idn), ce2))
  }
}

// This function is very similar to CPS, the difference being that for CPSTail the second parameter is an
// identifier which will point to a continuation function that has to be called with the result of e.
// For explanations, please see the comments to CPS
def CPSTail(e: SExp, c: SExp) : SExp = e match {
  case SIdn(idn) => SAppl(c, List(e))
  case SInt(i) => SAppl(c, List(e))

  case SIf(e1, e2, e3) => {
    val c = fresh("var")
    val p = fresh("var")
    CPS(e1, (ce1: SExp) =>
      SLet(c, SLambda(List(p), SAppl(c, List(p))),
        SIf(ce1, CPSTail(e2, c), CPSTail(e3, c))))
  }

  case SLambda(params, e) => {
    val c_ = fresh("cont")
    SAppl(c, List((SLambda(c_ :: params, CPSTail(e, c_)))))
  }
  
  // TODO redundant?
  case SDefine(name, params, e) => {
    val c_ = fresh("cont")
    SAppl(c, List((SDefine(name, c_ :: params, CPSTail(e, c_)))))
  }

  case SAppl(proc, es) => {
    val f = fresh("var")
    CPS(proc, (cproc: SExp) => {
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SAppl(cproc, SLambda(List(f), SAppl(c, List(f))) :: ces)
        case (e::es) => CPS(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
    aux(es, Nil)
    })
  }

  case SApplPrimitive(proc, es) => {
    val z = fresh("var")

    def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
      case Nil => SLet(z, SApplPrimitive(proc, ces), SAppl(c, List(z)))
      case (e::es) => CPS(e, (ce: SExp) => aux(es, ces ::: List(ce)))
    }
    aux(es, Nil)
  }

  case SLet(idn, e1, e2) => {
    val f = fresh("var")
    val c_ = SLambda(List(f), SAppl(c, List(f)))

    val ce2 = CPSTail(e2, c_)

    CPSTail(e1, SLambda(List(idn), ce2))
  }
}



// ################ Closure conversion ####################

case class SMakeEnv(idns: List[SIdn]) extends SExp { 
  override def toString = {
    if (idns.size == 0)
      "(make-env)"
    else
      "(make-env " + idns.mkString(" ") + ")" 
  }
}
case class SMakeLambda(lambda: SLambda, env: SExp) extends SExp { override def toString = "(make-lambda " + lambda.toString + " " + env.toString + ")" }
case class SNth(n: Int, e: SExp) extends SExp { override def toString = "(nth " + n.toString + " " + e.toString + ")" }
case class SGetEnv(e: SExp) extends SExp { override def toString = "(get-env " + e.toString + ")" }
case class SGetProc(e: SExp) extends SExp { override def toString = "(get-proc " + e.toString + ")" }

def FreeVars(p: SProgram, e: SExp) : Set[Idn] = e match {
  case SDefine(name, params, e) => FreeVars(p, e) -- Set(name.idn) -- params.map(sIdn => sIdn.idn)
  case SIdn(idn) => Set(idn)
  case SInt(_) => Set()
  case SIf(e1, e2, e3) => FreeVars(p, e1) ++ FreeVars(p, e2) ++ FreeVars(p, e3)
  // idn.idn is necessary becase idn is of type SIdn but Idn is required
  case SLet(idn, e1, e2) => FreeVars(p, e1) ++ (FreeVars(p, e2) - idn.idn)
  // TODO case sdefine?
  case SLambda(params, e) => FreeVars(p, e) -- params.map(sIdn => sIdn.idn)
  case SAppl(proc, es) => (FreeVars(p, proc) -- p.ds.map{d => d.name.idn}) ++ es.flatMap{e => FreeVars(p, e)}.toSet
  case SApplPrimitive(proc, es) => es.flatMap{e => FreeVars(p, e)}.toSet
}



def ClConv(p: SProgram, e: SExp) : SExp = e match {
  case SProgram(ds, e) => SProgram(ds.map{d => SDefine(d.name, d.params, ClConv(p, d.e))}, ClConv(p, e))
  case SDefine(name, params, e) => SDefine(name, params, ClConv(p, e))
  // Trivial cases
  case SIdn(idn) => e
  case SInt(i) => e
  case SIf(e1, e2, e3) => SIf(ClConv(p, e1), ClConv(p, e2), ClConv(p, e3))
  case SLet(idn, e1, e2) => SLet(idn, ClConv(p, e1), ClConv(p, e2))
  //case SDefine(name, params, e) => SDefine(name, params, ClConv(e))

  // TODO closure convert e)
  case SLambda(params, e) => {
    val free_vars = FreeVars(p, SLambda(params, e)).toList

    val env = fresh("env")
    val free_vars_with_index = free_vars.zipWithIndex
    val body_with_free_vars_from_env = free_vars_with_index.foldRight(e) {
      case ((x, n), e) => SLet(SIdn(x), SNth(n, env), e)
    }

    SMakeLambda(SLambda(env :: params, body_with_free_vars_from_env), SMakeEnv(free_vars.map{SIdn(_)}))
  }

  case SAppl(proc, es) => {
    val converted_proc = ClConv(p, proc)
    converted_proc match {
      case SMakeLambda(lambda, env) => {
        val converted_lambda = SMakeLambda(lambda, env)
        val converted_lambda_var = fresh("converted_lambda")
        SLet(converted_lambda_var, converted_lambda, SAppl(SGetProc(converted_lambda_var), SGetEnv(converted_lambda_var) :: es.map{e => ClConv(p, e)}))
      }
      case _ => SAppl(converted_proc, es.map{e => ClConv(p, e)})
    }
  }

  case SApplPrimitive(proc, es) => SApplPrimitive(proc, es.map{e => ClConv(p, e)})
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
val progCps = CPS_translate_prog(progTree, (x: SExp) => x)//SAppl(SIdn("display"), List(x)))
println(progCps.toString)
println()

println("Closure converted program:")
val progCC = ClConv(progCps, progCps)
println(progCC.toString())
println()

