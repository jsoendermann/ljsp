import scala.util.parsing.combinator._

//type Num = Int
type Idn = String



// TODO test case for later: ((if #f + *) 3 4)



// ################ Classes ####################



abstract class SExp
case class SIdn(idn: Idn) extends SExp { override def toString = idn }
case class SInt(i: Int) extends SExp { override def toString = i.toString() }
case class SBool(b: Boolean) extends SExp { override def toString = if (b) "#t" else "#f" }
// TODO double
case class SIf(e1: SExp, e2: SExp, e3: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + " " + e3.toString() + ")" }
case class SLambda(params: List[SIdn], e: SExp) extends SExp { override def toString = "(lambda (" + params.mkString(" ") + ") " + e.toString() + ")" }
case class SDefine(name: SIdn, params: List[SIdn], e: SExp) extends SExp { override def toString = "(define (" + name.toString() + " " +  params.mkString(" ") + ") " + e.toString() + ")" }
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
  def primitive_proc: Parser[SIdn] = ("+" | "-" | "*" | "/" | "<" | ">" | "and" | "or" | "equal?") ^^ (SIdn(_))
  def integer: Parser[SInt] = wholeNumber ^^ (i => SInt(i.toInt))
  def boolean: Parser[SBool] = ("#t" | "#f") ^^ {
    case "#t" => SBool(true)
    case "#f" => SBool(false)
  }

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
  def expression: Parser[SExp] = identifier | integer | boolean | _if | lambda | define| primitive_application | application | let

  // TODO this throws an exception instead of printing a nice error message when the input isn't well formed
  def parseExpr(str: String) = parse(expression, str).get
}



// ################ CPS Translation ####################



def CPS(e: SExp, k: SExp => SExp) : SExp = e match {
  // For atomic values, no CPS-Translation is necessary. Simply apply k to e
  case SIdn(idn) => k(e)
  case SInt(i) => k(e)
  case SBool(b) => k(e)

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
    k(SLambda(params ::: List(c), CPSTail(e, c)))
  }
  case SDefine(name, params, e) => {
    val c = fresh("cont")
    k(SDefine(name, params ::: List(c), CPSTail(e, c)))
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
        case Nil => SAppl(cproc, ces ::: List(SLambda(List(f), k(f)))) //SLet(f, SAppl(cproc, ces), k(f))
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
      case Nil => SLet(z, SAppl(proc, ces), k(z))
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
  case SBool(b) => SAppl(c, List(e))

  case SIf(e1, e2, e3) => {
    val c = fresh("var")
    val p = fresh("var")
    CPS(e1, (ce1: SExp) =>
      SLet(c, SLambda(List(p), SAppl(c, List(p))),
        SIf(ce1, CPSTail(e2, c), CPSTail(e3, c))))
  }

  case SLambda(params, e) => {
    val c_ = fresh("cont")
    SAppl(c, List((SLambda(params ::: List(c_), CPSTail(e, c_)))))
  }
  
  case SDefine(name, params, e) => {
    val c_ = fresh("cont")
    SAppl(c, List((SDefine(name, params ::: List(c_), CPSTail(e, c_)))))
  }

  case SAppl(proc, es) => {
    val f = fresh("var")
    CPS(proc, (cproc: SExp) => {
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SAppl(cproc, ces ::: List(SLambda(List(f), SAppl(c, List(f)))))
        case (e::es) => CPS(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
    aux(es, Nil)
    })
  }

  case SApplPrimitive(proc, es) => {
    val z = fresh("var")

    def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
      case Nil => SLet(z, SAppl(proc, ces), SAppl(c, List(z)))
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

def FreeVars(e: SExp) : Set[Idn] = e match {
  case SIdn(idn) => Set(idn)
  case SInt(_) => Set()
  case SBool(_) => Set()
  case SIf(e1, e2, e3) => FreeVars(e1) ++ FreeVars(e2) ++ FreeVars(e3)
  case SLet(idn, e1, e2) => FreeVars(e1) ++ (FreeVars(e2) - idn)
  // TODO remaining cases
}


def ClConv(e: SExp) : SExp = e match {
  // Trivial cases
  case SIdn(idn) => ClConv(e)
  case SInt(i) => ClConv(e)
  case SBool(b) => ClConv(e)
  case SIf(e1, e2, e3) => SIf(ClConv(e1), ClConv(e2), ClConv(e3))
  case SLet(idn, e1, e2) => SLet(idn, ClConv(e1), ClConv(e2))
  case SDefine(name, params, e) => SDefine(name, params, CLConv(e))

  //case SLambda(params, e) => {
  //  val env = fresh("env")

  //case SAppl(proc, es) => {
  //}

}





//TODO better option handling

if (args.length == 0) {
  // TODO more helpful usage
  println("try <cmd> --cps")
  System.exit(-1)
}

args(0) match {
  case "--cps" => {
    val progTree = JLispParsers.parseExpr(args(1))
    val progCps = CPS(progTree, (x: SExp) => SAppl(SIdn("display"), List(x)))
    println(progCps.toString())
  }
  case _ => println("Wrong argument: " + args(0))
}
