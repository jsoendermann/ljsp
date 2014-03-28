package ljsp

object AST {
  // TODO add types for Double, possibly Int
  type Idn = String



  // ################################ LJSP AST classes ################################
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
  case class SMakeClosure(lambda: SLambda, env: SExp) extends SExp
  case class SNth(n: Int, e: SExp) extends SExp
  case class SGetEnv(e: SExp) extends SExp
  case class SGetProc(e: SExp) extends SExp

  // Class used in the hoisting phase
  case class SHoistedLambda(f: SIdn, env: SExp) extends SExp



  // ################################ IR AST classes ################################
  // TODO remove name from this and all other modules
  case class IModule(name: Idn, functions: List[IFunction])

  case class IFunction(name: Idn, params: List[Idn], statements: List[IStatement])

  abstract class IStatement
  case class IIf(if_var: Idn, block1: List[IStatement], block2: List[IStatement]) extends IStatement
  case class IVarAssignment(idn: Idn, value: IExp) extends IStatement

  abstract class IExp extends IStatement
  case class IIdn(idn: Idn) extends IExp
  case class IStaticValue(d: Double) extends IExp
  case class IFunctionCallByName(f_name: Idn, params: List[IExp]) extends IExp
  case class IFunctionCallByVar(hl_var: Idn, params: List[IExp]) extends IExp
  case class IPrimitiveInstruction(op: String, is: List[IExp]) extends IExp
  case class IMakeEnv(idns: List[Idn]) extends IExp
  case class IHoistedLambda(f_name: Idn, env: Idn) extends IExp
  case class IArrayAccess(a: Idn, index: Int) extends IExp



  // TODO actually use this
  // Type class for C and LLVM IR
  abstract class Type
  abstract class TPtrTo(t: Type) extends Type



  // ################################ C AST classes ################################
  case class CModule(name: Idn, functions: List[CFunction])

  case class CFunction(name: Idn, params: List[Idn], declarations: List[CDeclareVar], statements: List[CStatement])

  abstract class CType extends Type
  case object CTInt extends CType
  case object CTIntPointer extends CType
  case object CTDouble extends CType
  case object CTDoublePointer extends CType
  case object CTDoublePointerPointer extends CType
  case object CTVoidPointer extends CType
  case object CTVoidPointerPointer extends CType
  case class CTFunctionPointer(num_params: Int) extends CType

  abstract class CStatement
  case class CDeclareVar(var_name: Idn, var_type: CType) extends CStatement

  case class CIf(if_var: Idn, block1: List[CStatement], block2: List[CStatement]) extends CStatement
  case class CVarAssignment(v: Idn, e: CExp) extends CStatement
  case class CDereferencedVarAssignment(v: Idn, e: CExp) extends CStatement
  case class CArrayAssignment(av: Idn, index: Int, e: CExp) extends CStatement
  case class CReturn(rv: Idn) extends CStatement

  abstract class CExp extends CStatement
  case class CIdn(idn: Idn) extends CExp
  case class CStaticValue(d: Double) extends CExp
  // TODO cast this to res_type with CCast
  case class CMalloc(res_type: CType, data_type: CType, num: Int) extends CExp
  case class JMalloc(res_type: CType, data_type: CType, num: Int) extends CExp
  case class CPrimitiveInstruction(op: String, cs: List[CExp]) extends CExp
  case class CFunctionPointer(f_name: Idn) extends CExp
  case class CCast(v: Idn, t: CType) extends CExp
  case class CDereferenceVar(v: Idn) extends CExp
  case class CArrayAccess(av: Idn, index: Int) extends CExp
  case class CFunctionCallByName(f_name: Idn, params: List[Idn]) extends CExp
  case class CFunctionCallByVar(hl_var: Idn, params: List[Idn]) extends CExp



  // ################################ LLVM IR AST classes ################################
  case class LModule(name: String, functions: List[LFunction])

  case class LFunction(name: String, params: List[Idn], statements: List[LStatement])

  // TODO tidy this up, make it possible to go up and down pointer levels, use LTFunctionPointer for everything
  abstract class LType extends Type
  case object LTI8 extends LType
  case object LTInt extends LType
  case object LTDouble extends LType
  case class LTFunctionPointer(num_params: Int) extends LType

