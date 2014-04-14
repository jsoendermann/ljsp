package ljsp

import ljsp.AST._

object wrap_funcs_in_lambdas {
  def wrap_funcs_in_lambdas_prog(p: SProgram) = {
    wrap_funcs_in_lambdas(p, p).asInstanceOf[SProgram]
  }

  def wrap_funcs_in_lambdas(p: SProgram, e: SExp) : SExp = e match {
    case SProgram(ds, e) => {
      val defines = ds.map{d => wrap_funcs_in_lambdas(p, d).asInstanceOf[SDefine]}
      SProgram(defines, wrap_funcs_in_lambdas(p, e))
    }

    case SIdn(i) => {
      // Check if this is the name of a function
      val f = p.ds.filter{d => d.name == SIdn(i)}

      if (f.size == 0) 
        e
      else {
        val func = f(0)

        // Wrap the function in a lambda expression
        SLambda(func.params, SAppl(func.name, func.params))
      }

    }
    
    case SDouble(_) => e

    case SIf(e1, e2, e3) => {
      val fe1 = wrap_funcs_in_lambdas(p, e1)
      val fe2 = wrap_funcs_in_lambdas(p, e2)
      val fe3 = wrap_funcs_in_lambdas(p, e3)

      SIf(fe1, fe2, fe3)
    }

    case SLambda(params, e) => SLambda(params, wrap_funcs_in_lambdas(p, e))
    case SDefine(name, params, e) => SDefine(name, params, wrap_funcs_in_lambdas(p, e))
    case SAppl(proc, es) => SAppl(proc, es.map{wrap_funcs_in_lambdas(p, _)})
    case SApplPrimitive(proc, es) => SApplPrimitive(proc, es.map{wrap_funcs_in_lambdas(p, _)})
    case SLet(idn, e1, e2) => SLet(idn, wrap_funcs_in_lambdas(p, e1), wrap_funcs_in_lambdas(p, e2))
  }
}

