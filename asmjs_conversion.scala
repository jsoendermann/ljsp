package ljsp

import ljsp.AST._
import ljsp.util._

object asmjs_conversion {
  def convert_prog_to_asmjs(p: SProgram) : AModule = {
    val ds_map = p.ds.groupBy(_.params.size)
    val fnames_map = ds_map.map{ case (size, ds) => Tuple2("ftable"+size.toString, ds.map{_.name.idn})}
    val fnames_map_pow_2 = fnames_map.map{ case (ftable, fnames) => Tuple2(ftable, fnames ++ List.fill(find_next_power_of_2(fnames.size)-fnames.size)(fnames(0)))}
    //println(fnames_map_pow_2)
    //println(ftable_name_and_index_for_fname(fnames_map_pow_2, "fib"))
    AModule(p.ds.map{d => convert_define_to_asmjs(p, fnames_map_pow_2, d)}, fnames_map_pow_2)
  }

  def ftable_name_and_index_for_fname(ftables: Map[String,List[ljsp.AST.Idn]], f: String): Tuple2[String, Int] = {
    ftables.foreach{ case (ftable, fnames) => {
      fnames.zipWithIndex.foreach{ case(fname, index) => {
        if (fname == f) 
          return Tuple2(ftable, index)}}}}
    // TODO handle error
    Tuple2("Error", -1)
  }


  def convert_define_to_asmjs(p: SProgram, ftables: Map[String,List[ljsp.AST.Idn]], d: SDefine) : AFunction = {
    val statements = convert_instruction_to_asmjs(p, ftables, d.e)
    val statements_with_return = add_return(statements)
    AFunction(d.name.idn, d.params.map{si => AIdn(si.idn)}, statements_with_return)
  }

  def convert_instruction_to_asmjs(p: SProgram, ftables: Map[String,List[ljsp.AST.Idn]], e: SExp) : List[AStatement] = e match {
    case SIdn(i) => List(AVarAccess(AIdn(i)))
    case SLet(i, SMakeEnv(idns), e2) => {
      val num_idns = idns.size
      val env_var = AIdn(i.idn)

      val setEnvValues: List[AStatement] = idns.zipWithIndex.map{ case (idn, index) => AHeapAssignment(APrimitiveInstruction("+", AVarAccess(env_var), AStaticValue(index * 4)), AVarAccess(AIdn(idn.idn))) }

      List(AVarAssignment(env_var, AAlloc(num_idns))) ++
      setEnvValues ++
      convert_instruction_to_asmjs(p, ftables, e2)
    }

    case SLet(i, SHoistedLambda(f, env), e2) => {
      val ai = AIdn(i.idn)


      List(AVarAssignment(ai, AAlloc(2)),
        AHeapAssignment(AVarAccess(ai), AStaticValue(ftable_name_and_index_for_fname(ftables, f.idn)._2)),
        AHeapAssignment(APrimitiveInstruction("+", AVarAccess(ai), AStaticValue(4)), AVarAccess(AIdn(env.asInstanceOf[SIdn].idn)))) ++
      convert_instruction_to_asmjs(p, ftables, e2)
    
    }


    //  AVarAssignment(
    case SLet(i, e1, e2) => {
      List(AVarAssignment(AIdn(i.idn), convert_value_to_asmjs(p, ftables, e1))) ++
      convert_instruction_to_asmjs(p, ftables, e2)
    }
    case SAppl(proc, es) => {
      List(convert_value_to_asmjs(p, ftables, e))
    }

    case SIf(e1, e2, e3) => {
      val if_var = AIdn(fresh("if_var").idn)
      List(AVarAssignment(if_var, convert_value_to_asmjs(p, ftables, e1)),
        AIf(AVarAccess(if_var), convert_instruction_to_asmjs(p, ftables, e2), convert_instruction_to_asmjs(p, ftables, e3)))
    }

    case _ => List(ATODO)
  }

  def convert_value_to_asmjs(p: SProgram, ftables: Map[String,List[ljsp.AST.Idn]], e: SExp) : AExp = e match {
    case SInt(i) => AStaticValue(i)
    case SNth(n, e) => AArrayAccess(AStaticValue(n*4), convert_value_to_asmjs(p, ftables, e))
    case SIdn(i) => AVarAccess(AIdn(i))
    // FIXME this only uses the first two operands. this should be fixed in the cps conv. function
    // a primitive application with more than two operands should be converted into several with two each
    case SApplPrimitive(proc, es) => APrimitiveInstruction(proc.idn, convert_value_to_asmjs(p, ftables, es(0)), convert_value_to_asmjs(p, ftables, es(1)))
    /*case SHoistedLambda(f, SMakeEnv(idns)) => {
      val num_vars = idns.size

      AVarAssignment(AIdn("env_temp"), AAlloc(num_vars));
    }*/
    case SAppl(proc, es) => {
      // This checks if this call is to a function defined in the defines of the program
      // FIXME: This does not work if a function is being returned as result of a more complex expression
      if (proc.isInstanceOf[SIdn] && p.ds.foldLeft(false) {(v, d) => v || d.name == proc}) {
        AFunctionCallByName(AIdn(proc.asInstanceOf[SIdn].idn), es.map{e => convert_value_to_asmjs(p, ftables, e)})
      } else {
        val ftable_name = "ftable"+(es.size+1).toString
        AFunctionCallByIndex(AIdn(ftable_name), AIdn(proc.asInstanceOf[SIdn].idn), ftables(ftable_name).size-1, es.map{convert_value_to_asmjs(p, ftables, _)})
      }
    }
    case _ => ATODO //TODO this shouldn't happen
  }

  def add_return(statements: List[AStatement]) : List[AStatement] = statements match {
    case (s::Nil) => s match {
      case AIf(cond, block1, block2) => {
        val ret_val = AIdn(fresh("ret_val").idn)
        List(AIf(cond, assign_last_expr_to_ret(ret_val, block1), assign_last_expr_to_ret(ret_val, block2)),
          AReturn(AVarAccess(ret_val)))}
      case _ => List(AReturn(s))
    }
    case (s::sts) => List(s) ++ add_return(sts)
    case Nil => {
      // TODO find a better way to suppress warning
      throw new IllegalArgumentException()
    }
  }

  def assign_last_expr_to_ret(ret_val: AIdn, statements: List[AStatement]) : List[AStatement] = statements match {
    // TODO this only works if the last statement is an expression and not a statement
    // is this always the case?
    // TODO if s is AIf, this doesn't work. can the last statement be a nested if at this point?
    case (s::Nil) => List(AVarAssignment(ret_val, s.asInstanceOf[AExp]))
    case (s::sts) => List(s) ++ assign_last_expr_to_ret(ret_val, sts)
    case Nil => {
      // TODO find a better way to suppress warning
      throw new IllegalArgumentException()
    }
  }

}
