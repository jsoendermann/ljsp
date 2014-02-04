package ljsp

object AST {

  type Num = Int
  type Idn = String


  abstract class SExp

  case class SProgram(ds: List[SDefine], e: SExp) extends SExp { override def toString = "; defines\n" + ds.mkString("\n") + "\n; expression\n" + e.toString }

  case class SDefine(name: SIdn, params: List[SIdn], e: SExp) extends SExp { override def toString = "(define (" + name.toString() + " " +  params.mkString(" ") + ") " + e.toString() + ")" }

  case class SIdn(idn: Idn) extends SExp { override def toString = idn }
  case class SInt(i: Num) extends SExp { override def toString = i.toString() }
  // TODO double
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

      var mem_top = 0;
      var H32 = new stdlib.Int32Array(heap);

      var log = foreign.consoleDotLog;
      var imul = stdlib.Math.imul;


      function alloc(size) {
          size = size|0;
          
          var current_mem_top = 0;

          current_mem_top = mem_top;
          mem_top = (mem_top + size)|0;


          return current_mem_top|0;
      }

      function get_array_element(base, offset) {
          base = base|0;
          offset = offset|0;

          return H32[((base + offset)|0) << 2 >> 2]|0;
      }


      function set_array_element(base, offset, value) {
          base = base|0;
          offset = offset |0;
          value = value|0;

          H32[((base + offset)|0) << 2 >> 2] = value;
      }

      function make_hoisted_lambda(f_index, env_pointer) {
          f_index = f_index|0;
          env_pointer = env_pointer|0;

          var a = 0;

          a = alloc(2)|0;
          set_array_element(a, 0, f_index);
          set_array_element(a, 1, env_pointer);
          return a|0;
      }

      function make_env_0() {
          return mem_top|0;
      }

      function make_env_1(v1) {
          v1 = v1|0;

          var a = 0;

          a = alloc(1)|0;
          set_array_element(a, 0, v1);
          return a|0;
      }

      function make_env_2(v1, v2) {
          v1 = v1|0;
          v2 = v2|0;

          var a = 0;

          a = alloc(2)|0;
          set_array_element(a, 0, v1);
          set_array_element(a, 1, v2);
          return a|0;
      }

      function make_env_3(v1, v2, v3) {
          v1 = v1|0;
          v2 = v2|0;
          v3 = v3|0;

          var a = 0;

          a = alloc(3)|0;
          set_array_element(a, 0, v1);
          set_array_element(a, 1, v2);
          set_array_element(a, 2, v3);
          return a|0;
      }
      
      function make_env_4(v1, v2, v3, v4) {
          v1 = v1|0;
          v2 = v2|0;
          v3 = v3|0;
          v4 = v4|0;

          var a = 0;

          a = alloc(4)|0;
          set_array_element(a, 0, v1);
          set_array_element(a, 1, v2);
          set_array_element(a, 2, v3);
          set_array_element(a, 3, v4);
          return a|0;
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
      "{\n" + params.map{p => p.toString() + " = " + p.toString() + "|0;\n"}.mkString("") + "\n" +
      (if (lv.size > 0) "var " + lv.mkString(" = 0, ") + " = 0;\n\n"; else "") +
      instructions.map{i => i.toString() + ";\n"}.mkString("") + "\n}" 
    }
  }

  abstract class AStatement
  case class AVarAssignment(idn: AIdn, value: AExp) extends AStatement { override def toString = { idn.toString + " = " + value.toString() }}
  case class AHeapAssignment(index: AExp, value: AExp) extends AStatement { override def toString = { "set_array_element(0, "+index+", "+value+")" }}
  //case class AArrayAssignment(base: AExp, arr_index: AExp, value: AExp) extends AStatement { override def toString = { "set_array_element(" + base.toString() + ", " + arr_index.toString() + ", " + value.toString() + ")" }}
  case class AIf(cond: AExp, block1: List[AStatement], block2: List[AStatement]) extends AStatement { override def toString = { "if (" + cond.toString() + ") {\n" + block1.map{i => i.toString() + ";\n"}.mkString("") + "} else {\n" + block2.map{i => i.toString() + ";\n"}.mkString("") + "}" }}
  case class AReturn(s: AStatement) extends AStatement { override def toString = { "return " + s.toString() + "|0" }}

  abstract class AExp extends AStatement
  // TODO remove this class
  case class AStaticValue(i: Int) extends AExp { override def toString = { i.toString() }}
  case class AFunctionCallByName(f: AIdn, params: List[AExp]) extends AExp { override def toString = { "(" + f + "(" + params.mkString(", ") + ")|0)" }}
  case class AFunctionCallByIndex(ftable: AIdn, fpointer: AIdn, mask: Int, params: List[AExp]) extends AExp { override def toString = { "(" + ftable + "[" + AHeapAccess(AVarAccess(fpointer)).toString + "&"+mask.toString + "](" + AArrayAccess(AVarAccess(fpointer), AStaticValue(1)) + ", " + params.mkString(", ") + ")|0)" }}
  case class APrimitiveInstruction(op: String, operand1: AExp, operand2: AExp) extends AExp { override def toString = op match {
    // TODO implement other primitive operations
    case "+" | "-" => "(((("+operand1.toString() +")|0)" + op + "((" + operand2.toString()+")|0))|0)"
    case "*" => "(imul(("+operand1.toString()+")|0,("+operand2.toString()+")|0)|0)"
    case "<" | ">" => "((("+operand1.toString() + ")|0)" + op + "((" + operand2.toString() + ")|0))"
    case _ => operand1.toString() + op + operand2.toString()
  }}
  // TODO remove this class, use AIdn directly
  case class AVarAccess(idn: AIdn) extends AExp { override def toString = { idn.toString }}
  case class AHeapAccess(index: AExp) extends AExp { override def toString = { AArrayAccess(AStaticValue(0), index).toString }}
  case class AArrayAccess(base_address: AExp, offset: AExp) extends AExp { override def toString = { "(get_array_element("+base_address+", "+offset+")|0)" }}
  case class AMakeEnv(values: List[AExp]) extends AExp { override def toString = { "(make_env_"+values.size.toString() + "(" + values.mkString(", ") + ")|0)"}}
  case class AMakeHoistedLambda(f_index: AExp, env_pointer: AExp) extends AExp { override def toString = { "(make_hoisted_lambda(" + f_index.toString() + ", " + env_pointer.toString() + ")|0)"}}
  case class AAlloc(size: Int) extends AExp { override def toString = { "(alloc("+size.toString()+")|0)" }}

  def local_vars(instructions: List[AStatement]) : Set[Idn] = instructions match {
    case Nil => Set()
    case (i::is) => local_vars(is) ++ (i match {
      case AVarAssignment(i, v) => Set(i.idn)
      case AIf(cond, block1, block2) => local_vars(List(cond)) ++ local_vars(block1) ++ local_vars(block2)
      case _ => Set()
    })
  }
}

