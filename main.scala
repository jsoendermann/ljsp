package ljsp

import ljsp.AST._

import ljsp.code_generation_ljsp._
import ljsp.code_generation_asmjs._
import ljsp.code_generation_ir._
import ljsp.code_generation_c._
import ljsp.code_generation_llvm_ir._

import ljsp.parser._

import ljsp.expand_let_ns._
import ljsp.reduce_prim_ops._
import ljsp.wrap_funcs_in_lambdas._
import ljsp.cps_translation._
import ljsp.closure_conversion._
import ljsp.hoisting._
import ljsp.ir_conversion._
import ljsp.remove_redundant_assigns_conversion._
import ljsp.asmjs_conversion._
import ljsp.c_conversion._
import ljsp.em_c_conversion._
import ljsp.llvm_ir_conversion._
import ljsp.numbered_llvm_ir_conversion._

import java.io._

object Ljsp {
  def main(args: Array[String]) {
    def printUsage() = {
      val usage = """|Usage: scala -cp ljsp/ ljsp.Ljsp [target] [-o <output_file] [-i <input file> | source]
        |  Targets:
        |    --parsed: Parsed, unaltered code
        |    --letExp: let expressions that define more than one var, e.g. (let ((a 1) (b 2)) <expr>), get expanded
        |              to single lets: (let ((a 1)) (let ((b 2)) <expr>))
        |    --reducePrimOps: Reduces some primitive operations to others, e.g. a >= b to !(a < b)
        |    --wrapFuncsInLambdas: Wraps function variables in lambdas. If the program contains a function called
        |                          'succ', a function call such as (apply succ 1) would get converted to
        |                          (apply (lambda (n) (succ n)) 1)
        |    --cps: Code in Continuation-Passing Style
        |    --cc: Closure converted code (lambdas get their environment in a parameter)
        |    --hoist: Hoisted code (lambdas get lifted to the top level an converted to normal functions)
        |    --ir: Internal intermediate representation
        |    --remRedundantAssignsIr: Remove all redundant assignments of the form var1 = var2 by replacing occurences
        |                             of var1 later in the code with var2
        |    --asmjs: Asm.js code
        |    --c: C code 
        |    --emC: C code that removes any expression given in addition to the defines, includes jalloc.c and adds
        |           another function for every function in the original code that takes and returns doubles instead
        |           of void pointers. This is meant to be compiled to asm.js using emscripten
        |    --llvmIr: LLVM IR code with named register vars
        |    --numLlvmIr: LLVM IR code with numbered register vars""".stripMargin
        println(usage)
    }
    def parseOptions(parsed_options: Map[Symbol, String], arglist: List[String]) : Map[Symbol, String] = arglist match {
      case "-i" :: in_file :: tail => parseOptions(parsed_options ++ Map('in_file -> in_file), tail)
      case "-o" :: out_file :: tail => parseOptions(parsed_options ++ Map('out_file -> out_file), tail)
      case "--parsed" :: tail => parseOptions(parsed_options ++ Map('target -> "parsed"), tail)
      case "--letExp" :: tail => parseOptions(parsed_options ++ Map('target -> "letExp"), tail)
      case "--reducePrimOps" :: tail => parseOptions(parsed_options ++ Map('target -> "reducePrimOps"), tail)
      case "--wrapFuncsInLambdas" :: tail => parseOptions(parsed_options ++ Map('target -> "wrapFuncsInLambdas"), tail)
      case "--cps" :: tail => parseOptions(parsed_options ++ Map('target -> "cps"), tail)
      case "--cc" :: tail => parseOptions(parsed_options ++ Map('target -> "cc"), tail)
      case "--hoist" :: tail => parseOptions(parsed_options ++ Map('target -> "hoist"), tail)
      case "--ir" :: tail => parseOptions(parsed_options ++ Map('target -> "ir"), tail)
      case "--remRedundantAssignsIr" :: tail => parseOptions(parsed_options ++ Map('target -> "remRedundantAssignsIr"), tail)
      case "--asmjs" :: tail => parseOptions(parsed_options ++ Map('target -> "asmjs"), tail)
      case "--c" :: tail => parseOptions(parsed_options ++ Map('target -> "c"), tail)
      case "--emC" :: tail => parseOptions(parsed_options ++ Map('target -> "emC"), tail)
      case "--llvmIr" :: tail => parseOptions(parsed_options ++ Map('target -> "llvmIr"), tail)
      case "--numLlvmIr" :: tail => parseOptions(parsed_options ++ Map('target -> "numLlvmIr"), tail)
      case prog :: tail => parseOptions(parsed_options ++ Map('prog -> prog), tail)
      case Nil => parsed_options
    }

    def parsedProg(prog: String) : SProgram = JLispParsers.parseExpr(prog)
    def letExpandedProg(prog: String) : SProgram = expand_let_ns_prog(parsedProg(prog))
    def reducePrimOpsProg(prog: String) : SProgram = reduce_prim_ops_prog(letExpandedProg(prog))
    def wrapFuncsInLambdasProg(prog: String) : SProgram = wrap_funcs_in_lambdas_prog(reducePrimOpsProg(prog))
    def cpsTranslatedProg(prog: String) : SProgram = cps_trans_prog(wrapFuncsInLambdasProg(prog), (x: SExp) => x)
    def closureConvertedProg(prog: String) : SProgram = cl_conv_prog(cpsTranslatedProg(prog))
    def hoistedProg(prog: String) : SProgram = hoist_prog(closureConvertedProg(prog))
    def irModule(prog: String) : IModule = convert_prog_to_ir(hoistedProg(prog))
    def remRedAssignsIr(prog: String) : IModule = convert_module_to_rra_ir(irModule(prog))
    def asmjsModule(prog: String) : AModule = convert_module_to_asmjs(remRedAssignsIr(prog))
    def cModule(prog: String) : CModule = convert_module_to_c(remRedAssignsIr(prog))
    def emCModule(prog: String) : CModule = convert_module_to_em_c(cModule(prog))
    def llvmIrModule(prog: String) : LModule = convert_module_to_llvm_ir(cModule(prog))
    def numberedLlvmIrModule(prog: String) : LModule = convert_module_to_numbered_llvm_ir(llvmIrModule(prog))

    def getProgram(options: Map[Symbol, String]) : String = {
      options.get('prog) match {
        case Some(prog) => prog
        case None => {
          options.get('in_file) match {
            case Some(in_file) => {
              val sourceFile = scala.io.Source.fromFile(in_file)
              val prog = sourceFile.mkString
              sourceFile.close()
              prog
            }
            case None => {
              printUsage()
              sys.exit(1)
            }
          }
        }
      }
    }

    def getOutput(options: Map[Symbol, String], prog: String) : String = {
      options.get('target) match {
        case Some("parsed") => ljsp_prog_to_string(parsedProg(prog))
        case Some("letExp") => ljsp_prog_to_string(letExpandedProg(prog))
        case Some("reducePrimOps") => ljsp_prog_to_string(reducePrimOpsProg(prog))
        case Some("wrapFuncsInLambdas") => ljsp_prog_to_string(wrapFuncsInLambdasProg(prog))
        case Some("cps") => ljsp_prog_to_string(cpsTranslatedProg(prog))
        case Some("cc") => ljsp_prog_to_string(closureConvertedProg(prog))
        case Some("hoist") => ljsp_prog_to_string(hoistedProg(prog))
        case Some("ir") => ir_module_to_string(irModule(prog))
        case Some("remRedundantAssignsIr") => ir_module_to_string(remRedAssignsIr(prog))
        case Some("asmjs") => asmjs_module_to_string(asmjsModule(prog))
        case Some("c") => c_module_to_string(cModule(prog), false)
        case Some("emC") => c_module_to_string(emCModule(prog), true)
        case Some("llvmIr") => llvm_ir_module_to_string(llvmIrModule(prog))
        case Some("numLlvmIr") => llvm_ir_module_to_string(numberedLlvmIrModule(prog))
        case None => {
          "Parsed code:\n" +
          ljsp_prog_to_string(parsedProg(prog)) +
          "\n\n" +
          "Let expanded code:\n" +
          ljsp_prog_to_string(letExpandedProg(prog)) +
          "\n\n" +
          "Code with prim ops reduced:\n" +
          ljsp_prog_to_string(reducePrimOpsProg(prog)) +
          "\n\n" +
          "Code with functions wrapped in lambdas:\n" + 
          ljsp_prog_to_string(wrapFuncsInLambdasProg(prog)) +
          "\n\n" +
          "CPS translated code:\n" +
          ljsp_prog_to_string(cpsTranslatedProg(prog)) +
          "\n\n" +
          "Closure converted code:\n" +
          ljsp_prog_to_string(closureConvertedProg(prog)) +
          "\n\n" +
          "Hoisted code:\n" +
          ljsp_prog_to_string(hoistedProg(prog)) +
          "\n\n" +
          "IR module:\n" +
          ir_module_to_string(irModule(prog)) +
          "\n\n" +
          "IR with redundant assignments removed:\n" +
          ir_module_to_string(remRedAssignsIr(prog)) +
          "\n\n" +
          "asm.js module:\n" +
          asmjs_module_to_string(asmjsModule(prog)) +
          "\n\n" +
          "C code:\n" +
          c_module_to_string(cModule(prog), false) +
          "\n\n" +
          "em C code:\n" +
          c_module_to_string(emCModule(prog), true) +
          "\n\n" +
          "LLVM IR code:\n" +
          llvm_ir_module_to_string(llvmIrModule(prog)) +
          "\n\n" +
          "Numbered LLVM IR code:\n" +
          llvm_ir_module_to_string(numberedLlvmIrModule(prog)) +
          "\n\n"
        }
      }
    }

    val options = parseOptions(Map(), args.toList)
    val output = getOutput(options, getProgram(options))

    options.get('out_file) match {
      case Some(out_file) => {
        val writer = new PrintWriter(out_file, "UTF-8")
        writer.println(output)
        writer.close()
      }
      case None => println(output)
    } 
  }
}
