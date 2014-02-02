package ljsp

import ljsp.AST._
import ljsp.parser._
import ljsp.cps_translation._
import ljsp.closure_conversion._
import ljsp.hoisting._
import ljsp.asmjs_conversion._

object Ljsp {
  def main(args: Array[String]) {

    args(0) match {
      case "--cps" => {
        val progTree = JLispParsers.parseExpr(args(1))
        println(cps_trans_prog(progTree, (r: SExp) => r))
      }
      case "--cc" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progCps = cps_trans_prog(progTree, (x: SExp) => x)
        println(cl_conv_prog(progCps))
      }
      case "--h" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progCps = cps_trans_prog(progTree, (x: SExp) => x)
        val progCC = cl_conv_prog(progCps)
        println(hoist_prog(progCC))
      }
      case "--asmjs" => {
        val progTree = JLispParsers.parseExpr(args(1))
        val progCps = cps_trans_prog(progTree, (x: SExp) => x)
        val progCC = cl_conv_prog(progCps)
        val progH = hoist_prog(progCC)
        val module = convert_prog_to_asmjs(progH)
        println(module)
      }

      case _ => {
        println("Parsed program:")
        val progTree = JLispParsers.parseExpr(args(0))
        println(progTree.toString)
        println()

        println("CPS translated program:")
        val progCps = cps_trans_prog(progTree, (x: SExp) => x)//SAppl(SIdn("display"), List(x)))
        println(progCps.toString)
        println()

        println("Closure converted program:")
        val progCC = cl_conv_prog(progCps)
        println(progCC.toString)
        println()

        println("Hoisted program:")
        val progH = hoist_prog(progCC)
        println(progH.toString)
        println()

        val module = convert_prog_to_asmjs(progH)
        println(module.toString)
      }
    }
  }
}
