package ljsp

import ljsp.AST._
import ljsp.util._

object ir_conversion {
  def convert_prog_to_ir(p: SProgram) : IModule = {
    IModule("ModuleName", convert_define_to_ir(p, SDefine(SIdn("expression"), Nil, p.e)) :: p.ds.map{d => convert_define_to_ir(p, d)})
  }

  def convert_define_to_ir(p: SProgram, d: SDefine) : IFunction = {
    IFunction(d.name.idn, d.params.map{i => i.idn}, convert_sexp_to_ir_statement(p, d.e))
  }

  def convert_sexp_to_ir_statement(p: SProgram, e: SExp) : List[IStatement] = e match {
    case SAppl(SHoistedLambda(f, SMakeEnv(idns)), es) => throw new IllegalArgumentException("nexted hl appl")

    case SIdn(_) | SAppl(_, _) => List(convert_sexp_to_ir_exp(p, e))

    case SLet(i, SMakeEnv(idns), e2) => {
      IVarAssignment(i.idn, IMakeEnv(idns.map{i => i.idn})) :: 
      convert_sexp_to_ir_statement(p, e2)
    }

    case SLet(i, SHoistedLambda(f, env), e2) => {
      IVarAssignment(i.idn, IHoistedLambda(f.idn, convert_sexp_to_ir_exp(p, env))) ::
      convert_sexp_to_ir_statement(p, e2)
    }

    case SLet(i, e1, e2) => {
      IVarAssignment(i.idn, convert_sexp_to_ir_exp(p, e1)) ::
      convert_sexp_to_ir_statement(p, e2)
    }

    case SIf(e1, e2, e3) => {
      val if_var = fresh("if_var")
      IVarAssignment(if_var, convert_sexp_to_ir_exp(p, e1)) ::
      IIf(if_var, convert_sexp_to_ir_statement(p, e2), convert_sexp_to_ir_statement(p, e3)) ::
      Nil
    }

  }

  def convert_sexp_to_ir_exp(p: SProgram, e: SExp) : IExp = e match {
    case SIdn(i) => IIdn(i)
    case SDouble(d) => IStaticValue(d)

    case SAppl(proc, es) => {
      // Two cases for function calls by name or by index
      if (proc.isInstanceOf[SIdn] && p.ds.foldLeft(false) {(v, d) => v || d.name == proc}) 
        IFunctionCallByName(proc.asInstanceOf[SIdn].idn, es.map{e => convert_sexp_to_ir_exp(p, e)})
      else {
        IFunctionCallByVar(convert_sexp_to_ir_exp(p, proc), es.map{e => convert_sexp_to_ir_exp(p, e)})
      }
    }

    case SApplPrimitive(proc, es) => IPrimitiveInstruction(proc.idn, es.map{e => convert_sexp_to_ir_exp(p, e)})
    case SNth(n, e) => IArrayAccess(convert_sexp_to_ir_exp(p, e).asInstanceOf[IIdn].idn, n)
  }

}
