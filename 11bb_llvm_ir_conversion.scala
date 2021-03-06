package ljsp

import ljsp.AST._
import ljsp.util._

object llvm_ir_conversion {
  def convert_module_to_llvm_ir(m: CModule) : LModule = {
    LModule(m.name, m.functions.map{f => convert_function_to_llvm_ir(f, m)})
  }

  def convert_function_to_llvm_ir(f: CFunction, m: CModule) : LFunction = {
    var new_params = List[String]()
    var new_params_decls = List[LVarAssignment]()
    var store_params = List[LStore]()

    f.params.map{p => {
      val new_p = p + "_p"
      new_params = new_params :+ new_p
      new_params_decls = new_params_decls :+ LVarAssignment(p, LAlloca(LTI8Pointer))
      store_params = store_params :+ LStore(LTI8Pointer, new_p, LTI8PointerPointer, p)
    }}

    val declarations = new_params_decls ++ f.declarations.map{convert_declaration_to_llvm_ir}
    val statements = store_params ++ f.statements.map{s => convert_statement_to_llvm_ir(s, m, declarations)}.flatten

    val numbered_vars_statements = statements

    val f_body = declarations ++ numbered_vars_statements
    LFunction(f.name, new_params, f_body)
  }

  def convert_declaration_to_llvm_ir(d: CDeclareVar) : LVarAssignment = {
    LVarAssignment(d.var_name, LAlloca(convert_c_type_to_llvm_ir_type(d.var_type)))
  }

  def convert_c_type_to_llvm_ir_type(ct: CType) : LType = ct match {
    case CTInt => LTInt
    case CTIntPointer => LTIntPointer
    case CTDouble => LTDouble
    case CTDoublePointer => LTDoublePointer
    case CTDoublePointerPointer => LTDoublePointerPointerPointer
    case CTVoidPointer => LTI8Pointer
    case CTVoidPointerPointer => LTI8PointerPointer
    case CTFunctionPointer(num_params) => LTFunctionPointer(num_params)
  }

