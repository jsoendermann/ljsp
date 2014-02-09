package ljsp

object AST {

  //type Num = Int
  type Idn = String


  abstract class SExp

  case class SProgram(ds: List[SDefine], e: SExp) extends SExp { override def toString = "; defines\n" + ds.mkString("\n") + "\n; expression\n" + e.toString }

  case class SDefine(name: SIdn, params: List[SIdn], e: SExp) extends SExp { override def toString = "(define (" + name.toString() + " " +  params.mkString(" ") + ") " + e.toString() + ")" }

  case class SIdn(idn: Idn) extends SExp { override def toString = idn }
  //case class SInt(i: Num) extends SExp { override def toString = i.toString() }
  case class SDouble(d: Double) extends SExp { override def toString = d.toString() }
  case class SIf(e1: SExp, e2: SExp, e3: SExp) extends SExp { override def toString = "(if " + e1.toString() + " " + e2.toString() + " " + e3.toString() + ")" }
  case class SLambda(params: List[SIdn], e: SExp) extends SExp { override def toString = "(lambda (" + params.mkString(" ") + ") " + e.toString() + ")" }
  case class SAppl(proc: SExp, es: List[SExp]) extends SExp { override def toString = "(" + proc.toString() + " " + es.mkString(" ") + ")"}
  case class SApplPrimitive(proc: SIdn, es: List[SExp]) extends SExp { override def toString = "(" + proc.toString() + " " + es.mkString(" ") + ")"}
  case class SLet(idn: SIdn, e1: SExp, e2: SExp) extends SExp { override def toString = "(let ((" + idn.toString() + " " + e1.toString() + ")) " + e2.toString() + ")" }
  case class SLetN(idnsAndE1s: List[LetDefineBlock], e2: SExp) extends SExp { override def toString = "(let (" + idnsAndE1s.mkString(" ") + ") " + e2.toString() + ")" } 
  case class LetDefineBlock(idn: SIdn, e: SExp) { override def toString = "(" + idn.toString() + " " + e.toString() + ")" }

  // Clases used in closure conversion
  case class SMakeEnv(idns: List[SIdn]) extends SExp { override def toString = { "(make-env" + (if (idns.size == 0) ")" else " " + idns.mkString(" ") + ")") }}
  case class SMakeLambda(lambda: SLambda, env: SExp) extends SExp { override def toString = "(make-lambda " + lambda.toString + " " + env.toString + ")" }
  case class SNth(n: Int, e: SExp) extends SExp { override def toString = "(nth " + n.toString + " " + e.toString + ")" }
  case class SGetEnv(e: SExp) extends SExp { override def toString = "(get-env " + e.toString + ")" }
  case class SGetProc(e: SExp) extends SExp { override def toString = "(get-proc " + e.toString + ")" }


  // Class used in the hoisting phase
  case class SHoistedLambda(f: SIdn, env: SExp) extends SExp { override def toString = "(hoisted-lambda " + f.toString + " " + env.toString + ")" }


  // Asm.js AST
  case class AModule(fs: List[AFunction], ftables: Map[String, List[String]]) { 
    // TODO inline all these functions for performance
    override def toString = { 
      """
      function AsmModule(stdlib, foreign, heap) {
      "use asm";

      var mem_top = 0.0;
      var H32 = new stdlib.Int32Array(heap);
      var D32 = new stdlib.Float32Array(heap);

      var log = foreign.consoleDotLog;

      var imul = stdlib.Math.imul;
      var sqrt = stdlib.Math.sqrt;
      var floor = stdlib.Math.floor;


      function alloc(size) {
          size = +size;
          
          var current_mem_top = 0.0;

          current_mem_top = mem_top;
          mem_top = +(mem_top + size);


          return +current_mem_top;
      }

      function min(a, b) {
        a = +a;
        b = +b;

        return +(a<b?a:b);
      }

      function max(a, b) {
        a = +a;
        b = +b;

        return +(a>b?a:b);
      }

      
      """ +
      fs.mkString("\n") + 
      "\n\n" + 
      ftables.map{case (ftable, fnames) => "var "+ftable+ " = [" + fnames.mkString(",") + "];"}.mkString("\n") +
      "\n\n" +
      "return {" +
      fs.filter{f => !f.name.startsWith("func") && !f.name.endsWith("_copy")}.map{f => f.name + ": " + f.name + "_copy"}.mkString(", ") +
      "};\n" +
      "}"
    }
  }
  
