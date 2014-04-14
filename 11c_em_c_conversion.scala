package ljsp

import ljsp.AST._
import ljsp.util._

object em_c_conversion {
  def convert_module_to_em_c(m: CModule) : CModule = {
    val functions_without_expression = m.functions.filter{_.name != "expression"}
    val functions_replace_malloc_by_jalloc = functions_without_expression.map{convert_function_to_em_c}

    var by_value_functions = List[CFunction]()

    functions_replace_malloc_by_jalloc.map{f => {
      if (f.name.endsWith("_copy")) {
        val ret_val = fresh("ret_val")
        val ret_val_cast = fresh("ret_val_cast")
        val ret_val_double = fresh("ret_val_double")
        by_value_functions = by_value_functions :+ CFunction(f.name + "_call_by_value", f.params,
          f.params.map{p => CDeclareVar(p + "_p", CTDoublePointer)} ++ 
            (CDeclareVar(ret_val, CTVoidPointer) ::
              CDeclareVar(ret_val_cast, CTDoublePointer) ::
              CDeclareVar(ret_val_double, CTDouble) :: Nil),
          CFunctionCallByName("free_all", Nil) ::
          f.params.map{p => CVarAssignment(p + "_p", JMalloc(CTDoublePointer, CTDouble, 1)) ::
                              CDereferencedVarAssignment(p + "_p", CIdn(p)) :: Nil}.flatten ++
          (CVarAssignment(ret_val, CFunctionCallByName(f.name, f.params.map{p => p + "_p"})) ::
            CVarAssignment(ret_val_cast, CCast(ret_val, CTDoublePointer)) ::
            CVarAssignment(ret_val_double, CDereferenceVar(ret_val_cast)) ::
            CReturn(ret_val_double) :: Nil))
      }
    }}

    CModule(m.name, functions_replace_malloc_by_jalloc ++ by_value_functions)
  }

  def convert_function_to_em_c(f: CFunction) : CFunction = {
    CFunction(f.name, f.params, f.declarations, f.statements.map{convert_statement_malloc_to_jalloc})
  }

  def convert_statement_malloc_to_jalloc(s: CStatement) : CStatement = s match {
    case CIf(if_var, block1, block2) => {
      CIf(if_var, block1.map{convert_statement_malloc_to_jalloc}, block2.map{convert_statement_malloc_to_jalloc})
    }
    case CVarAssignment(v, e) => CVarAssignment(v, convert_expression_malloc_to_jalloc(e))
    case CDereferencedVarAssignment(v, e) => CDereferencedVarAssignment(v, convert_expression_malloc_to_jalloc(e))
    case CArrayAssignment(av, index, e) => CArrayAssignment(av, index, convert_expression_malloc_to_jalloc(e))
    case CReturn(rv) => s
    case _ => convert_expression_malloc_to_jalloc(s.asInstanceOf[CExp])
  }

  def convert_expression_malloc_to_jalloc(e: CExp) : CExp = e match {
    case CMalloc(res_type, data_type, num) => JMalloc(res_type, data_type, num)
    case _ => e
  }


}
