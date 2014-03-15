package ljsp

import ljsp.AST._

import ljsp.code_generation_ljsp._
import ljsp.code_generation_asmjs._
import ljsp.code_generation_ir._
import ljsp.code_generation_c._
import ljsp.code_generation_llvm_ir._

import ljsp.parser._

import ljsp.expand_let_ns._
import ljsp.remove_negs._
import ljsp.cps_translation._
import ljsp.closure_conversion._
import ljsp.hoisting._
import ljsp.ir_conversion._
import ljsp.asmjs_conversion._
import ljsp.c_conversion._
import ljsp.llvm_ir_conversion._
import ljsp.numbered_llvm_ir_conversion._

import java.io._

object Ljsp {
  def main(args: Array[String]) {
    // TODO switches to: minify asm.js and c
    def parseOptions(parsed_options: Map[Symbol, String], arglist: List[String]) : Map[Symbol, String] = arglist match {
      case "-i" :: in_file :: tail => parseOptions(parsed_options ++ Map('in_file -> in_file), tail)
      case "-o" :: out_file :: tail => parseOptions(parsed_options ++ Map('out_file -> out_file), tail)
      // TODO switch for expand prim ops
      case "--parsed" :: tail => parseOptions(parsed_options ++ Map('target -> "parsed"), tail)
      case "--letExp" :: tail => parseOptions(parsed_options ++ Map('target -> "letExp"), tail)
      case "--negsRemoved" :: tail => parseOptions(parsed_options ++ Map('target -> "negsRemoved"), tail)
      case "--cps" :: tail => parseOptions(parsed_options ++ Map('target -> "cps"), tail)
      case "--cc" :: tail => parseOptions(parsed_options ++ Map('target -> "cc"), tail)
      case "--hoist" :: tail => parseOptions(parsed_options ++ Map('target -> "hoist"), tail)
      case "--ir" :: tail => parseOptions(parsed_options ++ Map('target -> "ir"), tail)
      case "--asmjs" :: tail => parseOptions(parsed_options ++ Map('target -> "asmjs"), tail)
      case "--c" :: tail => parseOptions(parsed_options ++ Map('target -> "c"), tail)
      case "--llvmIr" :: tail => parseOptions(parsed_options ++ Map('target -> "llvmIr"), tail)
      case "--numLlvmIr" :: tail => parseOptions(parsed_options ++ Map('target -> "numLlvmIr"), tail)
      case prog :: tail => parseOptions(parsed_options ++ Map('prog -> prog), tail)
      case Nil => parsed_options
    }

    def parsedProg(prog: String) : SProgram = JLispParsers.parseExpr(prog)
    def letExpandedProg(prog: String) : SProgram = expand_let_ns_prog(parsedProg(prog))
    def negsRemovedProg(prog: String) : SProgram = remove_negs_prog(letExpandedProg(prog))
    def cpsTranslatedProg(prog: String) : SProgram = cps_trans_prog(negsRemovedProg(prog), (x: SExp) => x)
    def closureConvertedProg(prog: String) : SProgram = cl_conv_prog(cpsTranslatedProg(prog))
    def hoistedProg(prog: String) : SProgram = hoist_prog(closureConvertedProg(prog))
    def asmjsModule(prog: String) : AModule = convert_prog_to_asmjs(hoistedProg(prog))
    def irModule(prog: String) : IModule = convert_prog_to_ir(hoistedProg(prog))
    def cModule(prog: String) : CModule = convert_module_to_c(irModule(prog))
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
            case None => throw new Exception("No input provided")
          }
        }
      }
    }

    def getOutput(options: Map[Symbol, String], prog: String) : String = {
      options.get('target) match {
        case Some("parsed") => ljsp_prog_to_string(parsedProg(prog))
        case Some("letExp") => ljsp_prog_to_string(letExpandedProg(prog))
        case Some("negsRemoved") => ljsp_prog_to_string(negsRemovedProg(prog))
        case Some("cps") => ljsp_prog_to_string(cpsTranslatedProg(prog))
        case Some("cc") => ljsp_prog_to_string(closureConvertedProg(prog))
        case Some("hoist") => ljsp_prog_to_string(hoistedProg(prog))
        case Some("ir") => ir_module_to_string(irModule(prog))
        case Some("asmjs") => asmjs_module_to_string(asmjsModule(prog))
        case Some("c") => c_module_to_string(cModule(prog))
        case Some("llvmIr") => llvm_ir_module_to_string(llvmIrModule(prog))
        case Some("numLlvmIr") => llvm_ir_module_to_string(numberedLlvmIrModule(prog))
        // TODO Output every step instead of throwing an exception
        case None => throw new IllegalArgumentException("No target provided")
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
