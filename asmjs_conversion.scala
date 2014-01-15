package ljsp

import ljsp.AST._
import ljsp.util._

object asmjs_conversion {
  //def convert_prog_to_asmjs(p: SProgram) : AModule = {
  //  println(p.ds.map{convert_expression_to_asmjs})
  //}



  def convert_define_to_asmjs(p: SProgram, d: SDefine) : AFunction = {
    val instructions = convert_instruction_to_asmjs(p, d.e)
    AFunction(d.name.idn, d.params.map{si => AIdn(si.idn)}, instructions)
  }

  def convert_instruction_to_asmjs(p: SProgram, e: SExp) : List[AExp] = e match {
    case SLet(i: SIdn, e1: SExp, e2: SExp) => {
      List(AVarAssignment(AIdn(i.idn), convert_value_to_asmjs(e1))) ++
      convert_instruction_to_asmjs(p, e2)
    }
    case SAppl(proc, es) => {
      // This checks if this call is to a function defined in the defines of the program
      // FIXME: This does not work if a function is being returned as result of a more complex expression
      if (proc.isInstanceOf[SIdn] && p.ds.foldLeft(false) {(v, d) => v || d.name == proc}) {
        List(AFunctionCallByName(AIdn(proc.asInstanceOf[SIdn].idn), es.map{e => convert_value_to_asmjs(e)}))
      } else {
        // TODO
        Nil
      }
    }

    case _ => Nil
  }

  def convert_value_to_asmjs(e: SExp) : AValue = e match {
    case SInt(i) => AStaticValue(i)
    case SNth(n, e) => AArrayAccess(AStaticValue(n), convert_value_to_asmjs(e))
    case SIdn(i) => AVarAccess(AIdn(i))
    // FIXME this only uses the first two operands. this should be fixed in the cps conv. function
    // a primitive application with more than two operands should be converted into several with two each
    case SApplPrimitive(proc, es) => APrimitiveInstruction(proc.idn, convert_value_to_asmjs(es(0)), convert_value_to_asmjs(es(1)))
    case SHoistedLambda(f, env) => ATODO
  }

}
