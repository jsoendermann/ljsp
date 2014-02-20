package ljsp

object AST {

  type Idn = String


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



  // Asm.js AST
  case class AModule(name: String, funtions: List[AFunction], ftables: Map[String, List[String]])  
  
  case class AFunction(name: String, params: List[AIdn], instructions: List[AStatement]) 

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
  // TODO rename base_address to base
  case class AArrayAccess(base_address: AExp, offset: AExp) extends AExp
  case class AAlloc(size: Int) extends AExp

}

