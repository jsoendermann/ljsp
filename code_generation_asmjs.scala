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
    m.functions.map{asmjs_function_to_string}.mkString("\n") + 
    "\n\n" + 
    m.ftables.map{case (ftable, fnames) => "var "+ftable+ " = [" + fnames.mkString(",") + "];"}.mkString("\n") +
    "\n\n" +
    "return {" +
    m.functions.filter{f => !f.name.startsWith("func") && !f.name.endsWith("_copy")}.map{f => f.name + ": " + f.name + "_copy"}.mkString(", ") +
    "};\n" +
    "}\n" + 
    "var module = " + 
    m.name + 
    "({ Math: Math, Float32Array: Float32Array}, {}, new ArrayBuffer(10*4096));"
  }

  def asmjs_function_to_string(f: AFunction) : String = {
    def local_vars(statements: List[AStatement]) : Set[Idn] = statements match {
      case Nil => Set()
      case (i::is) => local_vars(is) ++ (i match {
        case AVarAssignment(i, v) => if (i.idn == "mem_top") Set() else Set(i.idn)
        case AIf(cond, block1, block2) => local_vars(List(cond)) ++ local_vars(block1) ++ local_vars(block2)
        case _ => Set()
      })
    }

    val lv = local_vars(f.statements)

    "function " + f.name + "(" + f.params.map{asmjs_exp_to_string}.mkString(", ") + ")" + 
    "{\n" + 
    f.params.map{p => asmjs_exp_to_string(p) + " = +" + asmjs_exp_to_string(p) + ";\n"}.mkString("") + 
    "\n" +
    (if (lv.size > 0) 
      "var " + lv.mkString(" = 0.0, ") + " = 0.0;\n\n"; 
    else 
      "") +
    f.statements.map{i => asmjs_statement_to_string(i) + ";\n"}.mkString("") + 
    "\n}"
  }


  def asmjs_statement_to_string(s: AStatement) : String = s match {
    case AVarAssignment(idn, value) => asmjs_exp_to_string(idn) + " = " + asmjs_exp_to_string(value)
    case AArrayAssignment(base, offset, value) => {
      "D32[" + 
      asmjs_exp_to_string(ADoubleToInt(APrimitiveInstruction("+", List(base, offset)))) + 
      " << 2 >> 2] = " + asmjs_exp_to_string(value)
    }
    case AIf(cond, block1, block2) => {
      "if (" + asmjs_exp_to_string(ADoubleToInt(cond)) + ")\n" +
      "{\n" +
      block1.map{s => asmjs_statement_to_string(s) + ";\n"}.mkString("") +
      "} else {\n" +
      block2.map{s => asmjs_statement_to_string(s) + ";\n"}.mkString("") +
      "}"
    }
    case AReturn(s) => "return " + asmjs_statement_to_string(s)

    case _ => asmjs_exp_to_string(s.asInstanceOf[AExp])
  }


  def asmjs_exp_to_string(e: AExp) : String = e match {
    case AIdn(idn) => idn
    // TODO remove this case
    case AVarAccess(v) => "(+" + asmjs_exp_to_string(v) + ")"
    case AStaticValue(d) => "(+" + d.toString + ")"
    case ADoubleToInt(e) => "(~~+floor(" + asmjs_exp_to_string(e) + ")|0)"
    case AFunctionCallByName(f, params) => {
      "(+" + asmjs_exp_to_string(f) + "(" + 
      params.map{asmjs_exp_to_string}.mkString(", ") + "))"
    }
    case AFunctionCallByIndex(ftable, fpointer, mask, params) => {
      "(+" + ftable + 
      "[" + asmjs_exp_to_string(ADoubleToInt(AHeapAccess(fpointer))) + " & "+mask.toString + "]" + 
      "(" + asmjs_exp_to_string(AArrayAccess(fpointer, AStaticValue(1.0))) + ", " + 
      params.map{asmjs_exp_to_string}.mkString(", ") + "))"
    }
    case APrimitiveInstruction(op, as) => op match {
      case "+" | "-" | "*" | "/" => "(+(" + asmjs_exp_to_string(as(0)) + op + asmjs_exp_to_string(as(1)) + "))"
      case "<" | ">" => "(+((" + asmjs_exp_to_string(as(0)) + op + asmjs_exp_to_string(as(1)) + ")|0))"
      case "neg" => "(+(-("+asmjs_exp_to_string(as(0))+")))"
      case "min" => {
        "(+(" + asmjs_exp_to_string(as(0)) + "<" + asmjs_exp_to_string(as(1)) + "?" +
        asmjs_exp_to_string(as(0)) + ":" + asmjs_exp_to_string(as(1)) +
        "))"
      }
      case "max" => {
        "(+(" + asmjs_exp_to_string(as(0)) + ">" + asmjs_exp_to_string(as(1)) + "?" +
        asmjs_exp_to_string(as(0)) + ":" + asmjs_exp_to_string(as(1)) +
        "))"
      }
      case "sqrt" => "(+"+op+"("+asmjs_exp_to_string(as(0))+"))"
      case _ => throw new IllegalArgumentException("Operation " + op + " not implemented.")
    }
    case AHeapAccess(index) => asmjs_exp_to_string(AArrayAccess(AStaticValue(0.0), index))
    case AArrayAccess(base, offset) => {
      "(+D32[" + 
      asmjs_exp_to_string(ADoubleToInt(APrimitiveInstruction("+", List(base, offset)))) + 
      " << 2 >> 2])"
    }
    case AAlloc(size) => "(+alloc(+"+size.toString()+"))"
  }
}
