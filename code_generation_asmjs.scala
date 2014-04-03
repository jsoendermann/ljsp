package ljsp

import ljsp.AST._

object code_generation_asmjs {

  def asmjs_module_to_string(m: AModule) : String = {
    // TODO inline alloc
    "function " + m.name + "(stdlib, foreign, heap) {" +
    """
    "use asm";

    var mem_top = 0.0;
    var D32 = new stdlib.Float32Array(heap);

    var sqrt = stdlib.Math.sqrt;
    var floor = stdlib.Math.floor;


    function alloc(size) {
        size = +size;
        
        var current_mem_top = 0.0;

        current_mem_top = mem_top;
        mem_top = +(mem_top + size);


        return +current_mem_top;
    }
    
    """ +
    m.functions.map{asmjs_function_to_string(_, m)}.mkString("\n") + 
    "\n\n" + 
    m.ftables.map{case (ftable, fnames) => "var "+ftable+ " = [" + fnames.mkString(",") + "];"}.mkString("\n") +
    "\n\n" +
    "return {" +
    m.functions.filter{f => !f.name.startsWith("func") && !f.name.endsWith("_copy")}.map{f => f.name + ": " + f.name + "_copy"}.mkString(", ") +
    "};\n" +
    "}\n" + 
    "var jModule = " + 
    m.name + 
    "({ Math: Math, Float32Array: Float32Array}, {}, new ArrayBuffer(10*4096));"
  }

  def asmjs_function_to_string(f: AFunction, m: AModule) : String = {
    def local_vars(statements: List[AStatement]) : Set[Idn] = statements match {
      case Nil => Set()
      case (i::is) => local_vars(is) ++ (i match {
        case AVarAssignment(i, v) => if (i == "mem_top") Set() else Set(i)
        case AIf(cond, block1, block2) => local_vars(block1) ++ local_vars(block2)
        case _ => Set()
      })
    }

    val lv = local_vars(f.statements)

    "function " + f.name + "(" + f.params.mkString(", ") + ")" + 
    "{\n" + 
    f.params.map{p => p + " = +" + p + ";\n"}.mkString("") + 
    "\n" +
    (if (lv.size > 0) 
      "var " + lv.mkString(" = 0.0, ") + " = 0.0;\n\n"; 
    else 
      "") +
    f.statements.map{i => asmjs_statement_to_string(i, m) + ";\n"}.mkString("") + 
    "\n}"
  }


  def asmjs_statement_to_string(s: AStatement, m: AModule) : String = s match {
    case AVarAssignment(idn, value) => idn + " = " + asmjs_exp_to_string(value, m)
    case AArrayAssignment(base, offset, value) => {
      "D32[" + 
      asmjs_exp_to_string(ADoubleToInt(APrimitiveInstruction("+", List(AIdn(base), AStaticValue(offset)))), m) + 
      " << 2 >> 2] = " + asmjs_exp_to_string(value, m)
    }
    case AIf(cond, block1, block2) => {
      "if (" + asmjs_exp_to_string(ADoubleToInt(AIdn(cond)), m) + ")\n" +
      "{\n" +
      block1.map{s => asmjs_statement_to_string(s, m) + ";\n"}.mkString("") +
      "} else {\n" +
      block2.map{s => asmjs_statement_to_string(s, m) + ";\n"}.mkString("") +
      "}"
    }
    case AReturn(s) => "return +(" + asmjs_statement_to_string(s, m) + ")"

    case _ => asmjs_exp_to_string(s.asInstanceOf[AExp], m)
  }


  def asmjs_exp_to_string(e: AExp, m: AModule) : String = e match {
    case AIdn(idn) => idn
    case AStaticValue(d) => "(" + d.toString + ")"
    case ADoubleToInt(e) => "(~~+floor(" + asmjs_exp_to_string(e, m) + ")|0)"
    case AFunctionCallByName(f, params) => {
      "(+" + f + "(" + 
      params.map{asmjs_exp_to_string(_, m)}.mkString(", ") + "))"
    }
    case AFunctionCallByIndex(ftable, fpointer, params) => {
      val mask = m.ftables(ftable).size-1

      "(+" + ftable + 
      "[" + asmjs_exp_to_string(ADoubleToInt(AHeapAccess(fpointer)), m) + " & "+mask.toString + "]" + 
      "(" + asmjs_exp_to_string(AArrayAccess(fpointer, 1), m) + ", " + 
      params.map{asmjs_exp_to_string(_, m)}.mkString(", ") + "))"
    }
    case APrimitiveInstruction(op, as) => op match {
      case "+" | "-" | "*" | "/" => "(+(" + asmjs_exp_to_string(as(0), m) + op + asmjs_exp_to_string(as(1), m) + "))"
      case "<" | ">" => "(+((" + asmjs_exp_to_string(as(0), m) + op + asmjs_exp_to_string(as(1), m) + ")|0))"
      case "=" => "(+((" + asmjs_exp_to_string(as(0), m) + "==" + asmjs_exp_to_string(as(1), m) + ")|0))"
      case "neg" => "(+(-("+asmjs_exp_to_string(as(0), m)+")))"
      case "min" => {
        "(+(" + asmjs_exp_to_string(as(0), m) + "<" + asmjs_exp_to_string(as(1), m) + "?" +
        asmjs_exp_to_string(as(0), m) + ":" + asmjs_exp_to_string(as(1), m) +
        "))"
      }
      case "max" => {
        "(+(" + asmjs_exp_to_string(as(0), m) + ">" + asmjs_exp_to_string(as(1), m) + "?" +
        asmjs_exp_to_string(as(0), m) + ":" + asmjs_exp_to_string(as(1), m) +
        "))"
      }
      case "sqrt" => "(+"+op+"("+asmjs_exp_to_string(as(0), m)+"))"
      case _ => throw new IllegalArgumentException("Operation " + op + " not implemented.")
    }
    case AHeapAccess(index) => {
      "(+D32[" + 
      asmjs_exp_to_string(ADoubleToInt(AIdn(index)), m) + 
      " << 2 >> 2])"
    }
    case AArrayAccess(base, offset) => {
      "(+D32[" + 
      asmjs_exp_to_string(ADoubleToInt(APrimitiveInstruction("+", List(AIdn(base), AStaticValue(offset)))), m) + 
      " << 2 >> 2])"
    }
    case AAlloc(size) => "(+alloc(+"+size.toString()+"))"
  }
}
