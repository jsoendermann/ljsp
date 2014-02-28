package ljsp

import ljsp.AST._
import ljsp.util._

object c_conversion {
  def convert_module_to_c(m: IModule) : CModule = {
    CModule(m.name, m.functions.map{convert_function_to_c})
    // TODO add main method and function declarations
  }

  def convert_function_to_c(f: IFunction) : CFunction = {
    val decls_sts = f.statements.map{convert_statement_to_c}
    var decls = decls_sts.map{_._1}.flatten
    var sts = decls_sts.map{_._2}.flatten

    if (f.name.startsWith("func_")) {
      val env_var = f.params(0)
      val env_var_c = env_var + "c"
      sts = CVarAssignment(env_var_c, CCast(env_var, CTVoidPointerPointer)) :: 
        rename_env_var_in_c_statement_list(sts, env_var)
      decls =  CDeclareVar(env_var_c, CTVoidPointerPointer) :: decls
    }

    val ret_val = fresh("ret_val")
    var sts_with_return = assign_last_expr_in_c_sts_list_to_ret_val(ret_val, sts)

    decls = decls :+ CDeclareVar(ret_val, CTVoidPointer)
    sts_with_return = sts_with_return :+ CReturn(ret_val)

    CFunction(f.name, f.params, decls, sts_with_return)
  }

