import scala.util.parsing.combinator._

//type Num = Int
type Idn = String


// this anonymous function returns a function that turns Idn's
// into unique Strings
val fresh = (() =>  {
   var counter = -1
   (i: Idn) => {
      counter +=1
      // TODO change or explain this
      i ++ "Z" ++ counter.toString()
   }
})()

// TODO test case for later: ((if #f + *) 3 4)



// ################ Classes ####################



// TODO define
abstract class SExp // extends SForm
case class SIdn(idn: Idn) extends SExp { override def toString = idn }
case class SInt(i: Int) extends SExp { override def toString = i.toString() }
// TODO double
// case class SBool(b: Boolean) extends SCon { override def toString =  if (b) "#t" else "#f" }
// TODO symbols
//case class SVar(x: SVar) extends SExp { override def toString = x.toString() }
//case class SCon(c: SCon) extends SExp { override def toString = c.toString() }
//case class SIfExp(t: SExp, e1: SExp, e2: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + ")" }
case class SIf0(e1: SExp, e2: SExp, e3: SExp) extends SExp { override def toString = "(if0 " + e1.toString() + " " + e2.toString() + " " + e3.toString() + ")" }
case class SLambda(idn: SIdn, e: SExp) extends SExp { override def toString = "(lambda (" + idn.toString() + ") " + e.toString() + ")" }
// TODO multiple variables lambda
// TODO begin
// TODO let
case class SPrim(p: Prim, e1: SExp, e2: SExp) extends SExp { override def toString = "(" + p.toString() + " " + e1.toString() + " " +  e2.toString() + ")" }
// TODO more than two expressions
case class SAppl(e1: SExp, e2: SExp) extends SExp { override def toString = "(" + e1.toString() + " " + e2.toString() + ")"}
case class SLet(idn: SIdn, e1: SExp, e2: SExp) extends SExp { override def toString = "(let (" + idn.toString() + " " + e1.toString() + ") " + e2.toString() + ")" }

//abstract class SList(es: List[SExp]) extends SExp { override def toString = "(" + es.mkString(" ") + ")" }

abstract class Prim
case class PrimPlus() extends Prim { override def toString = "+" }
case class PrimMinus() extends Prim { override def toString = "-" }
case class PrimMult() extends Prim { override def toString = "*" }
// TODO boolean operators



// ################ Parser ####################



// inspired by proginscala chapter 33 & http://stackoverflow.com/questions/4363871/parsing-scheme-using-scala-parser-combinators
object JLispParsers extends JavaTokenParsers {
  def primitive_op: Parser[Prim] = ("+" | "-" | "*") ^^ {
    case "+" => PrimPlus()
    case "-" => PrimMinus()
    case "*" => PrimMult()
  }

  def identifier: Parser[SIdn] = ident ^^ (SIdn(_))
  def integer: Parser[SInt] = wholeNumber ^^ (i => SInt(i.toInt))

  def if0: Parser[SIf0] = "("~>"if0"~>expression~expression~expression<~')' ^^ {
    case e1~e2~e3 => SIf0(e1, e2, e3)
  }
    
  def lambda: Parser[SLambda] = "("~>"lambda"~>"("~>identifier~")"~expression<~")" ^^ {
    case idn~")"~e => SLambda(idn, e)
  }
  def prim: Parser[SPrim] = "("~>primitive_op~expression~expression<~")" ^^ {
    case prim~e1~e2 => SPrim(prim, e1, e2)
  }
  def application: Parser[SAppl] = "("~>expression~expression<~")" ^^ {
    case e1~e2 => SAppl(e1, e2)
  }
  def let: Parser[SLet] = "("~>"let"~>"("~>identifier~expression~")"~expression<~")" ^^ {
    case idn~e1~")"~e2 => SLet(idn, e1, e1)
  }
  def expression: Parser[SExp] = identifier | integer | if0 | lambda | prim | application | let

  def parseExpr(str: String) = parse(expression, str)
}



// ################ Simple Tests ####################



val prog0string = "(if0 ((lambda (x) (+ x 1)) 2) 1 0)"
val prog0tree = JLispParsers.parseExpr(prog0string)

val prog1string = "(let (x 2) (+ x 3))"
val prog1tree = JLispParsers.parseExpr(prog1string)

println(prog0string)
println(prog0tree.toString())

println(prog1string)
println(prog1tree.toString())


