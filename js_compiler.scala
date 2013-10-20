//type Num = Int
type Idn = String


// this anonymous function returns a function that turns Idn's
// into unique Strings
val fresh = (() =>  {
   var counter = -1
   (i: Idn) => {
      counter +=1
      i ++ "_" ++ counter.toString()
   }
})()


abstract class SAtom
case class SVar(x: Idn) extends SAtom { override def toString = x }
abstract class SCon extends SAtom
case class SInt(i: Int) extends SCon { override def toString = i.toString() }
// TODO double
// case class SBool(b: Boolean) extends SCon { override def toString =  if (b) "#t" else "#f" }
// TODO symbols

// TODO define
abstract class SExp // extends SForm
case class SVarExp(x: SVar) extends SExp { override def toString = x.toString() }
case class SConExp(c: SCon) extends SExp { override def toString = c.toString() }
//case class SIfExp(t: SExp, e1: SExp, e2: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + ")" }
case class SIf0Exp(t: SExp, e1: SExp, e2: SExp) extends SExp { override def toString = "(if0 " + e1.toString() + " " + e2.toString() + ")" }
case class SLambdaExp(v: SVar, e: SExp) extends SExp { override def toString = "(lambda (" + v.toString() + ") " + e.toString() + ")" }
// TODO multiple variables lambda
// TODO begin
// TODO let
case class SPrimExp(p: SPrim, e1: SExp, e2: SExp) extends SExp { override def toString = "(" + p.toString() + " " + e1.toString() + " " +  e2.toString() + ")" }
// TODO more than two expressions
case class SApplExp(e1: SExp, e2: SExp) extends SExp { override def toString = "(" + e1.toString() + " " + e2.toString() + ")"}

abstract class SList(es: List[SExp]) extends SExp { override def toString = "(" + es.mkString(" ") + ")" }

abstract class SPrim
case class SPrimPlus() extends SPrim { override def toString = "+" }
case class SPrimMinus() extends SPrim { override def toString = "-" }
case class SPrimMult() extends SPrim { override def toString = "*" }
// TODO boolean operators



val prog0 = SPrimExp(SPrimPlus(), SConExp(SInt(1)), SConExp(SInt(2)))
val prog1 = SApplExp(SLambdaExp(SVar("x"), SPrimExp(SPrimMult(), SConExp(SInt(1)), SVarExp(SVar("x")))), SConExp(SInt(3)))

println(prog0.toString())
println(prog1.toString())