  def convert_statement_to_c(s: IStatement) : (List[CDeclareVar], List[CStatement]) = s match {
    case IIf(if_var, block1, block2) => {
      val decls_sts_block1 = block1.map{convert_statement_to_c}
      val decls_sts_block2 = block2.map{convert_statement_to_c}

      val decls_block1 = decls_sts_block1.map{_._1}.flatten
      val decls_block2 = decls_sts_block2.map{_._1}.flatten

      val sts_block1 = decls_sts_block1.map{_._2}.flatten
      val sts_block2 = decls_sts_block2.map{_._2}.flatten

     (decls_block1 ++ decls_block2, 
       CIf(if_var, sts_block1, sts_block2) :: Nil)
    }

    case IVarAssignment(v, IArrayAccess(a, index)) => {
      (CDeclareVar(v, CTVoidPointer) :: Nil,
        CVarAssignment(v, CArrayAccess(a, index)) :: Nil)
    }

    case IFunctionCallByName(f_name, params) => {
      var static_value_params = List[(Idn, Double)]()
      var c_params = List[Idn]()

      for (param <- params) {
        if (param.isInstanceOf[IStaticValue]) {
          val d = param.asInstanceOf[IStaticValue].d
          val const = fresh("const")
          static_value_params = static_value_params :+ (const, d)
          c_params = c_params :+ const
        } else {
          c_params = c_params :+ param.asInstanceOf[IIdn].idn
        }
      }

      (static_value_params.map{sv => CDeclareVar(sv._1, CTDoublePointer)},
        static_value_params.map{sv => {
          CVarAssignment(sv._1, CMalloc(CTDoublePointer, CTDouble, 1)) ::
          CDereferencedVarAssignment(sv._1, CStaticValue(sv._2)) :: Nil
        }}.flatten ++
        List(CFunctionCallByName(f_name, c_params)))

    }

    case IFunctionCallByVar(IIdn(hl_var), params) => {
      val casted_hl_var = fresh("casted_hl_var")
      val uncast_func_pointer = fresh("uncast_func_pointer")
      val func_pointer = fresh("func_pointer")
      val env_param = fresh("env_param")

      val num_params = params.size + 1 // +1 for env

      var static_value_params = List[(Idn, Double)]()
      var c_params = List[Idn]()

      for (param <- params) {
        if (param.isInstanceOf[IStaticValue]) {
          val d = param.asInstanceOf[IStaticValue].d
          val const = fresh("const")
          static_value_params = static_value_params :+ (const, d)
          c_params = c_params :+ const
        } else {
          c_params = c_params :+ param.asInstanceOf[IIdn].idn
        }
      }

      (CDeclareVar(casted_hl_var, CTVoidPointerPointer) ::
        CDeclareVar(uncast_func_pointer, CTVoidPointer) ::
        CDeclareVar(func_pointer, CTFunctionPointer(num_params)) :: 
        CDeclareVar(env_param, CTVoidPointer) :: 
        static_value_params.map{sv => CDeclareVar(sv._1, CTDoublePointer)},

        CVarAssignment(casted_hl_var, CCast(hl_var, CTVoidPointerPointer)) ::
        CVarAssignment(uncast_func_pointer, CArrayAccess(casted_hl_var, 0)) ::
        CVarAssignment(func_pointer, CCast(uncast_func_pointer, CTFunctionPointer(num_params))) ::
        CVarAssignment(env_param, CArrayAccess(casted_hl_var, 1)) ::
        static_value_params.map{sv => {
          CVarAssignment(sv._1, CMalloc(CTDoublePointer, CTDouble, 1)) ::
          CDereferencedVarAssignment(sv._1, CStaticValue(sv._2)) :: Nil
        }}.flatten ++
        List(CFunctionCallByVar(func_pointer, env_param :: c_params)))
    }

    case IVarAssignment(idn, IPrimitiveInstruction(op, is)) => {
      if (is.size == 1) {
        op match {
          case "neg" => {
            val op_i = is(0).asInstanceOf[IIdn].idn
            val prim_op_v = fresh("prim_op_v")
            (CDeclareVar(prim_op_v, CTDoublePointer) ::
              CDeclareVar(idn, CTDoublePointer) :: Nil,

              CVarAssignment(prim_op_v, CCast(op_i, CTDoublePointer)) ::
              CVarAssignment(idn, CMalloc(CTDoublePointer, CTDouble, 1)) ::
              CDereferencedVarAssignment(idn, CPrimitiveInstruction(op, CDereferenceVar(prim_op_v) :: Nil)) :: Nil)
          }
          case "sqrt" => {
            if (is(0).isInstanceOf[IIdn]) {
                val op_i = is(0).asInstanceOf[IIdn].idn
                val prim_op_v = fresh("prim_op_v")
                (CDeclareVar(prim_op_v, CTDoublePointer) ::
                  CDeclareVar(idn, CTDoublePointer) :: Nil,

                  CVarAssignment(prim_op_v, CCast(op_i, CTDoublePointer)) ::
                  CVarAssignment(idn, CMalloc(CTDoublePointer, CTDouble, 1)) ::
                  CDereferencedVarAssignment(idn, CPrimitiveInstruction(op, CDereferenceVar(prim_op_v) :: Nil)) :: Nil)
              } else if (is(0).isInstanceOf[IStaticValue]) {
                val op_d = is(0).asInstanceOf[IStaticValue].d
                (CDeclareVar(idn, CTDoublePointer) :: Nil,
                  CVarAssignment(idn, CMalloc(CTDoublePointer, CTDouble, 1)) ::
                  CDereferencedVarAssignment(idn, CPrimitiveInstruction(op, CStaticValue(op_d) :: Nil)) :: Nil)
              } else throw new IllegalArgumentException()
          }
        }
      } else if (is.size == 2) {
        var decls = List[CDeclareVar]()
        var sts = List[CStatement]()
        var c_operands = List[CExp]()

        for (operand <- is) {
          if (operand.isInstanceOf[IIdn]) {
            val o_idn = operand.asInstanceOf[IIdn].idn
            val prim_op_p = fresh("prim_op_p")
            val prim_op_v = fresh("prim_op_v")

            decls = decls ++ (CDeclareVar(prim_op_p, CTDoublePointer) ::
              CDeclareVar(prim_op_v, CTDouble) :: Nil)

            sts = sts ++ (CVarAssignment(prim_op_p, CCast(o_idn, CTDoublePointer)) ::
              CVarAssignment(prim_op_v, CDereferenceVar(prim_op_p)) :: Nil)

            c_operands = c_operands :+ CIdn(prim_op_v)
          } else {
            val d = operand.asInstanceOf[IStaticValue].d
            c_operands = c_operands :+ CStaticValue(d)
          }
        }

        op match {
          case "+" | "-" | "*" | "/" | "min" | "max" => {
            (decls ++ (CDeclareVar(idn, CTDoublePointer) :: Nil),
              sts ++ (CVarAssignment(idn, CMalloc(CTDoublePointer, CTDouble, 1)) ::
                CDereferencedVarAssignment(idn, CPrimitiveInstruction(op, c_operands)) :: Nil))
          }
          case "<" | ">" => {
            (decls ++ (CDeclareVar(idn, CTIntPointer) :: Nil),
              sts ++ (CVarAssignment(idn, CMalloc(CTIntPointer, CTInt, 1)) ::
                CDereferencedVarAssignment(idn, CPrimitiveInstruction(op, c_operands)) :: Nil))
          }
          case _ => throw new IllegalArgumentException("unrecognized prim op")
        }
      } else {
        throw new IllegalArgumentException("prim op with > 2 operands")
      }


    }

    case IVarAssignment(env_idn, IMakeEnv(idns)) => {
      (CDeclareVar(env_idn, CTVoidPointerPointer) :: Nil,
        CVarAssignment(env_idn, CMalloc(CTVoidPointerPointer, CTVoidPointer, idns.size)) ::
        idns.zipWithIndex.map{case (idn, index) => CArrayAssignment(env_idn, index, CIdn(idn))})
    }

    case IVarAssignment(hl_idn, IHoistedLambda(f_name, env)) => {
      (CDeclareVar(hl_idn, CTVoidPointerPointer) :: Nil,

        CVarAssignment(hl_idn, CMalloc(CTVoidPointerPointer, CTVoidPointer, 2)) ::
        CArrayAssignment(hl_idn, 0, CFunctionPointer(f_name)) ::
        CArrayAssignment(hl_idn, 1, CIdn(env.asInstanceOf[IIdn].idn)) ::
        Nil)
    }

    case IVarAssignment(idn, IIdn(rh_idn)) => {
      if (idn.startsWith("if_var_")) {
        val cast_if_var_pointer = fresh("cast_if_var_pointer")
        (CDeclareVar(cast_if_var_pointer, CTIntPointer) ::
          CDeclareVar(idn, CTInt) :: Nil,

          CVarAssignment(cast_if_var_pointer, CCast(rh_idn, CTIntPointer)) ::
          CVarAssignment(idn, CDereferenceVar(cast_if_var_pointer)) :: Nil)
      } else {
        // TODO test if this works
        (CDeclareVar((idn), CTDoublePointer) :: Nil,

          CVarAssignment(idn, CIdn(rh_idn)) :: Nil)
      }
    }
    
    case IIdn(idn) => (Nil, CIdn(idn) :: Nil)
    case IStaticValue(d) => (Nil, CStaticValue(d) :: Nil)


    case _ => (Nil, CIdn("###" + s.getClass.getSimpleName) :: Nil)
  }

