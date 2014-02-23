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
  case class IIf(cond: IExp, block1: List[IStatement], block2: List[IStatement]) extends IStatement
  case class IVarAssignment(idn: IIdn, value: IExp) extends IStatement
  case class IReturn(e: IExp) extends IStatement

  abstract class IExp extends IStatement
  case class IIdn(idn: Idn) extends IExp
  case class IStaticValue(d: Double) extends IExp
  case class IFunctionCallByName(f_name: IIdn, params: List[IExp]) extends IExp
  case class IFunctionCallByVar(f_var: IExp, params: List[IExp]) extends IExp
  case class IPrimitiveInstruction(op: String, is: List[IExp]) extends IExp
  case class IMakeEnv(idns: List[IIdn]) extends IExp
  case class IHoistedLambda(f_name: IIdn, env: IExp) extends IExp
  case class IArrayAccess(a: IExp, index: Int) extends IExp



  // Asm.js AST classes
  case class AModule(name: String, funtions: List[AFunction], ftables: Map[String, List[String]])  
  
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