  case class AFunction(name: String, params: List[AIdn], instructions: List[AStatement]) { 
    override def toString = { 
      val lv = local_vars(instructions)
      "function " + name + "(" + params.mkString(", ") + ")" + 
      "{\n" + params.map{p => p.toString() + " = +" + p.toString() + ";\n"}.mkString("") + "\n" +
      (if (lv.size > 0) "var " + lv.mkString(" = 0.0, ") + " = 0.0;\n\n"; else "") +
      instructions.map{i => i.toString() + ";\n"}.mkString("") + "\n}" 
    }
  }

  def local_vars(instructions: List[AStatement]) : Set[Idn] = instructions match {
    case Nil => Set()
    case (i::is) => local_vars(is) ++ (i match {
      case AVarAssignment(i, v) => if (i.idn == "mem_top") Set() else Set(i.idn)
      case AIf(cond, block1, block2) => local_vars(List(cond)) ++ local_vars(block1) ++ local_vars(block2)
      case _ => Set()
    })
  }


  abstract class AStatement
  case class AVarAssignment(idn: AIdn, value: AExp) extends AStatement { override def toString = { idn.toString + " = " + value.toString() }}
  case class AArrayAssignment(base: AExp, offset: AExp, value: AExp) extends AStatement { override def toString = { "D32[(~~+floor(+(" + base.toString() + " + " + offset.toString() + "))|0) << 2 >> 2] = +(" + value.toString() + ")"}}
  case class AIf(cond: AExp, block1: List[AStatement], block2: List[AStatement]) extends AStatement { override def toString = { "if (" + ADoubleToInt(cond).toString() + ") {\n" + block1.map{i => i.toString() + ";\n"}.mkString("") + "} else {\n" + block2.map{i => i.toString() + ";\n"}.mkString("") + "}" }}
  case class AReturn(s: AStatement) extends AStatement { override def toString = { "return +" + s.toString() }}

  abstract class AExp extends AStatement
  case class AIdn(idn: Idn) extends AExp { override def toString = { idn }}
  // TODO remove this class
  case class AStaticValue(d: Double) extends AExp { override def toString = { "(+"+d.toString()+")" }}
  case class ADoubleToInt(d: AExp) extends AExp { override def toString = { "(~~+floor("+d.toString()+")|0)" }}
  case class AFunctionCallByName(f: AIdn, params: List[AExp]) extends AExp { override def toString = { "(+" + f + "(" + params.mkString(", ") + "))" }}
  case class AFunctionCallByIndex(ftable: AIdn, fpointer: AIdn, mask: Int, params: List[AExp]) extends AExp { override def toString = { "(+" + ftable + "[(" + ADoubleToInt(AHeapAccess(fpointer)).toString + ") & "+mask.toString + "](" + AArrayAccess(fpointer, AStaticValue(1.0)) + ", " + params.mkString(", ") + "))" }}
  // as.size is either 1 or 2
  case class APrimitiveInstruction(op: String, as: List[AExp]) extends AExp { override def toString = op match {
    case "+" | "-" | "*" | "/" => "(+((+("+as(0).toString() +"))" + op + "(+(" + as(1).toString()+"))))"
    case "<" | ">" => "+(((+(" + as(0).toString() + "))" + op + "(+(" + as(1).toString() + ")))|0)"
    case "neg" => "(+(-("+as(0)+")))"
    case "min" | "max" => "(+"+op+"(+("+as(0).toString()+"), +("+as(1).toString()+")))"
    case "sqrt" => "(+"+op+"(+("+as(0)+")))"
    case _ => as(0).toString() + op + as(1).toString()
  }}
  case class AHeapAccess(index: AExp) extends AExp { override def toString = { AArrayAccess(AStaticValue(0.0), index).toString }}
  case class AArrayAccess(base_address: AExp, offset: AExp) extends AExp { override def toString = { "(+D32[(~~+floor(+(" + base_address.toString() + " + " + offset.toString() + "))|0) << 2 >> 2])" }}
  // TODO size should be of type AStaticValue
  case class AAlloc(size: Int) extends AExp { override def toString = { "(+alloc(+"+size.toString()+"))" }}

}

