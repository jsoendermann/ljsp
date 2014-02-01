package ljsp

object AST {

  type Num = Int
  type Idn = String


  abstract class SExp

  case class SProgram(ds: List[SDefine], e: SExp) extends SExp { override def toString = ds.mkString("\n") + "\n" + e.toString }

  case class SDefine(name: SIdn, params: List[SIdn], e: SExp) extends SExp { override def toString = "(define (" + name.toString() + " " +  params.mkString(" ") + ") " + e.toString() + ")" }

  case class SIdn(idn: Idn) extends SExp { override def toString = idn }
  case class SInt(i: Num) extends SExp { override def toString = i.toString() }
  // TODO double
  case class SIf(e1: SExp, e2: SExp, e3: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + " " + e3.toString() + ")" }
  case class SLambda(params: List[SIdn], e: SExp) extends SExp { override def toString = "(lambda (" + params.mkString(" ") + ") " + e.toString() + ")" }
  // TODO begin
  case class SAppl(proc: SExp, es: List[SExp]) extends SExp { override def toString = "(" + proc.toString() + " " + es.mkString(" ") + ")"}
  case class SApplPrimitive(proc: SIdn, es: List[SExp]) extends SExp { override def toString = "(" + proc.toString() + " " + es.mkString(" ") + ")"}
  case class SLet(idn: SIdn, e1: SExp, e2: SExp) extends SExp { override def toString = "(let ((" + idn.toString() + " " + e1.toString() + ")) " + e2.toString() + ")" }
  // TODO more than one variable let


  // Clases used in closure conversion
  case class SMakeEnv(idns: List[SIdn]) extends SExp { override def toString = { "(make-env" + (if (idns.size == 0) ")" else " " + idns.mkString(" ") + ")") }}
  case class SMakeLambda(lambda: SLambda, env: SExp) extends SExp { override def toString = "(make-lambda " + lambda.toString + " " + env.toString + ")" }
  case class SNth(n: Int, e: SExp) extends SExp { override def toString = "(nth " + n.toString + " " + e.toString + ")" }
  case class SGetEnv(e: SExp) extends SExp { override def toString = "(get-env " + e.toString + ")" }
  case class SGetProc(e: SExp) extends SExp { override def toString = "(get-proc " + e.toString + ")" }


  // Classes used in hoisting phase
  case class SHoistedLambda(f: SIdn, env: SExp) extends SExp { override def toString = "(hoisted-lambda " + f.toString + " " + env.toString + ")" }


  // Asm.js AST

  // TODO move this to a different file
  def local_vars(instructions: List[AStatement]) : Set[Idn] = instructions match {
    case Nil => Set()
    case (i::is) => local_vars(is) ++ (i match {
      case AVarAssignment(i, v) => Set(i.idn)
      case _ => Set()
    })
  }

  case class AModule(fs: List[AFunction], ftables: Map[String, List[String]]) { 
    override def toString = { 
      """
      function AsmModule(stdlib, foreign, heap) {
      "use asm";

      var mem_top = 0;
      var H32 = new stdlib.Int32Array(heap);

      var log = foreign.consoleDotLog;


      function alloc(size) {
          size = size|0;
          
          var current_mem_top = 0;
          current_mem_top = mem_top;
          mem_top = mem_top + size|0;

          return current_mem_top|0;
      }
      """ +
      fs.mkString("\n") + 
      "\n\n" + 
      ftables.map{case (ftable, fnames) => "var "+ftable+ " = [" + fnames.mkString(",") + "];"}.mkString("\n") +
      "\n}"
    }
  }
  case class AIdn(idn: Idn) { override def toString = { idn }}
  case class AFunction(name: String, params: List[AIdn], instructions: List[AStatement]) { 
    override def toString = { 
      val lv = local_vars(instructions)
      "function " + name + "(" + params.mkString(", ") + ")" + 
      "{\n" + params.map{p => p.toString() + " = " + p.toString() + "|0;\n"}.mkString("") + "\n" +
      (if (lv.size > 0) "var " + lv.mkString(" = 0, ") + " = 0;\n\n"; else "") +
      instructions.map{i => i.toString() + ";\n"}.mkString("") + "\n}" 
    }
  }

  abstract class AStatement
  case class AVarAssignment(idn: AIdn, value: AExp) extends AStatement { override def toString = { idn.toString + " = " + value.toString() }}
  case class AHeapAssignment(index: AExp, value: AExp) extends AStatement { override def toString = { "H32[(" + index.toString + ")>>2] = " + value.toString() }}
  case class AIf(cond: AExp, block1: List[AStatement], block2: List[AStatement]) extends AStatement { override def toString = { "if (" + cond.toString() + ") {\n" + block1.map{i => i.toString() + ";\n"}.mkString("") + "} else {\n" + block2.map{i => i.toString() + ";\n"}.mkString("") + "}" }}

  abstract class AExp extends AStatement
  case class AStaticValue(i: Int) extends AExp { override def toString = { i.toString() }}
  case class AFunctionCallByName(f: AIdn, params: List[AExp]) extends AExp { override def toString = { f + "(" + params.mkString(", ") + ")" }}
  case class AFunctionCallByIndex(ftable: AIdn, fpointer: AIdn, params: List[AExp]) extends AExp { override def toString = { ftable + "[" + AHeapAccess(AVarAccess(fpointer)).toString + "](" + APrimitiveInstruction("+", AVarAccess(fpointer), AStaticValue(1)).toString + ", " + params.mkString(", ") + ")" }}
  case class APrimitiveInstruction(op: String, operand1: AExp, operand2: AExp) extends AExp { override def toString = { operand1.toString() + op + operand2.toString() }}
  case class AVarAccess(idn: AIdn) extends AExp { override def toString = { idn.toString }}
  case class AHeapAccess(index: AExp) extends AExp { override def toString = { "H32[(" + index.toString + ")>>2]" }}
  case class AArrayAccess(index: AExp, adr: AExp) extends AExp { override def toString = { "H32[(" + APrimitiveInstruction("+", adr, index).toString() + ") >> 2]" }}
  case class AAlloc(size: Int) extends AExp { override def toString = { "alloc("+size.toString()+")" }}
  case object ATODO extends AExp { override def toString = { "/*TODO*/" }}

}
