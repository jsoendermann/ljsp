package ljsp

import ljsp.AST._
import ljsp.util._

object llvm_ir_conversion {
  def convert_module_to_llvm_ir(m: CModule) : LModule = {
    LModule(m.name, m.functions.map{convert_function_to_llvm_ir})
  }

  def convert_function_to_llvm_ir(f: CFunction) : LFunction = {
    // TODO rename params + alloc and move into alloca   
    val declarations = f.declarations.map{convert_declaration_to_llvm_ir}
    val statements = f.statements{convert_statement_to_llvm_ir}.flatten
    LFunction(f.name, f.params, statements)
  }

  def convert_declaration_to_llvm_ir(d: CDeclareVar) : LStatement = {
    LVarAccess(d.var_name, LAlloca(convert_c_type_to_llvm_ir_type(d.var_type)))
  }

  def convert_c_type_to_llvm_ir_type(ct: CType) : LType = ct match {
    case CTInt => LTInt
    case CTIntPointer => LTIntPointer
    case CTDouble => LTDouble
    case CTDoublePointer => LTDoublePointer
    case CTDoublePointerPointer => LTDoublePointerPointerPointer
    case CTVoidPointer => LTI8Pointer
    case CTVoidPointerPointer => LTI8PointerPointer
    case CTFunctionPointer(num_params) => LTFunctionPointer(num_params
  }

  def convert_statement_to_llvm_ir(s: CStatement) : List[LStatement] = s match {
    // CIf
    case CVarAssignment(v, CCast(rh_v, t)) => {
      val a = fresh("cast_a")
      val b = fresh("cast_b")
      LVarAssignment(a, LLoad(

    case _ => LIdn("###" + s.getClass.getSimpleName) :: Nil)
  }

    def get_var_type(v: Idn, 

}
