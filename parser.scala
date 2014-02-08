package ljsp

import scala.util.parsing.combinator._
import ljsp.AST._

object parser {

  object JLispParsers extends JavaTokenParsers {
    def expression: Parser[SExp] = identifier | double | _if | lambda | letN | primitive_application | application

    protected override val whiteSpace = """(\s|;.*)+""".r //"""(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

    def prog: Parser[SProgram] = rep(define)~opt(expression)~rep(define) ^^ {
      case ds1~e~ds2 => {
        e match {
          case Some(e) => SProgram(ds1 ::: ds2, e)
          // TODO This is a hack, e should be set to None or null.
          //      It works because the expression gets thrown away
          //      before compiling to asm.js code.
          case None => SProgram(ds1 ::: ds2, SDouble(-42))
        }
      }
    }

    def define: Parser[SDefine] = "("~>"define"~>"("~>identifier~rep(identifier)~")"~expression<~")" ^^ {
      case name~params~")"~e => SDefine(name, params, e)
    }

    def identifier: Parser[SIdn] = """[a-zA-Z=*+/<>!\?\-][a-zA-Z0-9=*+/<>!\?\-_]*""".r ^^ (SIdn(_))

    def primitive_proc: Parser[SIdn] = ("+" | "-" | "*" | "/" | "<" | ">" | "and" | "or" | "equal?" | "car" | "cdr" | "neg" | "min" | "max" | "sqrt") ^^ (SIdn(_))
    
    def let_define_block: Parser[LetDefineBlock] = "("~>identifier~expression<~")" ^^ {
      case idn~e => LetDefineBlock(idn, e)
    }

    def double: Parser[SDouble] = floatingPointNumber ^^ {d => SDouble(d.toDouble)}

    def _if: Parser[SIf] = "("~>"if"~>expression~expression~expression<~')' ^^ {
      case e1~e2~e3 => SIf(e1, e2, e3)
    }

    def lambda: Parser[SLambda] = "("~>"lambda"~>"("~>rep1(identifier, identifier)~")"~expression<~")" ^^ {
      case params~")"~e => SLambda(params, e)
    }
    
    def letN: Parser[SLetN] = "("~>"let"~>"("~>rep1(let_define_block, let_define_block)~")"~expression<~")" ^^ {
      case let_define_blocks~")"~e2 => SLetN(let_define_blocks, e2)
    }

    def primitive_application: Parser[SApplPrimitive] = "("~>primitive_proc~rep1(expression, expression)<~")" ^^ {
      case proc~es => SApplPrimitive(proc, es)
    }

    def application: Parser[SAppl] = "("~>expression~rep1(expression, expression)<~")" ^^ {
      case proc~es => SAppl(proc, es)
    }


    // TODO this throws an exception instead of printing a nice error message when the input isn't well formed
    def parseExpr(str: String) = parse(prog, str).get
  }

}
