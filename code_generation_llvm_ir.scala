package ljsp

import ljsp.AST._

object code_generation_llvm_ir {
  
  def llvm_ir_module_to_string(m: LModule) : String = {
    "declare i8* @malloc(i64)\n" +
    "declare i32 @printf(i8*, ...)\n" +
    // main function
    """
    @.str = private unnamed_addr constant [4 x i8] c"%f\0A\00"
    define i32 @main(i32 %argc, i8** %argv) {
      %1 = alloca i32
      %2 = alloca i8**
      %r = alloca double*
      store i32 %argc, i32* %1
      store i8** %argv, i8*** %2
      %3 = call i8* @expression()
      %4 = bitcast i8* %3 to double*
      store double* %4, double** %r
      %5 = load double** %r
      %6 = load double* %5
      %7 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([4 x i8]* @.str, i32 0, i32 0), double %6)
      ret i32 0
    }
    """ +
    "\n" +
    m.functions.map{llvm_ir_function_to_string}.mkString("\n\n")
  }

  def llvm_ir_function_to_string(f: LFunction) : String = {
    "define i8* @" + f.name + "(" + f.params.map{p => "i8* %" + p}.mkString(", ") + ") {\n" +
    f.statements.map{llvm_ir_statement_to_string}.mkString("\n") + "\n" + "}\n"
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
    case LStoreDouble(d, v) => {
      "store double " + "%.7f".format(d) + ", double* %" + v
    }

    case LLabel(l) => l + ":"

    case LUnconditionalBr(l) => "br label %" + l
    case LConditionalBr(br_var, l_true, l_false) => "br i1 %" + br_var + ", label %" + l_true + ", label %" + l_false
 

    case LRet(t, v) => "ret " + llvm_ir_type_to_string(t) + " %" + v
    case _ => llvm_ir_expression_to_string(s.asInstanceOf[LExp])
  }

  def llvm_ir_expression_to_string(e: LExp) : String = e match {
    // TODO rename this to LIdn
    case LVarAccess(t, v) => "%" + v
    case LStaticValue(d) => "%.7f".format(d)
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
    case LCallFPointer(f_pointer, params) => {
      "call i8* %" + f_pointer + "(" + params.map{p => "i8* %" + p}.mkString(", ") + ")"
    }
    case LCallFName(f_name, params) => {
      "call i8* @" + f_name + "(" + params.map{p => "i8* %" + p}.mkString(", ") + ")"
    }

    case LMalloc(bytes) => {
      "call i8* @malloc(i64 " + bytes.toString + ")"
    }

    case LPrimitiveInstruction(op, ls) => {
      def op_to_llvm_ir_instruction(op: String) : String = op match {
        case "+" => "fadd"
        case "-" => "fsub"
        case "*" => "fmul"
        case "/" => "fdiv"

        case "<" => "fcmp olt"
        case ">" => "fcmp ogt"
      }
      op match {
        case "+" | "-" | "*" | "/" => {
          op_to_llvm_ir_instruction(op) + " double " + 
          ls.map{llvm_ir_expression_to_string}.mkString(", ")
        }
        case "<" | ">" => {
          op_to_llvm_ir_instruction(op) + " double " +
          ls.map{llvm_ir_expression_to_string}.mkString(", ")
        }
      }
    }

    case LZext(v) => "zext i1 %" + v + " to i32"

    case LIcmpNe(v) => "icmp ne i32 %" + v + ", 0"

    case LPhi(bs) => {
      "phi double " + 
      bs.map{b => "[ " + llvm_ir_expression_to_string(b._1) + ", %" + b._2 + " ]"}.mkString(", ")
  }

    case _ => "### TODO"
  }
}


