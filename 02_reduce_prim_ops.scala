package ljsp

import ljsp.AST._

// TODO remove neg cases from code further down the stream
object reduce_prim_ops {
  def reduce_prim_ops_prog(p: SProgram) = {
    reduce_prim_ops(p).asInstanceOf[SProgram]
  }

  def reduce_prim_ops(e: SExp) : SExp = e match {
    case SProgram(ds, e) => SProgram(ds.map{d => reduce_prim_ops(d).asInstanceOf[SDefine]}, reduce_prim_ops(e))

    case SIdn(_) | SDouble(_) => e

    case SIf(e1, e2, e3) => {
      e1 match {
        // TODO This only works if the primitive operation ("not, "and", "or", etc.) is the first expression
        //      of an if expression, not if the result of a boolean expression is assigned to a variable.
        //      The code in the SApplPrimitive case below throws an exception if the user tries to do that.

        // reduce (if (>= a1 a2) t f) to (if (not (< a1 a2)) t f)
        case SApplPrimitive(SIdn(">="), es) => {
          reduce_prim_ops(SIf(SApplPrimitive(SIdn("not"), SApplPrimitive(SIdn("<"), es) :: Nil), reduce_prim_ops(e2), reduce_prim_ops(e3)))
        }

        // reduce (if (<= a1 a2) t f) to (if (not (> a1 a2)) t f)
        case SApplPrimitive(SIdn("<="), es) => {
          reduce_prim_ops(SIf(SApplPrimitive(SIdn("not"), SApplPrimitive(SIdn(">"), es) :: Nil), reduce_prim_ops(e2), reduce_prim_ops(e3)))
        }

        // reduce (if (not b) t f) to (if b f t)
        case SApplPrimitive(SIdn("not"), es) => reduce_prim_ops(SIf(es(0).asInstanceOf[SApplPrimitive], e3, e2))
        // reduce (if (and b1 b2) t f) to (if b1 (if b2 t f) f)
        case SApplPrimitive(SIdn("and"), es) => {
          val b1 = es(0).asInstanceOf[SApplPrimitive]
          val b2 = es(1).asInstanceOf[SApplPrimitive]

          reduce_prim_ops(SIf(b1, SIf(b2, e2, e3), e3))
        }
        // reduce (if (or b1 b2) t f) to (if b1 t (if b2 t f))
        case SApplPrimitive(SIdn("or"), es) => {
          val b1 = es(0).asInstanceOf[SApplPrimitive]
          val b2 = es(1).asInstanceOf[SApplPrimitive]

          reduce_prim_ops(SIf(b1, e2, SIf(b2, e2, e3)))
        }
        
        case _ => SIf(reduce_prim_ops(e1), reduce_prim_ops(e2), reduce_prim_ops(e3))
      }
    }
    case SLambda(params, e) => SLambda(params, reduce_prim_ops(e))
    case SDefine(name, params, e) => SDefine(name, params, reduce_prim_ops(e))
    case SAppl(proc, es) => SAppl(proc, es.map{reduce_prim_ops})
    case SApplPrimitive(proc, es) => e match {
      case SApplPrimitive(SIdn("neg"), e :: Nil) => SApplPrimitive(SIdn("-"), SDouble(0) :: reduce_prim_ops(e) :: Nil)
      case SApplPrimitive(SIdn(">="), _) => throw new IllegalArgumentException("Complex boolean expressions can not be assigned to variables")
      case SApplPrimitive(SIdn("<="), _) => throw new IllegalArgumentException("Complex boolean expressions can not be assigned to variables")
      case SApplPrimitive(SIdn("and"), _) => throw new IllegalArgumentException("Complex boolean expressions can not be assigned to variables")
      case SApplPrimitive(SIdn("or"), _) => throw new IllegalArgumentException("Complex boolean expressions can not be assigned to variables")
      case SApplPrimitive(proc, es) => SApplPrimitive(proc, es.map{reduce_prim_ops})
      case _ => throw new IllegalArgumentException()
    }
    case SLet(idn, e1, e2) => SLet(idn, reduce_prim_ops(e1), reduce_prim_ops(e2))
  }
}


// >=    not <
