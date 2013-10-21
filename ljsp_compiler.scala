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
// TODO double
// TODO bool
// TODO symbols
//case class SIfExp(t: SExp, e1: SExp, e2: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + ")" }
case class SIf0(e1: SExp, e2: SExp, e3: SExp) extends SExp { override def toString = "(if0 " + e1.toString() + " " + e2.toString() + " " + e3.toString() + ")" }
case class SLambda(idns: List[SIdn], e: SExp) extends SExp { override def toString = "(lambda (" + idns.mkString(" ") + ") " + e.toString() + ")" }
// TODO begin
case class SAppl(e1: SExp, e2: SExp) extends SExp { override def toString = "(" + e1.toString() + " " + e2.toString() + ")"}
// TODO more than two expressions
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
  def identifier: Parser[SIdn] = """[a-zA-Z=*+/<>!\?][a-zA-Z0-9=*+/<>!\?]*""".r ^^ (SIdn(_))
  def integer: Parser[SInt] = wholeNumber ^^ (i => SInt(i.toInt))

  def if0: Parser[SIf0] = "("~>"if0"~>expression~expression~expression<~')' ^^ {
    case e1~e2~e3 => SIf0(e1, e2, e3)
  }
    
  def lambda: Parser[SLambda] = "("~>"lambda"~>"("~>rep1(identifier, identifier)~")"~expression<~")" ^^ {
    case idns~")"~e => SLambda(idns, e)
  }
  def application: Parser[SAppl] = "("~>expression~expression<~")" ^^ {
    case e1~e2 => SAppl(e1, e2)
  }
  def let: Parser[SLet] = "("~>"let"~>"("~"("~>identifier~expression~")"~")"~expression<~")" ^^ {
    case idn~e1~")"~")"~e2 => SLet(idn, e1, e1)
  }
  def halt: Parser[SHalt] = "("~>"halt"~>expression<~")" ^^ (e => SHalt(e))

  def expression: Parser[SExp] = identifier | integer | if0 | lambda | application | let | halt

  def parseExpr(str: String) = parse(expression, str).get
}



// ################ CPS Translation ####################



def CPS(e: SExp, k: SExp => SExp) : SExp = e match {
  case SIdn(idn) => k(SIdn(idn))
  case SInt(i) => k(SInt(i))
  case SIf0(e1, e2, e3) => {
    CPS(e1, (x: SExp) =>
        SIf0(x, CPS(e2, k), CPS(e3, k)))
  }
  /*case SLambda(idns, e) => {
    val f = SIdn(fresh(""))
    k(SLambda(idns ::: List(f), CPS(e, f*/
  case SAppl(e1, e2) => {
    val f = fresh()
    CPS(e1, (x: SExp) =>
        CPS(e2, (y: SExp) =>
            SLet(f, SAppl(x, y), k(f))))
  }
  /*case SLet(idn, e1, e2) => {
    val f1 = SIdn(fresh(""))
    val f2 = SIdn(fresh(""))
    CPS(e1, (x: SExp) =>
        CPS(e2, (y: SExp) =>
          SLet(f1, x, SLet(f2, y, SLet(idn, */
  case SHalt(e) => {
    val f = fresh()
    CPS(e, (x: SExp) =>
        SLet(f, x, SHalt(f)))
  }
}



// ################ Simple Tests ####################

val prog3 = JLispParsers.parseExpr("(+ 2 )")
//val prog3cps = CPS(prog3, (x: SExp) => SHalt(x))

println(prog3.toString())

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
