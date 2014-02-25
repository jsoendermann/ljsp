package ljsp

object AST {

  type Idn = String



  // LJSP AST classes
  abstract class SExp

  case class SProgram(ds: List[SDefine], e: SExp) extends SExp

  case class SDefine(name: SIdn, params: List[SIdn], e: SExp) extends SExp

  case class SIdn(idn: Idn) extends SExp
  case class SDouble(d: Double) extends SExp
  case class SIf(e1: SExp, e2: SExp, e3: SExp) extends SExp
  case class SLambda(params: List[SIdn], e: SExp) extends SExp
  case class SAppl(proc: SExp, es: List[SExp]) extends SExp
  case class SApplPrimitive(proc: SIdn, es: List[SExp]) extends SExp
  case class SLet(idn: SIdn, e1: SExp, e2: SExp) extends SExp
  case class SLetN(idnsAndE1s: List[LetDefineBlock], e2: SExp) extends SExp
  case class LetDefineBlock(idn: SIdn, e: SExp)

  // Clases used in closure conversion
  case class SMakeEnv(idns: List[SIdn]) extends SExp
  case class SMakeLambda(lambda: SLambda, env: SExp) extends SExp
  case class SNth(n: Int, e: SExp) extends SExp
  case class SGetEnv(e: SExp) extends SExp
  case class SGetProc(e: SExp) extends SExp

  // Class used in the hoisting phase
  case class SHoistedLambda(f: SIdn, env: SExp) extends SExp



  // IR AST classes
  // TODO either remove IIdn or use it consistently
  case class IModule(name: Idn, functions: List[IFunction])

  case class IFunction(name: Idn, params: List[Idn], statements: List[IStatement])

  abstract class IStatement
  case class IIf(if_var: Idn, block1: List[IStatement], block2: List[IStatement]) extends IStatement
  case class IVarAssignment(idn: Idn, value: IExp) extends IStatement

  abstract class IExp extends IStatement
  case class IIdn(idn: Idn) extends IExp
  case class IStaticValue(d: Double) extends IExp
  case class IFunctionCallByName(f_name: Idn, params: List[IExp]) extends IExp
  case class IFunctionCallByVar(hl_var: IExp, params: List[IExp]) extends IExp
  // TODO add type for primitive instructions
  case class IPrimitiveInstruction(op: String, is: List[IExp]) extends IExp
  case class IMakeEnv(idns: List[Idn]) extends IExp
  case class IHoistedLambda(f_name: Idn, env: IExp) extends IExp
  case class IArrayAccess(a: Idn, index: Int) extends IExp



  // C AST classes
  case class CModule(name: Idn, functions: List[CFunction])

  case class CFunction(name: Idn, params: List[Idn], declarations: List[CDeclareVar], statements: List[CStatement])

  abstract class CType
  case object CTInt extends CType
  case object CTIntPointer extends CType
  case object CTDouble extends CType
  case object CTDoublePointer extends CType
  case object CTVoidPointer extends CType
  case object CTVoidPointerPointer extends CType
  case class CTFunctionPointer(num_params: Int) extends CType

  abstract class CStatement
  case class CDeclareVar(var_name: Idn, var_type: CType) extends CStatement

  case class CIf(if_var: Idn, block1: List[CStatement], block2: List[CStatement]) extends CStatement
  case class CVarAssignment(v: Idn, e: CExp) extends CStatement
  case class CDereferencedVarAssignment(v: Idn, e: CExp) extends CStatement
  case class CArrayAssignment(av: Idn, index: Int, e: CExp) extends CStatement
  case class CReturn(s: CStatement) extends CStatement

  abstract class CExp extends CStatement
  case class CIdn(idn: Idn) extends CExp
  case class CStaticValue(d: Double) extends CExp
  // TODO cast this to res_type with CCast
  case class CMalloc(res_type: CType, data_type: CType, num: Int) extends CExp
  case class CPrimitiveInstruction(op: String, cs: List[CExp]) extends CExp
  case class CFunctionPointer(f_name: Idn) extends CExp
  case class CCast(v: Idn, t: CType) extends CExp
  case class CDereferenceVar(v: CExp) extends CExp
  case class CArrayAccess(av: Idn, index: Int) extends CExp
  case class CFunctionCallByName(f_name: Idn, params: List[CExp]) extends CExp
  case class CFunctionCallByVar(hl_var: Idn, params: List[CExp]) extends CExp



  // LLVM IR AST classes
  case class LModule(name: String, functions: List[LFunction])

  case class LFunction(name: String, params: List[Idn], statements: List[LStatement])

  // TODO tidy this up, make it possible to go up and down pointer levels, use LTFunctionPointer for everything
  abstract class LType
  case object LTInt extends LType
  case object LTIntPointer extends LType
  case object LTDouble extends LType
  case object LTDoublePointer extends LType
  case object LTDoublePointerPointerPointer extends LType
  case object LTI8Pointer extends LType
  case object LTI8PointerPointer extends LType
  case object LTI8PointerPointerPointer extends LType
  case class LTFunctionPointer(num_params: Int) extends LType

  case class LTPointerTo(t: LType) extends LType

  abstract class LStatement
  case class LVarAssignment(v: Idn, e: LExp) extends LStatement
  case class LStore(e1: LExp, e2: LExp) extends LStatement
  case class LRet(e: LExp) extends LStatement

  abstract class LExp
  case class LVarAccess(v: Idn, t: LType) extends LExp
  case class LAlloca(t: LType) extends LExp
  case class LLoad(e: LExp) extends LExp
  case class LBitCast(v: Idn, old_type: LType, new_type: LType) extends LExp
  case class LGetElementPtr(a: LExp, index: Int) extends LExp
  case class LCall(f_pointer: Idn, params: List[Idn]) extends LExp
  case class LMalloc(num_elements: Int) extends LExp



  // Asm.js AST classes
  case class AModule(name: String, functions: List[AFunction], ftables: Map[String, List[String]])  
  
  case class AFunction(name: String, params: List[AIdn], statements: List[AStatement]) 

  abstract class AStatement
  case class AVarAssignment(idn: AIdn, value: AExp) extends AStatement
  case class AArrayAssignment(base: AExp, offset: AExp, value: AExp) extends AStatement
  case class AIf(cond: AExp, block1: List[AStatement], block2: List[AStatement]) extends AStatement
  case class AReturn(s: AStatement) extends AStatement

  abstract class AExp extends AStatement
  case class AIdn(idn: Idn) extends AExp
  case class AVarAccess(v: AIdn) extends AExp
  case class AStaticValue(d: Double) extends AExp
  case class ADoubleToInt(e: AExp) extends AExp
  case class AFunctionCallByName(f: AIdn, params: List[AExp]) extends AExp
  case class AFunctionCallByIndex(ftable: String, fpointer: AIdn, mask: Int, params: List[AExp]) extends AExp 
  // as.size is either 1 or 2
  case class APrimitiveInstruction(op: String, as: List[AExp]) extends AExp
  case class AHeapAccess(index: AExp) extends AExp
  case class AArrayAccess(base: AExp, offset: AExp) extends AExp
  case class AAlloc(size: Int) extends AExp
}

