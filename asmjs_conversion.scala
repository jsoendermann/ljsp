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
    case SLet(i, SMakeEnv(idns), e2) => {
      val num_idns = idns.size
      val ai = AIdn(i.idn)

      val setEnvValues: List[AExp] = idns.zipWithIndex.map{ case (idn, index) => AHeapAssignment(APrimitiveInstruction("+", AVarAccess(ai), AStaticValue(index)), AVarAccess(AIdn(idn.idn))) }

      List(AVarAssignment(ai, AAlloc(num_idns))) ++
      setEnvValues ++
      convert_instruction_to_asmjs(p, e2)
    }

    case SLet(i, SHoistedLambda(f, env), e2) => {
      val ai = AIdn(i.idn)


      List(AVarAssignment(ai, AAlloc(2)),
        AHeapAssignment(AVarAccess(ai), AStaticValue(-999)), // TODO: Look up index of function
        AHeapAssignment(APrimitiveInstruction("+", AVarAccess(ai), AStaticValue(1)), AVarAccess(AIdn(env.asInstanceOf[SIdn].idn)))) ++
      convert_instruction_to_asmjs(p, e2)
    
    }


    //  AVarAssignment(
    case SLet(i, e1, e2) => {
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
        List(ATODO)
      }
    }

    case _ => List(ATODO)
  }

  def convert_value_to_asmjs(e: SExp) : AValue = e match {
    case SInt(i) => AStaticValue(i)
    case SNth(n, e) => AArrayAccess(AStaticValue(n), convert_value_to_asmjs(e))
    case SIdn(i) => AVarAccess(AIdn(i))
    // FIXME this only uses the first two operands. this should be fixed in the cps conv. function
    // a primitive application with more than two operands should be converted into several with two each
    case SApplPrimitive(proc, es) => APrimitiveInstruction(proc.idn, convert_value_to_asmjs(es(0)), convert_value_to_asmjs(es(1)))
    /*case SHoistedLambda(f, SMakeEnv(idns)) => {
      val num_vars = idns.size

      AVarAssignment(AIdn("env_temp"), AAlloc(num_vars));
    }*/
    case _ => ATODO //TODO this shouldn't happen
  }

}