  def convert_statement_to_llvm_ir(s: CStatement, m: CModule, declarations: List[LVarAssignment]) : List[LStatement] = s match {
    case CIf(if_var, block1, block2) => {
      val sts1 = block1.map{s => convert_statement_to_llvm_ir(s, m, declarations)}.flatten
      val sts2 = block2.map{s => convert_statement_to_llvm_ir(s, m, declarations)}.flatten

      val label_true = fresh("label_true")
      val label_false = fresh("label_false")
      val label_end = fresh("label_end")

      val llvm_ir_if_var = fresh("llvm_ir_if_var")
      val bool_var = fresh("bool_var")

      (LVarAssignment(llvm_ir_if_var, LLoad(LTPointerTo(LTInt), if_var)) ::
        LVarAssignment(bool_var, LIcmpNe(llvm_ir_if_var)) ::
        LConditionalBr(bool_var, label_true, label_false) ::
        LLabel(label_true) :: Nil) ++
      sts1 ++
      (LUnconditionalBr(label_end) ::
        LLabel(label_false) :: Nil) ++
      sts2 ++
      (LUnconditionalBr(label_end) ::
        LLabel(label_end) :: Nil)
    }

    case CVarAssignment(lh_v, CIdn(rh_v)) => {
      val direct_assign = fresh("direct_assign")
      val lh_t = get_var_type(lh_v, declarations)
      val rh_t = get_var_type(rh_v, declarations)
      if (lh_t == rh_t) {
        LVarAssignment(direct_assign, LLoad(LTPointerTo(rh_t), rh_v)) ::
        LStore(lh_t, direct_assign, LTPointerTo(lh_t), lh_v) :: Nil
      } else {
        val direct_assign_casted = fresh("direct_assign_casted")
        LVarAssignment(direct_assign, LLoad(LTPointerTo(rh_t), rh_v)) ::
        LVarAssignment(direct_assign_casted, LBitCast(rh_t, direct_assign, lh_t)) ::
        LStore(lh_t, direct_assign_casted, LTPointerTo(lh_t), lh_v) :: Nil
      }
    }
    case CVarAssignment(v, CCast(rh_v, t)) => {
      val type_org = get_var_type(rh_v, declarations)
      val type_new = convert_c_type_to_llvm_ir_type(t)
      val a = fresh("cast_a")
      val b = fresh("cast_b")
      LVarAssignment(a, LLoad(LTPointerTo(type_org), rh_v)) ::
      LVarAssignment(b, LBitCast(type_org, a, type_new)) ::
      LStore(type_new, b, LTPointerTo(type_new), v) :: Nil
    }

    case CVarAssignment(lh_v, CArrayAccess(av, index)) => {
      val type_lh_v = get_var_type(lh_v, declarations)
      val type_a = get_var_type(av, declarations)
      val a = fresh("arr_get_a")
      val b = fresh("arr_get_b")
      val c = fresh("arr_get_c")
      LVarAssignment(a, LLoad(LTPointerTo(type_a), av)) ::
      LVarAssignment(b, LGetElementPtr(type_a, a, index)) ::
      LVarAssignment(c, LLoad(LTPointerTo(type_lh_v), b)) ::
      LStore(LTUnderlyingType(type_a), c, LTPointerTo(type_lh_v), lh_v) :: Nil
    }

    case CVarAssignment(lh_v, CFunctionCallByName(f_name, params)) => {
      var param_vars = List[Idn]()
      var load_params = List[LVarAssignment]()
      params.map{p => {
        val type_p = get_var_type(p, declarations)
        val uncast_param_var = fresh("uncast_param_var")
        val param_var = fresh("fv_param")
        param_vars = param_vars :+ param_var
        load_params = load_params ++ 
        (LVarAssignment(uncast_param_var, LLoad(LTPointerTo(type_p), p)) ::
          LVarAssignment(param_var, LBitCast(type_p, uncast_param_var, LTI8Pointer)) :: Nil)
      }}

      val fn_res = fresh("fn_res")

      load_params ++
      (LVarAssignment(fn_res, LCallFName(f_name, param_vars)) ::
        LStore(LTI8Pointer, fn_res, LTI8PointerPointer, lh_v) :: Nil)
    }


    case CVarAssignment(lh_v, CFunctionCallByVar(f_var, params)) => {
      val type_f = get_var_type(f_var, declarations)
      val f_pointer = fresh("f_pointer")

      val load_f_pointer = LVarAssignment(f_pointer, LLoad(LTPointerTo(type_f), f_var))

      var param_vars = List[Idn]()
      var load_params = List[LVarAssignment]()
      params.map{p => {
        val type_p = get_var_type(p, declarations)
        val uncast_param_var = fresh("uncast_param_var")
        val param_var = fresh("fv_param")
        param_vars = param_vars :+ param_var
        load_params = load_params ++ 
        (LVarAssignment(uncast_param_var, LLoad(LTPointerTo(type_p), p)) ::
          LVarAssignment(param_var, LBitCast(type_p, uncast_param_var, LTI8Pointer)) :: Nil)
      }}


      val f_call_result = fresh("f_call_result")

      (load_f_pointer :: load_params) ++ 
      (LVarAssignment(f_call_result, LCallFPointer(f_pointer, param_vars)) :: 
        LStore(LTI8Pointer, f_call_result, LTI8PointerPointer, lh_v) :: Nil)
    }

    case CVarAssignment(lh_v, CDereferenceVar(v)) => {
      val type_lh = get_var_type(lh_v, declarations)
      val type_rh = get_var_type(v, declarations)
      val a = fresh("assign_deref")
      val b = fresh("assign_deref")
      LVarAssignment(a, LLoad(LTPointerTo(type_rh), v)) ::
      LVarAssignment(b, LLoad(type_rh, a)) ::
      LStore(LTUnderlyingType(type_rh), b, LTPointerTo(type_lh), lh_v) :: Nil
    }

    case CVarAssignment(v, CMalloc(res_type, data_type, num)) => {
      var bytes = 0
      // int is four bytes wide, all other types 8
      if (data_type == CTInt)
        bytes = 4 * num
      else
        bytes = 8 * num

      val type_arr = convert_c_type_to_llvm_ir_type(res_type)
      val a = fresh("malloc_var")
      val b = fresh("cast_mem")

      LVarAssignment(a, LMalloc(bytes)) ::
      LVarAssignment(b, LBitCast(LTI8Pointer, a, type_arr)) ::
      LStore(type_arr, b, LTPointerTo(type_arr), v) :: Nil
    }

    case CArrayAssignment(av, index, CFunctionPointer(f_name)) => {
      val type_arr = get_var_type(av, declarations)
      var num_params = -1
      m.functions.map{f => {
        if (f.name == f_name)
          num_params = f.params.size
      }}
      val a = fresh("assign_fp_to_arr")
      val b = fresh("assign_fp_to_arr")

      LVarAssignment(a, LLoad(LTPointerTo(type_arr), av)) ::
      LVarAssignment(b, LGetElementPtr(type_arr, a, index)) ::
      LStoreFPointer(num_params, f_name, type_arr, b) :: Nil
    }

    case CArrayAssignment(av, index, CIdn(rh_v)) => {
      val type_arr = get_var_type(av, declarations)
      val type_rh_v = get_var_type(rh_v, declarations)
      val a = fresh("assign_v_to_arr")
      val b = fresh("assign_v_to_arr")
      val c = fresh("assign_v_to_arr")
      val d = fresh("assign_v_to_arr")

      LVarAssignment(a, LLoad(LTPointerTo(type_rh_v), rh_v)) ::
      LVarAssignment(b, LBitCast(type_rh_v, a, LTUnderlyingType(type_arr))) ::
      LVarAssignment(c, LLoad(LTPointerTo(type_arr), av)) ::
      LVarAssignment(d, LGetElementPtr(type_arr, c, index)) ::
      LStore(LTUnderlyingType(type_arr), b, type_arr, d) :: Nil
    }

    case CDereferencedVarAssignment(lh_v, CPrimitiveInstruction(op, cs)) => {
      if (cs.size == 1) {
        op match {
          case "sqrt" => {
            val res = fresh("res")
            val res_ptr = fresh("res_ptr")
            val type_res = get_var_type(lh_v, declarations)

            val operand = cs(0)

            (
              if (operand.isInstanceOf[CIdn]) {
                val idn = operand.asInstanceOf[CIdn].idn
                val type_idn = get_var_type(idn, declarations)
                val op_var = fresh("operand_var")

                LVarAssignment(op_var, LLoad(LTPointerTo(type_idn), idn)) ::
                LVarAssignment(res, LSqrt(LVarAccess(LTDouble, op_var))) :: Nil
              } else if (operand.isInstanceOf[CStaticValue]) {
                val d = operand.asInstanceOf[CStaticValue].d

                LVarAssignment(res, LSqrt(LStaticValue(d))) :: Nil
              } else {
                throw new IllegalArgumentException(operand.toString)
              }
            ) ++
            (LVarAssignment(res_ptr, LLoad(LTPointerTo(type_res), lh_v)) ::
              LStore(LTUnderlyingType(type_res), res, type_res, res_ptr) :: Nil)
         }
         case _ => throw new IllegalArgumentException("Unknown prim op: " + op)
        }
      }
      else if (cs.size == 2) {
        var load_operands = List[LVarAssignment]()
        var loaded_operands = List[LExp]()

        cs.map{operand => {
          if (operand.isInstanceOf[CIdn]) {
            val idn = operand.asInstanceOf[CIdn].idn
            val type_idn = get_var_type(idn, declarations)
            val op_var = fresh("operand_var")

            load_operands = load_operands :+ LVarAssignment(op_var, LLoad(LTPointerTo(type_idn), idn))
            loaded_operands = loaded_operands :+ LVarAccess(type_idn, op_var)
          }
          if (operand.isInstanceOf[CStaticValue]) {
            loaded_operands = loaded_operands :+ LStaticValue(operand.asInstanceOf[CStaticValue].d)
          }
        }}

        val res = fresh("res")
        val res_ptr = fresh("res_ptr")

        val type_res = get_var_type(lh_v, declarations)

        load_operands ++ 
        (op match {
            case "<" | ">" | "=" => {
              val res_i1 = fresh("res_i1")
              LVarAssignment(res_i1, LPrimitiveInstruction(op, loaded_operands)) ::
              LVarAssignment(res, LZext(res_i1)) :: Nil
            } 
            case "+" | "-" | "*" | "/" => LVarAssignment(res, LPrimitiveInstruction(op, loaded_operands)) :: Nil
            case "min" | "max" => { 
              val cmp_res = fresh("cmp_res")
              val label_true = fresh(op + "_true")
              val label_false = fresh(op + "_false")
              val label_end = fresh(op + "_end")
              val true_val = fresh("true_val")
              val false_val = fresh("false_val")

              ((
                if (op == "min") 
                  LVarAssignment(cmp_res, LPrimitiveInstruction("<", loaded_operands))
                else
                  LVarAssignment(cmp_res, LPrimitiveInstruction(">", loaded_operands))
              ) ::
              LConditionalBr(cmp_res, label_true, label_false) ::
              LLabel(label_true) :: Nil) ++
              (
                if (loaded_operands(0).isInstanceOf[LVarAccess]) {
                  val cast_cs_i = cs(0).asInstanceOf[CIdn].idn
                  val cast_cs_t = get_var_type(cast_cs_i, declarations)
                  LVarAssignment(true_val, LLoad(LTPointerTo(cast_cs_t), cast_cs_i)) :: Nil
                } else
                  Nil
              ) ++
              (LUnconditionalBr(label_end) ::
              LLabel(label_false) :: Nil) ++
              (
                if (loaded_operands(1).isInstanceOf[LVarAccess]) {
                  val cast_cs_i = cs(1).asInstanceOf[CIdn].idn
                  val cast_cs_t = get_var_type(cast_cs_i, declarations)
                  LVarAssignment(false_val, LLoad(LTPointerTo(cast_cs_t), cast_cs_i)) :: Nil
                } else
                  Nil
              ) ++
              (LUnconditionalBr(label_end) ::
              LLabel(label_end) ::
              LVarAssignment(res, LPhi(
                (
                  if (loaded_operands(0).isInstanceOf[LVarAccess]) {
                    (LVarAccess(LTDoublePointer, true_val), label_true)
                  } else
                    (loaded_operands(0), label_true)
                ) ::
                (
                  if (loaded_operands(1).isInstanceOf[LVarAccess]) {
                    (LVarAccess(LTDoublePointer, false_val), label_false)
                  } else
                    (loaded_operands(1), label_false)
                ) :: Nil)) ::
              Nil)
            }
            case _ => throw new IllegalArgumentException("Operator " + op + " has not been implemented yet")
          }) ++
        (LVarAssignment(res_ptr, LLoad(LTPointerTo(type_res), lh_v)) ::
          LStore(LTUnderlyingType(type_res), res, type_res, res_ptr) :: Nil)
      } else {
        throw new IllegalArgumentException("Too many operands: " + cs.toString)
      }
    }

    case CDereferencedVarAssignment(lh_v, CStaticValue(d)) => {
      val const_assign = fresh("const_assign")
      val type_lh_v = get_var_type(lh_v, declarations)
      LVarAssignment(const_assign, LLoad(LTPointerTo(type_lh_v), lh_v)) ::
      LStoreDouble(d, const_assign) :: Nil
    }

    case CReturn(rv) => {
      val llvm_ir_ret_val = fresh("llvm_ir_ret_val")
      LVarAssignment(llvm_ir_ret_val, LLoad(LTI8PointerPointer, rv)) ::
      LRet(LTI8Pointer, llvm_ir_ret_val) :: Nil
    }

    case _ => throw new IllegalArgumentException(s.toString)
  }

  def get_var_type(v: Idn, declarations: List[LVarAssignment]) : LType = {
    declarations.map{d => {
      if (d.v == v)
        return d.e.asInstanceOf[LAlloca].t
    }}
    throw new IllegalArgumentException("Type of var " + v + " unknown.")
  }

}
