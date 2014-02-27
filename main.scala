package ljsp

import ljsp.AST._

import ljsp.code_generation_ljsp._
import ljsp.code_generation_asmjs._
import ljsp.code_generation_ir._
import ljsp.code_generation_c._
import ljsp.code_generation_llvm_ir._

import ljsp.parser._
import ljsp.expand_let_ns._
import ljsp.cps_translation._
import ljsp.closure_conversion._
import ljsp.hoisting._
import ljsp.ir_conversion._
import ljsp.asmjs_conversion._
import ljsp.c_conversion._
import ljsp.llvm_ir_conversion._

object Ljsp {
  def main(args: Array[String]) {

    /* TODO rewrite this function to accept these switches:
      -i input file
      -o output file
      --cps
      --cc
      --hoist
      --ir
      --asmjs
      --llvmir
      */
    args(0) match {
      case "--expLetN" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progExpand = expand_let_ns_prog(progTree)
        println(ljsp_prog_to_string(progExpand))
      }
      case "--cps" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progExpand = expand_let_ns_prog(progTree)
        val progCps = cps_trans_prog(progExpand, (x: SExp) => x)
        println(ljsp_prog_to_string(progCps))
      }
      case "--cc" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progExpand = expand_let_ns_prog(progTree)
        val progCps = cps_trans_prog(progExpand, (x: SExp) => x)
        val progCC = cl_conv_prog(progCps)
        println(ljsp_prog_to_string(progCC))
      }
      case "--h" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progExpand = expand_let_ns_prog(progTree)
        val progCps = cps_trans_prog(progExpand, (x: SExp) => x)
        val progCC = cl_conv_prog(progCps)
        val progH = hoist_prog(progCC)
        println(ljsp_prog_to_string(progH))
      }
      case "--asmjs" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progExpand = expand_let_ns_prog(progTree)
        val progCps = cps_trans_prog(progExpand, (x: SExp) => x)
        val progCC = cl_conv_prog(progCps)
        val progH = hoist_prog(progCC)
        val module = convert_prog_to_asmjs(progH)
        println(asmjs_module_to_string(module))
      }

      case "-f" => {
        val sourceFile = scala.io.Source.fromFile(args(1))
        val source = sourceFile.mkString
        sourceFile.close()

        val progTree = JLispParsers.parseExpr(source)
        val progExpand = expand_let_ns_prog(progTree)
        val progCps = cps_trans_prog(progExpand, (x: SExp) => x)
        val progCC = cl_conv_prog(progCps)
        val progH = hoist_prog(progCC)
        val module = convert_prog_to_asmjs(progH)
        println(asmjs_module_to_string(module))
      }


      case _ => {
        println("Parsed program:")
        val progTree = JLispParsers.parseExpr(args(0))
        println(ljsp_prog_to_string(progTree))
        println()

        println("LetN expanded program:")
        val progExpand = expand_let_ns_prog(progTree)
        println(ljsp_prog_to_string(progExpand))
        println()

        println("CPS translated program:")
        val progCps = cps_trans_prog(progExpand, (x: SExp) => x)
        println(ljsp_prog_to_string(progCps))
        println()

        println("Closure converted program:")
        val progCC = cl_conv_prog(progCps)
        println(ljsp_prog_to_string(progCC))
        println()

        println("Hoisted program:")
        val progH = hoist_prog(progCC)
        println(ljsp_prog_to_string(progH))
        println()

        println("IR code:")
        val moduleIr = convert_prog_to_ir(progH)
        println(ir_module_to_string(moduleIr))
        println()

        println("C code:")
        val moduleC = convert_module_to_c(moduleIr)
        println(c_module_to_string(moduleC))
        println()

        println("LLVM IR code:")
        val moduleLLVMIR = convert_module_to_llvm_ir(moduleC)
        println(llvm_ir_module_to_string(moduleLLVMIR))
        println()
      }
    }
  }
}
