package ljsp

import ljsp.AST._
import ljsp.util._

object asmjs_conversion {
  def convert_module_to_asmjs(m: IModule) : AModule = {
    val functions_without_expression = m.functions.filter{_.name != "expression"}

    // To build ftables, first group functions by number of parameters.
    val fs_map = functions_without_expression.groupBy(_.params.size)
    // Create a new map that instead of mapping #params ints to lists of SDefines, maps strings of the form "ftable"+#params to lists of function names (strings)
    val fnames_map = fs_map.map{ case (size, fs) => ("ftable"+size.toString, fs.map{_.name})}
    // every ftable needs to be equal in size to a power of two. this is achieved by appending the first
    // element to the end of the table as many times as necessary to reach the next higher power of two. these elements
    // of the table won't be used by the program and are just there to produce valid asm.js
    val fnames_map_pow_2 = fnames_map.map{ case (ftable, fnames) => (ftable, fnames ++ List.fill(find_next_power_of_2(fnames.size)-fnames.size)(fnames(0)))}

    AModule("AsmModule", functions_without_expression.map{f => convert_function_to_asmjs(fnames_map_pow_2, f)}, fnames_map_pow_2)
  }

  def ftable_name_and_index_for_fname(ftables: Map[String, List[Idn]], f: String): Tuple2[String, Int] = {
    ftables.foreach{ case (ftable, fnames) => {
      fnames.zipWithIndex.foreach{ case(fname, index) => {
        if (fname == f) 
          return Tuple2(ftable, index)}}}}
    throw new IllegalArgumentException("Function not found in tables")
  }


  def convert_function_to_asmjs(ftables: Map[String, List[Idn]], f: IFunction) : AFunction = {
    val statements = f.statements.map{s => convert_statement_to_asmjs(ftables, s)}.flatten
    val statements_with_return = add_return(statements)
    val statements_with_stack_reset = 
    (
      if (f.name.endsWith("_copy")) 
        AVarAssignment("mem_top", AStaticValue(0)) :: statements_with_return 
      else 
        statements_with_return
    )
    
    AFunction(f.name, f.params, statements_with_stack_reset)
  }

  def convert_statement_to_asmjs(ftables: Map[String, List[Idn]], s: IStatement) : List[AStatement] = s match {
    case IIf(if_var, block1, block2) => {
      val converted_block1 = block1.map{s => convert_statement_to_asmjs(ftables, s)}.flatten
      val converted_block2 = block2.map{s => convert_statement_to_asmjs(ftables, s)}.flatten
      AIf(if_var, converted_block1, converted_block2) :: Nil
    }

    case IVarAssignment(env_idn, IMakeEnv(idns)) => {
      val num_idns = idns.size

      val setEnvVars = idns.zipWithIndex.map{ 
        case (idn, index) => AArrayAssignment(env_idn, index, AIdn(idn))
      }

      (AVarAssignment(env_idn, AAlloc(num_idns)) :: Nil) ++ setEnvVars
    }

    case IVarAssignment(hl_idn, IHoistedLambda(f_name, env)) => {
      val hl_var = AIdn(hl_idn)

      // Make array with
      // 1. The ftable index (the correct ftable will be determined once the 
      //    hoisted lambda is being called by looking at the number of params 
      //    it's being called with)
      // 2. A pointer (array index) to the environment array
      AVarAssignment(hl_idn, AAlloc(2)) ::
      AArrayAssignment(hl_idn, 0, AStaticValue(ftable_name_and_index_for_fname(ftables, f_name)._2)) ::
      AArrayAssignment(hl_idn, 1, AIdn(env)) ::
      Nil
    }

    case IVarAssignment(idn, value) => {
      AVarAssignment(idn, convert_expression_to_asmjs(ftables, value)) :: Nil
    }

    case _ => convert_expression_to_asmjs(ftables, s.asInstanceOf[IExp]) :: Nil
  }

  def convert_expression_to_asmjs(ftables: Map[String, List[Idn]], e: IExp) : AExp = e match {
    case IIdn(i) => AIdn(i)
    case IStaticValue(d) => AStaticValue(d)
    case IFunctionCallByName(f_name, params) => {
      val converted_params = params.map{convert_expression_to_asmjs(ftables, _)}
      AFunctionCallByName(f_name, converted_params)
    }
    case IFunctionCallByVar(hl_var, params) => {
      val converted_params = params.map{convert_expression_to_asmjs(ftables, _)}
      val ftable_name = "ftable" + (params.size + 1).toString
      val mask = ftables(ftable_name).size-1
      AFunctionCallByIndex(ftable_name, hl_var, mask, converted_params)
    }
    case IPrimitiveInstruction(op, is) => {
      APrimitiveInstruction(op, is.map{convert_expression_to_asmjs(ftables, _)})
    }
    case IArrayAccess(a, index) => AArrayAccess(a, index)

    case _ => throw new IllegalArgumentException(e.toString)
  }

  // TODO move these functions to a separate file
  // This function wraps the last statement in an AReturn if it's not an if (which means it's a function call?)
  // otherwise it creates a variable that holds the return value, calls assign_last_expr_to_ret and returns the
  // value after the if
  def add_return(statements: List[AStatement]) : List[AStatement] = statements match {
    case (s::Nil) => s match {
      case AIf(cond, block1, block2) => {
        val ret_val = fresh("ret_val")
        List(AIf(cond, assign_last_expr_to_ret_val(ret_val, block1), assign_last_expr_to_ret_val(ret_val, block2)),
          AReturn(AIdn(ret_val)))}
      case _ => List(AReturn(s))
    }
    case (s::sts) => List(s) ++ add_return(sts)
    // This is necessary to suppress warnings
    case Nil => throw new IllegalArgumentException()
  }


  def assign_last_expr_to_ret_val(ret_val: Idn, statements: List[AStatement]) : List[AStatement] = statements match {
    case (s::Nil) => {
      (s match {
          case AIf(cond, block1, block2) => List(AIf(cond, assign_last_expr_to_ret_val(ret_val, block1), assign_last_expr_to_ret_val(ret_val, block2)))
          case _ => List(AVarAssignment(ret_val, s.asInstanceOf[AExp]))
        })
    }
    case (s::sts) => List(s) ++ assign_last_expr_to_ret_val(ret_val, sts)
    // This is necessary to suppress warnings
    case Nil => throw new IllegalArgumentException()
  }
}