  case class LTPointerTo(t: LType) extends LType
  case class LTUnderlyingType(p: LType) extends LType

  val LTI8Pointer = LTPointerTo(LTI8)
  val LTI8PointerPointer = LTPointerTo(LTI8Pointer)
  val LTI8PointerPointerPointer = LTPointerTo(LTI8PointerPointer)
  val LTIntPointer = LTPointerTo(LTInt)
  val LTDoublePointer = LTPointerTo(LTDouble)
  val LTDoublePointerPointer = LTPointerTo(LTDoublePointer)
  val LTDoublePointerPointerPointer = LTPointerTo(LTDoublePointerPointer)

  abstract class LStatement
  case class LVarAssignment(v: Idn, e: LExp) extends LStatement
  // TODO exp instead of t/v
  // TODO merge these classes into one
  case class LStore(t1: LType, v1: Idn, t2: LType, v2: Idn) extends LStatement
  case class LStoreFPointer(num_params: Int, f_name: Idn, t2: LType, v2: Idn) extends LStatement
  case class LStoreDouble(d: Double, v: Idn) extends LStatement
  case class LLabel(l: Idn) extends LStatement
  case class LUnconditionalBr(l: Idn) extends LStatement
  case class LConditionalBr(br_var: Idn, l_true: Idn, l_false: Idn) extends LStatement
  case class LRet(t: LType, v: Idn) extends LStatement

  abstract class LExp extends LStatement
  // TODO add LIdn class for when type isn't being used
  case class LVarAccess(t: LType, v: Idn) extends LExp
  case class LStaticValue(d: Double) extends LExp
  case class LPrimitiveInstruction(op: String, ls: List[LExp]) extends LExp
  case class LAlloca(t: LType) extends LExp
  case class LLoad(t: LType, v: Idn) extends LExp
  case class LBitCast(old_type: LType, v: Idn, new_type: LType) extends LExp
  case class LGetElementPtr(t: LType, av: Idn, index: Int) extends LExp
  case class LCallFName(f_name: Idn, params: List[Idn]) extends LExp
  // TODO this is a special case of LCallFName but LCallFName assumes
  //      its parameters to be of type i8* at the moment. The way to fix this
  //      is to use LVarAccess instead of Idn for parameters and make
  //      code conversion respect the type of the var access by adding
  //      another class LIdn for when the type doesn't matter.
  case class LSqrt(e: LExp) extends LExp
  case class LCallFPointer(f_pointer: Idn, params: List[Idn]) extends LExp
  case class LMalloc(bytes: Int) extends LExp
  case class LZext(v: Idn) extends LExp
  case class LIcmpNe(v: Idn) extends LExp
  case class LPhi(bs: List[(LExp, Idn)]) extends LExp



  // ################################ Asm.js AST classes ################################
  case class AModule(name: String, functions: List[AFunction], ftables: Map[String, List[String]])  
  
  case class AFunction(name: String, params: List[Idn], statements: List[AStatement]) 

  abstract class AStatement
  case class AVarAssignment(idn: Idn, value: AExp) extends AStatement
  case class AArrayAssignment(base: Idn, offset: Int, value: AExp) extends AStatement
  case class AIf(if_var: Idn, block1: List[AStatement], block2: List[AStatement]) extends AStatement
  case class AReturn(s: AStatement) extends AStatement

  abstract class AExp extends AStatement
  case class AIdn(idn: Idn) extends AExp
  case class AStaticValue(d: Double) extends AExp
  case class ADoubleToInt(e: AExp) extends AExp
  case class AFunctionCallByName(f: Idn, params: List[AExp]) extends AExp
  case class AFunctionCallByIndex(ftable: Idn, fpointer: Idn, mask: Int, params: List[AExp]) extends AExp 
  case class APrimitiveInstruction(op: String, as: List[AExp]) extends AExp
  case class AHeapAccess(index: Idn) extends AExp
  case class AArrayAccess(base: Idn, offset: Int) extends AExp
  case class AAlloc(size: Int) extends AExp
}

