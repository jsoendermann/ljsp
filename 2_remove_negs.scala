package ljsp

import ljsp.AST._

// TODO remove neg cases from code further down the stream
object remove_negs {
  def remove_negs_prog(p: SProgram) = {
    remove_negs(p).asInstanceOf[SProgram]
  }

  def remove_negs(e: SExp) : SExp = e match {
    case SProgram(ds, e) => SProgram(ds.map{d => remove_negs(d).asInstanceOf[SDefine]}, remove_negs(e))

    case SIdn(_) | SDouble(_) => e

    case SIf(e1, e2, e3) => SIf(remove_negs(e1), remove_negs(e2), remove_negs(e3))
    case SLambda(params, e) => SLambda(params, remove_negs(e))
    case SDefine(name, params, e) => SDefine(name, params, remove_negs(e))
    case SAppl(proc, es) => SAppl(proc, es.map{remove_negs})
    case SApplPrimitive(proc, es) => e match {
      // TODO reduce a >= b to !(a<b) etc.
      case SApplPrimitive(SIdn("neg"), e :: Nil) => SApplPrimitive(SIdn("-"), SDouble(0) :: remove_negs(e) :: Nil)
      case SApplPrimitive(proc, es) => SApplPrimitive(proc, es.map{remove_negs})
      case _ => throw new IllegalArgumentException()
    }
    case SLet(idn, e1, e2) => SLet(idn, remove_negs(e1), remove_negs(e2))
  }
}
