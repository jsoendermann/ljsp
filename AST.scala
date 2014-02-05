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
  // TODO more than one variable let


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

      function get_array_element(base, offset) {
          base = +base;
          offset = +offset;

          var addr = 0.0;

          addr = +(base + offset);
          return +D32[(~~+floor(addr)|0) << 2 >> 2];
      }


      function set_array_element(base, offset, value) {
          base = +base;
          offset = +offset;
          value = +value;
          
          var addr = 0.0;

          addr = +(base + offset);
          D32[(~~+floor(addr)|0) << 2 >> 2] = value;
      }

      function make_hoisted_lambda(f_index, env_pointer) {
          f_index = +f_index;
          env_pointer = +env_pointer;

          var a = 0.0;

          a = +alloc(2.0);
          set_array_element(a, 0.0, f_index);
          set_array_element(a, 1.0, env_pointer);
          return +a;
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
  case class AIdn(idn: Idn) { override def toString = { idn }}
  // TODO reset mem_top at the beginning of *_copy functions
  case class AFunction(name: String, params: List[AIdn], instructions: List[AStatement]) { 
    override def toString = { 
      val lv = local_vars(instructions)
      "function " + name + "(" + params.mkString(", ") + ")" + 
      "{\n" + params.map{p => p.toString() + " = +" + p.toString() + ";\n"}.mkString("") + "\n" +
      (if (lv.size > 0) "var " + lv.mkString(" = 0.0, ") + " = 0.0;\n\n"; else "") +
      instructions.map{i => i.toString() + ";\n"}.mkString("") + "\n}" 
    }
  }

  abstract class AStatement
  case class AVarAssignment(idn: AIdn, value: AExp) extends AStatement { override def toString = { idn.toString + " = " + value.toString() }}
  case class AHeapAssignment(index: AExp, value: AExp) extends AStatement { override def toString = { "set_array_element(0.0, "+index+", "+value+")" }}
  case class AArrayAssignment(base: AExp, offsete: AExp, value: AExp) extends AStatement { override def toString = { "set_array_element(" + base.toString() + ", " + offsete.toString() + ", " + value.toString() + ")" }}
  case class AIf(cond: AExp, block1: List[AStatement], block2: List[AStatement]) extends AStatement { override def toString = { "if (" + ADoubleToInt(cond).toString() + ") {\n" + block1.map{i => i.toString() + ";\n"}.mkString("") + "} else {\n" + block2.map{i => i.toString() + ";\n"}.mkString("") + "}" }}
  case class AReturn(s: AStatement) extends AStatement { override def toString = { "return +" + s.toString() }}

  abstract class AExp extends AStatement
  // TODO remove this class
  case class AStaticValue(d: Double) extends AExp { override def toString = { "(+"+d.toString()+")" }}
  case class ADoubleToInt(d: AExp) extends AExp { override def toString = { "(~~+floor("+d.toString()+")|0)" }}
  case class AFunctionCallByName(f: AIdn, params: List[AExp]) extends AExp { override def toString = { "(+" + f + "(" + params.mkString(", ") + "))" }}
  case class AFunctionCallByIndex(ftable: AIdn, fpointer: AIdn, mask: Int, params: List[AExp]) extends AExp { override def toString = { "(+" + ftable + "[(" + ADoubleToInt(AHeapAccess(AVarAccess(fpointer))).toString + ") & "+mask.toString + "](" + AArrayAccess(AVarAccess(fpointer), AStaticValue(1.0)) + ", " + params.mkString(", ") + "))" }}
  // as.size is either 1 or 2
  case class APrimitiveInstruction(op: String, as: List[AExp]) extends AExp { override def toString = op match {
    // TODO implement other primitive operations
    case "+" | "-" | "*" | "/" => "(+((+("+as(0).toString() +"))" + op + "(+(" + as(1).toString()+"))))"
    //case "*" => "(+imul(+("+as(0).toString()+"),+("+as(1).toString()+")))"
    case "<" | ">" => "+(((+(" + as(0).toString() + "))" + op + "(+(" + as(1).toString() + ")))|0)"
    case "neg" => "(+(-("+as(0)+")))"
    case "min" | "max" => "(+"+op+"(+("+as(0).toString()+"), +("+as(1).toString()+")))"
    case "sqrt" => "(+"+op+"(+("+as(0)+")))"
    case _ => as(0).toString() + op + as(1).toString()
  }}
  // TODO remove this class, use AIdn directly
  case class AVarAccess(idn: AIdn) extends AExp { override def toString = { idn.toString }}
  case class AHeapAccess(index: AExp) extends AExp { override def toString = { AArrayAccess(AStaticValue(0.0), index).toString }}
  case class AArrayAccess(base_address: AExp, offset: AExp) extends AExp { override def toString = { "(+get_array_element("+base_address+", "+offset+"))" }}
  // TODO turn this into a statement
  //case class AMakeEnv(values: List[AExp]) extends AExp { override def toString = { "(+make_env_"+values.size.toString() + "(" + values.mkString(", ") + "))"}}
  case class AMakeHoistedLambda(f_index: AExp, env_pointer: AExp) extends AExp { override def toString = { "(+make_hoisted_lambda(" + f_index.toString() + ", " + env_pointer.toString() + "))"}}
  // TODO size should be of type AStaticValue
  case class AAlloc(size: Int) extends AExp { override def toString = { "(+alloc(+"+size.toString()+"))" }}

  def local_vars(instructions: List[AStatement]) : Set[Idn] = instructions match {
    case Nil => Set()
    case (i::is) => local_vars(is) ++ (i match {
      case AVarAssignment(i, v) => Set(i.idn)
      case AIf(cond, block1, block2) => local_vars(List(cond)) ++ local_vars(block1) ++ local_vars(block2)
      case _ => Set()
    })
  }
}

