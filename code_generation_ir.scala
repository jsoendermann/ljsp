package ljsp

import ljsp.AST._

object code_generation_ir {
  
  def ir_module_to_string(m: IModule) : String = {
    "IRModule " + m.name + ":\n\n" +
    m.functions.map{ir_function_to_string}.mkString("\n\n")
  }

  def ir_function_to_string(f: IFunction) : String = {
    "function " + f.name + "(" + f.params.mkString(", ") + ")" + "\n" +
    f.statements.map{ir_statement_to_string}.mkString("\n") + "\n" +
    "}"
  }

  def ir_statement_to_string(s: IStatement) : String = s match {
    case IIf(cond, block1, block2) => {
      "if (" + cond + ") {" + "\n" +
      block1.map{ir_statement_to_string}.mkString("\n") + "\n" +
      "} else {\n" + 
      block2.map{ir_statement_to_string}.mkString("\n") + "\n" +
      "}"
    }

    case IVarAssignment(idn, value) => idn + " = " + ir_exp_to_string(value)

    case _ => ir_exp_to_string(s.asInstanceOf[IExp])
  }

  def ir_exp_to_string(e: IExp) : String = e match {
    case IIdn(idn) => idn
    case IStaticValue(d) => d.toString()
    case IFunctionCallByName(f_name, params) => {
      f_name + "(" + params.map{ir_exp_to_string}.mkString(", ") + ")"
    }
    case IFunctionCallByVar(f_var, params) => {
      f_var + ".code" + 
      "(" + f_var + ".env" + ", " +
      params.map{ir_exp_to_string}.mkString(", ") + ")"
    }
    case IPrimitiveInstruction(op, is) => {
      if (is.size == 1) {
        op + " " + ir_exp_to_string(is(0))
      } else {
        op match {
          case "=" => ir_exp_to_string(is(0)) + " == " + ir_exp_to_string(is(1))
          case _ => ir_exp_to_string(is(0)) + " " + op + " " + ir_exp_to_string(is(1))
        }
      }
    }
    case IMakeEnv(idns) => "make-env(" + idns.mkString(", ") + ")"
    case IHoistedLambda(f_name, env) => {
      "make-hl(" + f_name + ", " + env + ")"
    }
    case IArrayAccess(a, index) => a + "[" + index.toString + "]"
  }
}
