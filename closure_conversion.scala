package ljsp

import ljsp.AST._
import ljsp.util._

object closure_conversion {
  def free_vars(p: SProgram, e: SExp) : Set[Idn] = e match {
    case SDefine(name, params, e) => free_vars(p, e) -- Set(name.idn) -- params.map(sIdn => sIdn.idn)
    case SIdn(idn) => Set(idn)
    case SInt(_) => Set()
    case SIf(e1, e2, e3) => free_vars(p, e1) ++ free_vars(p, e2) ++ free_vars(p, e3)
    // idn.idn is necessary becase idn is of type SIdn but Idn is required
    case SLet(idn, e1, e2) => free_vars(p, e1) ++ (free_vars(p, e2) - idn.idn)
    case SLambda(params, e) => free_vars(p, e) -- params.map(sIdn => sIdn.idn)
    // If the proc is in the definitions block, it's not free
    // TODO is there a better way to give this case access to the name of definitions in the program
    // than to have p as an additional parameter to both free_vars and cl_conv?
    case SAppl(proc, es) => (free_vars(p, proc) -- p.ds.map{d => d.name.idn}) ++ es.flatMap{e => free_vars(p, e)}.toSet
    case SApplPrimitive(proc, es) => es.flatMap{e => free_vars(p, e)}.toSet
  }

  // this function only exists for typecasting, maybe there is a better way to do this
  def cl_conv_prog(p: SProgram) : SProgram = {
    cl_conv(p, p).asInstanceOf[SProgram]
  }

  def cl_conv(p: SProgram, e: SExp) : SExp = e match {
    // Trivial cases
    case SProgram(ds, e) => SProgram(ds.map{d => SDefine(d.name, d.params, cl_conv(p, d.e))}, cl_conv(p, e))
    case SDefine(name, params, e) => SDefine(name, params, cl_conv(p, e))
    case SIdn(idn) => e
    case SInt(i) => e
    case SIf(e1, e2, e3) => SIf(cl_conv(p, e1), cl_conv(p, e2), cl_conv(p, e3))
    case SLet(idn, e1, e2) => SLet(idn, cl_conv(p, e1), cl_conv(p, e2))

    case SLambda(params, e) => {
      // free vars
      val fvs = free_vars(p, SLambda(params, e)).toList

      val env = fresh("env")
      val fvs_with_index = fvs.zipWithIndex
      val e_with_fvs_bound = fvs_with_index.foldRight(cl_conv(p, e)) {
        case ((x, n), e) => SLet(SIdn(x), SNth(n, env), e)
      }

      SMakeLambda(SLambda(env :: params, e_with_fvs_bound), SMakeEnv(fvs.map{SIdn(_)}))
    }

    case SAppl(proc, es) => {
      // This checks if this call is to a function defined in the defines of the program
      // FIXME: This does not work if a function is being returned as result of a more complex expression
      if (proc.isInstanceOf[SIdn] && p.ds.foldLeft(false) {(v, d) => v || d.name == proc})
        SAppl(proc, es.map{e => cl_conv(p, e)})
      else {
        val converted_proc = cl_conv(p, proc)
        SAppl(SGetProc(converted_proc), SGetEnv(converted_proc) :: es.map{e => cl_conv(p, e)})
      }
    }

    case SApplPrimitive(proc, es) => SApplPrimitive(proc, es.map{e => cl_conv(p, e)})
  }
}
