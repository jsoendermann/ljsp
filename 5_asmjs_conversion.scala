package ljsp

import ljsp.AST._
import ljsp.util._

object asmjs_conversion {
  def convert_prog_to_asmjs(p: SProgram) : AModule = {
    // To build ftables, first group functions by number of parameters.
    val ds_map = p.ds.groupBy(_.params.size)
    // Create a new map that instead of mapping #params ints to lists of SDefines, maps strings of the form "ftable"+#params to lists of function names (strings)
    val fnames_map = ds_map.map{ case (size, ds) => ("ftable"+size.toString, ds.map{_.name.idn})}
    // every ftable needs to be equal in size to a power of two. this is achieved by appending the first
    // element to the end of the table as many times as necessary to reach the next higher power of two. these elements
    // of the table won't be used by the program and are just there to produce valid asm.js
    val fnames_map_pow_2 = fnames_map.map{ case (ftable, fnames) => (ftable, fnames ++ List.fill(find_next_power_of_2(fnames.size)-fnames.size)(fnames(0)))}

    AModule("AsmModule", p.ds.map{d => convert_define_to_asmjs(p, fnames_map_pow_2, d)}, fnames_map_pow_2)
  }

  def ftable_name_and_index_for_fname(ftables: Map[String, List[Idn]], f: String): Tuple2[String, Int] = {
    ftables.foreach{ case (ftable, fnames) => {
      fnames.zipWithIndex.foreach{ case(fname, index) => {
        if (fname == f) 
          return Tuple2(ftable, index)}}}}
    throw new IllegalArgumentException("Function not found in tables")
  }


  def convert_define_to_asmjs(p: SProgram, ftables: Map[String, List[Idn]], d: SDefine) : AFunction = {
    val statements = convert_statement_to_asmjs(p, ftables, d.e)
    val statements_with_return = add_return(statements)
    val statements_with_stack_reset = if (d.name.idn.endsWith("_copy")) AVarAssignment(AIdn("mem_top"), AStaticValue(0)) :: statements_with_return else statements_with_return
    
    AFunction(d.name.idn, d.params.map{sidn => AIdn(sidn.idn)}, statements_with_stack_reset)
  }

  def convert_statement_to_asmjs(p: SProgram, ftables: Map[String, List[Idn]], e: SExp) : List[AStatement] = e match {
    // TODO move this somewhere else
    case SAppl(SHoistedLambda(f, SMakeEnv(idns)), es) => {
      val hl = SIdn(fresh("hoisted_lambda_var"))
      val env = SIdn(fresh("env"))

      convert_statement_to_asmjs(p, ftables, SLet(env, SMakeEnv(idns), SLet(hl, SHoistedLambda(f, env), SAppl(hl, es))))
    }
    case SIdn(_) | SAppl(_, _) => List(convert_value_to_asmjs(p, ftables, e))

    // Two different kind of lets are necessary because hoisted lambdas require more work before
    // the i=e1;e2 part at the end. The first two cases deal with building environments and assigning hoisted lambdas to variables
    case SLet(i, SMakeEnv(idns), e2) => {
      val num_idns = idns.size
      val env_var = AIdn(i.idn)

      val setEnvValues: List[AStatement] = idns.zipWithIndex.map{ case (idn, index) => AArrayAssignment(env_var, AStaticValue(index), AIdn(idn.idn))}
      
      List(AVarAssignment(env_var, AAlloc(num_idns))) ++
      setEnvValues ++
      convert_statement_to_asmjs(p, ftables, e2)
    }

    case SLet(i, SHoistedLambda(f, env), e2) => {
      val hl_var = AIdn(i.idn)

      // Make array with
      // 1. The ftable index (the correct ftable will be determined once the hoisted lambda is being called by looking at the number of params it's being called with)
      // 2. A pointer (array index) to the environment array
      AVarAssignment(hl_var, AAlloc(2)) ::
      AArrayAssignment(hl_var, AStaticValue(0), AStaticValue(ftable_name_and_index_for_fname(ftables, f.idn)._2)) ::
      AArrayAssignment(hl_var, AStaticValue(1), AIdn(env.asInstanceOf[SIdn].idn)) ::
      convert_statement_to_asmjs(p, ftables, e2)}

    case SLet(i, e1, e2) => {
      List(AVarAssignment(AIdn(i.idn), convert_value_to_asmjs(p, ftables, e1))) ++
      convert_statement_to_asmjs(p, ftables, e2)}

    case SIf(e1, e2, e3) => {
      // In asm.js there are no bools and the result to the boolean expression gets assigned to this temporary variable
      // TODO this might be unnecessary
      val if_var = AIdn(fresh("if_var"))
      List(AVarAssignment(if_var, convert_value_to_asmjs(p, ftables, e1)),
        AIf(if_var, convert_statement_to_asmjs(p, ftables, e2), convert_statement_to_asmjs(p, ftables, e3)))}
  }

  def convert_value_to_asmjs(p: SProgram, ftables: Map[String, List[Idn]], e: SExp) : AExp = e match {
    case SIdn(i) => AVarAccess(AIdn(i))
    case SDouble(d) => AStaticValue(d)

    case SAppl(proc, es) => {
      // Two cases for function calls by name or by index
      if (proc.isInstanceOf[SIdn] && p.ds.foldLeft(false) {(v, d) => v || d.name == proc}) 
        AFunctionCallByName(AIdn(proc.asInstanceOf[SIdn].idn), es.map{e => convert_value_to_asmjs(p, ftables, e)})
      else {
        // determine correct ftable by looking at #params
        val ftable_name = "ftable"+(es.size+1).toString
        AFunctionCallByIndex(ftable_name, AIdn(proc.asInstanceOf[SIdn].idn), ftables(ftable_name).size-1, es.map{convert_value_to_asmjs(p, ftables, _)})
      }
    }

    case SApplPrimitive(proc, es) => APrimitiveInstruction(proc.idn, es.map{convert_value_to_asmjs(p, ftables, _)})
    case SNth(n, e) => AArrayAccess(convert_value_to_asmjs(p, ftables, e), AStaticValue(n))
  }

  // This function wraps the last statement in an AReturn if it's not an if (which means it's a function call?)
  // otherwise it creates a variable that holds the return value, calls assign_last_expr_to_ret and returns the
  // value after the if
  def add_return(statements: List[AStatement]) : List[AStatement] = statements match {
    case (s::Nil) => s match {
      case AIf(cond, block1, block2) => {
        val ret_val = AIdn(fresh("ret_val"))
        List(AIf(cond, assign_last_expr_to_ret_val(ret_val, block1), assign_last_expr_to_ret_val(ret_val, block2)),
          AReturn(AVarAccess(ret_val)))}
      case _ => List(AReturn(s))
    }
    case (s::sts) => List(s) ++ add_return(sts)
    // This is necessary to suppress warnings
    case Nil => throw new IllegalArgumentException()
  }


  def assign_last_expr_to_ret_val(ret_val: AIdn, statements: List[AStatement]) : List[AStatement] = statements match {
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
