package ljsp

import scala.util.parsing.combinator._
import ljsp.AST._

object parser {

  object JLispParsers extends JavaTokenParsers {
    def expression: Parser[SExp] = identifier | double | _if | lambda | primitive_application | application | let

    // TODO make e optional
    def prog: Parser[SProgram] = rep(define)~opt(expression)~rep(define) ^^ {
      case ds1~e~ds2 => {
        e match {
          case Some(e) => SProgram(ds1 ::: ds2, e)
          // TODO this doesn't work yet because the rest of the code can't handle null
          case None => SProgram(ds1 ::: ds2, null)
        }
      }
    }

    def define: Parser[SDefine] = "("~>"define"~>"("~>identifier~rep(identifier)~")"~expression<~")" ^^ {
      case name~params~")"~e => SDefine(name, params, e)
    }

    def identifier: Parser[SIdn] = """[a-zA-Z=*+/<>!\?\-][a-zA-Z0-9=*+/<>!\?\-]*""".r ^^ (SIdn(_))
    // TODO: Add all primitive operations
    def primitive_proc: Parser[SIdn] = ("+" | "-" | "*" | "/" | "<" | ">" | "and" | "or" | "equal?" | "car" | "cdr" | "neg") ^^ (SIdn(_))
    def double: Parser[SDouble] = floatingPointNumber ^^ {d => SDouble(d.toDouble)}
    //def integer: Parser[SInt] = wholeNumber ^^ (i => SInt(i.toInt))

    def _if: Parser[SIf] = "("~>"if"~>expression~expression~expression<~')' ^^ {
      case e1~e2~e3 => SIf(e1, e2, e3)
    }

    def lambda: Parser[SLambda] = "("~>"lambda"~>"("~>rep1(identifier, identifier)~")"~expression<~")" ^^ {
      case params~")"~e => SLambda(params, e)
    }

    def primitive_application: Parser[SApplPrimitive] = "("~>primitive_proc~rep1(expression, expression)<~")" ^^ {
      case proc~es => SApplPrimitive(proc, es)
    }
    def application: Parser[SAppl] = "("~>expression~rep1(expression, expression)<~")" ^^ {
      case proc~es => SAppl(proc, es)
    }
    def let: Parser[SLet] = "("~>"let"~>"("~"("~>identifier~expression~")"~")"~expression<~")" ^^ {
      case idn~e1~")"~")"~e2 => SLet(idn, e1, e2)
    }

    // TODO this throws an exception instead of printing a nice error message when the input isn't well formed
    def parseExpr(str: String) = parse(prog, str).get
  }

}
