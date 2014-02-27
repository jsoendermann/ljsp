package ljsp

import ljsp.AST._

object code_generation_c {
  
  def c_module_to_string(m: CModule) : String = {
    // TODO main
    "#include <stdio.h>\n" +
    "#include <stdlib.h>\n" +
    m.functions.map{f => "void *" + f.name + "(" + f.params.map{p => "void *" + p}.mkString(", ") + ");"}.mkString("\n") + "\n" +
    m.functions.map{c_function_to_string}.mkString("\n\n")
  }

  def c_function_to_string(f: CFunction) : String = {
    "void* " + f.name + "(" + f.params.map{p => "void *"+p}.mkString(", ") + ") {" + "\n" +
    f.declarations.map{c_statement_to_string}.mkString(";\n") + ";\n\n" +
    f.statements.map{c_statement_to_string}.mkString(";\n") + ";\n" +
    "}"
  }

  def c_type_to_string(t: CType) : String = t match {
    case CTInt => "int"
    case CTIntPointer => "int*"
    case CTDouble => "double"
    case CTDoublePointer => "double*"
    case CTVoidPointer => "void*"
    case CTVoidPointerPointer => "void**"
    case CTFunctionPointer(_) => throw new IllegalArgumentException("CTFunctionPointer has to be converted to string manually")
  }

  def c_statement_to_string(s: CStatement) : String = s match {
    case CDeclareVar(var_name, var_type) => {
      if (var_type.isInstanceOf[CTFunctionPointer]) {
        val num_params = var_type.asInstanceOf[CTFunctionPointer].num_params
        "void *(*" + var_name + ")(" + List.fill(num_params)("void*").mkString(",") + ");"
      } else {
        c_type_to_string(var_type) + " " + var_name
      }
    }

    case CVarAssignment(v, e) => v + " = " + c_exp_to_string(e)
    case CDereferencedVarAssignment(v, e) => "*" + v + " = " + c_exp_to_string(e)
    case CArrayAssignment(av, index, e) => {
      av + "[" + index.toString() + "] = " + c_exp_to_string(e)
    }
    case CIf(if_var, block1, block2) => {
      "if (" + if_var + ") {\n" +
      block1.map{c_statement_to_string}.mkString(";\n") + ";\n" +
      "} else {\n" + 
      block2.map{c_statement_to_string}.mkString(";\n") + ";\n" +
      "}"
    }
    case CReturn(s) => "return " + s
    case _ => c_exp_to_string(s.asInstanceOf[CExp])
  }

  def c_exp_to_string(e: CExp) : String = e match {
    case CIdn(idn) => idn
    case CStaticValue(d) => d.toString()
    case CMalloc(res_type, data_type, num) => {
      "(" + c_type_to_string(res_type) + ")malloc(sizeof(" +
      c_type_to_string(data_type) + ") * " + num.toString + ")"
    }
    case CPrimitiveInstruction(op, cs) => {
      // TODO add support for unary ops
      if (cs.size == 2) 
        c_exp_to_string(cs(0)) + op + c_exp_to_string(cs(1))
      else
        "TODO"
    }
    case CFunctionPointer(f_name) => "&" + f_name
    case CCast(v, t) => {
      if (t.isInstanceOf[CTFunctionPointer]) {
        val num_params = t.asInstanceOf[CTFunctionPointer].num_params
        "(void* (*)(" + List.fill(num_params)("void*").mkString(",") + "))" + v
      } else {
        "(" + c_type_to_string(t) + ")" + v
      }
    }
    case CDereferenceVar(v) => "*" + v
    case CArrayAccess(av, index) => av + "[" + index.toString() + "]"

    case CFunctionCallByName(f_name, params) => {
      f_name + "(" + params.map{c_exp_to_string}.mkString(", ") + ")"
    }
    case CFunctionCallByVar(hl_var, params) => {
      hl_var + "(" + params.mkString(", ") + ")"
    }


    case _ => "TODO"
  }
}

