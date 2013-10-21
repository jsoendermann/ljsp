import scala.util.parsing.combinator._

//type Num = Int
type Idn = String



// TODO test case for later: ((if #f + *) 3 4)



// ################ Classes ####################



// TODO define
// TODO set!
abstract class SExp
case class SIdn(idn: Idn) extends SExp { override def toString = idn }
case class SInt(i: Int) extends SExp { override def toString = i.toString() }
case class SBool(b: Boolean) extends SExp { override def toString = if (b) "#t" else "#f" }
// TODO double
// TODO symbols
case class SIf(e1: SExp, e2: SExp, e3: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + " " + e3.toString() + ")" }
case class SLambda(idns: List[SIdn], e: SExp) extends SExp { override def toString = "(lambda (" + idns.mkString(" ") + ") " + e.toString() + ")" }
// TODO begin
case class SAppl(proc: SExp, es: List[SExp]) extends SExp { override def toString = "(" + proc.toString() + " " + es.mkString(" ") + ")"}
case class SLet(idn: SIdn, e1: SExp, e2: SExp) extends SExp { override def toString = "(let ((" + idn.toString() + " " + e1.toString() + ")) " + e2.toString() + ")" }
// TODO more than one variable let
case class SHalt(e: SExp) extends SExp { override def toString = "(halt " + e.toString() + ")" }



// fresh returns a new unique identifier
val fresh = (() =>  {
   var counter = -1
   () => {
      counter +=1
      SIdn("nv" ++ "_" ++ counter.toString())
   }
})()



// ################ Parser ####################



object JLispParsers extends JavaTokenParsers {
  def identifier: Parser[SIdn] = """[a-zA-Z=*+/<>!\?\-][a-zA-Z0-9=*+/<>!\?\-]*""".r ^^ (SIdn(_))
  def integer: Parser[SInt] = wholeNumber ^^ (i => SInt(i.toInt))
  def boolean: Parser[SBool] = ("#t" | "#f") ^^ {
    case "#t" => SBool(true)
    case "#f" => SBool(false)
  }

  def _if: Parser[SIf] = "("~>"if"~>expression~expression~expression<~')' ^^ {
    case e1~e2~e3 => SIf(e1, e2, e3)
  }
    
  def lambda: Parser[SLambda] = "("~>"lambda"~>"("~>rep1(identifier, identifier)~")"~expression<~")" ^^ {
    case idns~")"~e => SLambda(idns, e)
  }
  def application: Parser[SAppl] = "("~>expression~rep1(expression, expression)<~")" ^^ {
    case proc~es => SAppl(proc, es)
  }
  def let: Parser[SLet] = "("~>"let"~>"("~"("~>identifier~expression~")"~")"~expression<~")" ^^ {
    case idn~e1~")"~")"~e2 => SLet(idn, e1, e1)
  }
  def halt: Parser[SHalt] = "("~>"halt"~>expression<~")" ^^ (e => SHalt(e))

  def expression: Parser[SExp] = identifier | integer | boolean | _if | lambda | application | let | halt

  // TODO this throws an exception instead of printing a nice error message when the input isn't well formed
  def parseExpr(str: String) = parse(expression, str).get
}

//def parseString(s: String) : SExp = JLispParsers.parseExpr(s)

// ################ CPS Translation ####################



def CPS(e: SExp, k: SExp => SExp) : SExp = e match {
  // For atomic values, no CPS-Translation is necessary. Simply apply k to e
  case SIdn(idn) => k(e)
  case SInt(i) => k(e)
  case SBool(b) => k(e)

  // For if, evaluate e1 first, then branch with two recursive calls
  case SIf(e1, e2, e3) => {
    CPS(e1, (ce1: SExp) =>
        SIf(ce1, CPS(e2, k), CPS(e3, k)))
  }

  // TODO lambda
  /*case SLambda(idns, e) => {
    val f = fresh()
    k(SLambda(idns ::: List(f), CPSTail(e, f)))
  }*/

  // For applications, evaluate proc first, then all the arguments
  case SAppl(proc, es) => {
    // the result of the application will be assigned to this variable
    // and k will be called with f as its parameter
    val f = fresh()

    // CPS-Translation the expression for the procedure first
    CPS(proc, (cproc: SExp) => {

      // this function calls CPS recursively for all expressions in es and accumulates the result in ces
      // the last call with an empty es creates a let and calls k on the result of the application
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SLet(f, SAppl(cproc, ces), k(f))
        case (e::es) => CPS(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
    aux(es, Nil)
    })
  }

  // TODO let

  // For halt, evaluate the expression to halt with
  // TODO maybe there is a better way to do this?
  case SHalt(e) => {
    val f = fresh()
    CPS(e, (x: SExp) =>
        SLet(f, x, k(SHalt(f))))
  }
}

/*def CPSTail(e: SExp, c: SExp) : SExp = e match {
  // For atomic values, no CPS-Translation is necessary. Simply apply k to e
  case SIdn(idn) => SAppl(c, List(e))
  case SInt(i) => SAppl(c, List(e))
  case SBool(b) => SAppl(c, List(e))

  // For if, evaluate e1 first, then branch with two recursive calls
  case SIf(e1, e2, e3) => {
    CPS(e1, (ce1: SExp) =>
        SIf(ce1, CPSTail(e2, c), CPSTail(e3, c)))
  }

  // TODO lambda
  //case SLambda(idns, e) => {
  //  val f = fresh()
  //  k(SLambda(idns ::: List(f), CPSTail(e, f)))
  //}

  // For applications, evaluate proc first, then all the arguments
  case SAppl(proc, es) => {
    // the result of the application will be assigned to this variable
    // and k will be called with f as its parameter
    val f = fresh()

    // CPS-Translation the expression for the procedure first
    CPS(proc, (cproc: SExp) => {

      // this function calls CPS recursively for all expressions in es and accumulates the result in ces
      // the last call with an empty es creates a let and calls k on the result of the application
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SLet(f, SAppl(cproc, ces), SAppl(c, List(f)))
        case (e::es) => CPS(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
    aux(es, Nil)
    })
  }

  // TODO let

  // For halt, evaluate the expression to halt with
  // TODO maybe there is a better way to do this?
  case SHalt(e) => {
    val f = fresh()
    CPS(e, (x: SExp) =>
        SLet(f, x, SAppl(c, List(f))))
  }
}*/



// ################ Simple Tests ####################

val prog3 = JLispParsers.parseExpr("(if #t 1 2)")
val prog3cps = CPS(prog3, (x: SExp) => SHalt(x))

println(prog3.toString())
println(prog3cps.toString())

/*
val prog0string = "(if0 ((lambda (x) (+ x 1)) 2) 1 0)"
val prog0tree = JLispParsers.parseExpr(prog0string)

val prog1string = "(let ((x 2)) (+ x 3))"
val prog1tree = JLispParsers.parseExpr(prog1string)

println(prog0string)
println(prog0tree.toString())

println(prog1string)
println(prog1tree.toString())

*/
