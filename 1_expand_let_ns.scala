package ljsp

import ljsp.AST._

object expand_let_ns {
  def expand_let_ns_prog(p: SProgram) = {
    expand_let_ns(p).asInstanceOf[SProgram]
  }

  def expand_let_ns(e: SExp) : SExp = e match {
    case SProgram(ds, e) => SProgram(ds.map{d => expand_let_ns(d).asInstanceOf[SDefine]}, expand_let_ns(e))

    // Leaf nodes
    case SIdn(_) | SDouble(_) => e

    case SIf(e1, e2, e3) => SIf(expand_let_ns(e1), expand_let_ns(e2), expand_let_ns(e3))
    case SLambda(params, e) => SLambda(params, expand_let_ns(e))
    case SDefine(name, params, e) => SDefine(name, params, expand_let_ns(e))
    case SAppl(proc, es) => SAppl(proc, es.map{expand_let_ns})
    case SApplPrimitive(proc, es) => SApplPrimitive(proc, es.map{expand_let_ns})
    case SLetN(idnsAndE1s, e2) => {
      idnsAndE1s.foldRight(expand_let_ns(e2)){case (idnAndE1, _e2) => SLet(idnAndE1.idn, idnAndE1.e, _e2)}
    }
  }
}
