package ljsp

import ljsp.AST._

object code_generation_llvm_ir {
  
  def llvm_ir_module_to_string(m: LModule) : String = {
    // TODO main
    "declare i8* @malloc(i64)\n" +
    "\n" +
    // TODO function declaration
    //m.functions.map{f => "define i8* @" + f.name + "(" + f.params.map{p => "i8* %" + p}.mkString(", ") + ");"}.mkString("\n") + "\n" +
    m.functions.map{llvm_ir_function_to_string}.mkString("\n\n")
  }

  def llvm_ir_function_to_string(f: LFunction) : String = {
    f.name + "(" + f.params.mkString(", ") + ")\n" +
    f.statements.map{llvm_ir_statement_to_string}.mkString("\n") + "\n"
  }

  def llvm_ir_type_to_string(t: LType) : String = t match {
    case LTI8 => "i8"
    case LTInt => "i32"
    case LTDouble => "double"
    case LTFunctionPointer(num_params: Int) => "i8* (" + List.fill(num_params)("i8*").mkString(", ") + ")*"    

    case LTPointerTo(t: LType) => llvm_ir_type_to_string(t) + "*"
    case LTUnderlyingType(LTPointerTo(t)) => llvm_ir_type_to_string(t)
    // TODO nested underlying types

    case _ => throw new IllegalArgumentException("Unknown type") 
  }

  def llvm_ir_statement_to_string(s: LStatement) : String = s match {
    case LVarAssignment(v, e) => "%" + v + " = " + llvm_ir_expression_to_string(e)
    case LStore(t1, v1, t2, v2) => {
      "store " +
      llvm_ir_type_to_string(t1) + " %" + v1 + ", " +
      llvm_ir_type_to_string(t2) + " %" + v2
    }
    case LStoreFPointer(num_params, f_name, t2, v2) => {
      "store i8* bitcast (" + 
      llvm_ir_type_to_string(LTFunctionPointer(num_params)) + 
      "@" + f_name + " to i8*), " +
      llvm_ir_type_to_string(t2) + 
      "%" + v2
    }
    case LRet(t, v) => "ret " + llvm_ir_type_to_string(t) + " %" + v
    case _ => llvm_ir_expression_to_string(s.asInstanceOf[LExp])
  }

  def llvm_ir_expression_to_string(e: LExp) : String = e match {
    case LVarAccess(v, t) => llvm_ir_type_to_string(t) + " %" + v
    case LAlloca(t) => "alloca " + llvm_ir_type_to_string(t)
    case LLoad(t, v) => "load " + llvm_ir_type_to_string(t) + " %" + v
    case LBitCast(old_type, v, new_type) => {
      "bitcast " + llvm_ir_type_to_string(old_type) + " %" + v +
      " to " + llvm_ir_type_to_string(new_type)
    }
    case LGetElementPtr(t, av, index) => {
      "getelementptr inbounds " + 
      llvm_ir_type_to_string(t) + " %" +
      av + ", i64 " + index.toString
    }
    case LCall(f_pointer, params) => {
      "call i8* %" + f_pointer + "(" + params.map{p => "i8* %" + p}.mkString(", ") + ")"
    }
    case LMalloc(bytes) => {
      "call i8* @malloc(i64 " + bytes.toString + ")"
    }

    case _ => "### TODO"
  }
}


