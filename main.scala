package ljsp

import ljsp.AST._
import ljsp.ljsp_code_generation._
import ljsp.parser._
import ljsp.expand_let_ns._
import ljsp.cps_translation._
import ljsp.closure_conversion._
import ljsp.hoisting._
import ljsp.asmjs_conversion._

object Ljsp {
  def main(args: Array[String]) {

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
        println(module)
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
        println(module)
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

        val module = convert_prog_to_asmjs(progH)
        println(module.toString)
      }
    }
  }
}