  def rename_env_var_in_c_statement_list(sts: List[CStatement], env_name: Idn) : List[CStatement] = {
    sts.map{s => rename_env_var_in_c_statement(s, env_name)}
  }

  def rename_env_var_in_c_statement(s: CStatement, env_name: Idn) : CStatement = s match {
    case CVarAssignment(idn, CArrayAccess(a, index)) => {
      CVarAssignment(idn, CArrayAccess(rename_var(a, env_name, env_name + "c"), index))
    }
    case _ => s
  }

  // TODO this doesn't work with static values that get returned
  def assign_last_expr_in_c_sts_list_to_ret_val(ret_val: Idn, statements: List[CStatement]) : List[CStatement] = statements match {
    case (s::Nil) => {
      (s match {
          case CIf(cond, block1, block2) => CIf(cond, assign_last_expr_in_c_sts_list_to_ret_val(ret_val, block1), assign_last_expr_in_c_sts_list_to_ret_val(ret_val, block2)) :: Nil
          case _ => CVarAssignment(ret_val, s.asInstanceOf[CExp]) :: Nil
        })
    }
    case (s::sts) => s :: assign_last_expr_in_c_sts_list_to_ret_val(ret_val, sts)
    // This is necessary to suppress warnings
    case Nil => throw new IllegalArgumentException()
  }
}
