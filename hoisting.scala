package ljsp

import ljsp.AST._
import ljsp.util._

object hoisting {
  def hoist_prog(p: SProgram) : SProgram = {
    val hp = hoist(p)
    val casted_hp_e : SProgram = hp.e.asInstanceOf[SProgram]
    SProgram(hp.new_defs ::: casted_hp_e.ds, casted_hp_e.e)
  }

  def hoist(e: SExp) : HoistedExpression = e match {
    case SProgram(ds, e) => {
      val hds = ds.map{hoist}
      val he = hoist(e)
      HoistedExpression(SProgram(hds.map{hd => hd.e.asInstanceOf[SDefine]}, he.e), hds.flatMap{hd => hd.new_defs} ::: he.new_defs)
    }
    case SDefine(name, params, e) => {
      val he = hoist(e)
      HoistedExpression(SDefine(name, params, he.e), he.new_defs)
    }
    case SIf(e1, e2, e3) => {
      val he1 = hoist(e1)
      val he2 = hoist(e2)
      val he3 = hoist(e3)
      HoistedExpression(SIf(he1.e, he2.e, he3.e), he1.new_defs ::: he2.new_defs ::: he3.new_defs)
    }
    case SLet(idn, e1, e2) => {
      val he1 = hoist(e1)
      val he2 = hoist(e2)
      HoistedExpression(SLet(idn, he1.e, he2.e), he1.new_defs ::: he2.new_defs)
    }
    // This deals with calls to closure converted lambdas. 
    // As they have been hoisted in this step, they no longer need special treatment
    case SAppl(SGetProc(e), es) => {
      val he = hoist(e)
      val hes = es.map{hoist}

      HoistedExpression(SAppl(he.e, hes.map{he => he.e}.tail), he.new_defs ::: hes.flatMap{he => he.new_defs})
    }
    case SAppl(proc, es) => {
      val h_proc = hoist(proc)
      val hes = es.map{hoist}
      HoistedExpression(SAppl(h_proc.e, hes.map{he => he.e}), h_proc.new_defs ::: hes.flatMap{hd => hd.new_defs})

    }
    case SApplPrimitive(proc, es) => {
      val hes = es.map{hoist}
      HoistedExpression(SApplPrimitive(proc, hes.map{he => he.e}), hes.flatMap{hd => hd.new_defs})
    }
    case SMakeLambda(l, env) => {
      val f = fresh("func")
      val hl = hoist(l)
      val casted_hl_e = hl.e.asInstanceOf[SLambda]

      HoistedExpression(SHoistedLambda(f, env), SDefine(f, casted_hl_e.params, casted_hl_e.e) :: hl.new_defs)
    }
    case SLambda(params, e) => {
      val he = hoist(e)
      HoistedExpression(SLambda(params, he.e), he.new_defs)
    }
    case _ => {
      //println("Ignoring " + e.toString)
      HoistedExpression(e, Nil)
    }
  }
}
