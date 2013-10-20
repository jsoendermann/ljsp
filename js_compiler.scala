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

case class SVar(x: Idn)

abstract class SCon
case class SInt(i: Int) extends SCon
case class SDouble(d: Double) extends SCon
case class SBool(b: Boolean) extends SCon

abstract class SPrim
case class SPrimPlus extends SPrim
case class SPrimMinus extends SPrim
case class SPrimMult extends SPrim

abstract class SForm
abstract class SExp extends SForm
case class SVarExp(x: SVar) extends SExp
case class SConExp(i: SCon) extends SExp
case class SIfExp(t: SExp, e1: SExp, e2: SExp) extends SExp
case class SProcExp(vs: List[SVar], e: SExp) extends SExp
case class SPrimExp(p: SPrim, e1: SExp, e2: SExp) extends SExp
case class SAppExp(e1: SExp, e2: SExp) extends SExp



