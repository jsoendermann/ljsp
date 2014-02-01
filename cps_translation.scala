package ljsp

import ljsp.AST._
import ljsp.util._

object cps_translation {
  // this function only exists for typecasting, maybe there is a better way to do this
  def cps_trans_prog(p : SProgram, k : SExp => SExp) : SProgram = {
    val func_copies = p.ds.map{d => {
      val ident_param = SIdn(fresh("ident_param"))
      SDefine(SIdn(d.name + "_copy"), d.params, SAppl(d.name, List(SLambda(List(ident_param), ident_param)) ++ d.params))}}
    val cps_prog = cps_trans(p, k).asInstanceOf[SProgram]
    SProgram(cps_prog.ds ++ func_copies, cps_prog.e)
  }

  def cps_trans(e: SExp, k: SExp => SExp) : SExp = e match {
    // CPS-translate all function definitions and the expression
    case SProgram(ds, e) => {
      SProgram(ds.map{d => 
        val c = SIdn(fresh("cont"))
        SDefine(d.name, c :: d.params, cps_tail_trans(d.e, c))
      }, cps_trans(e, k))
  }

  // For SDefine case see further down, as its CPS translation is very similar to that
  // of SLambda, the two are grouped together

  // For atomic values, no CPS-Translation is necessary. Simply apply k to e
  case SIdn(idn) => k(e)
  case SInt(i) => k(e)

  // For if, evaluate e1 first, then branch with two recursive calls
  case SIf(e1, e2, e3) => {
    val c = SIdn(fresh("var"))
    val p = SIdn(fresh("var"))
    cps_trans(e1, (ce1: SExp) =>
      SLet(c, SLambda(List(p), k(p)),
        SIf(ce1, cps_tail_trans(e2, c), cps_tail_trans(e3, c))))
    }

    // for lambdas and defines, add an additional parameter that will hold the continuation
    // and call it with the result when the function is done
    case SLambda(params, e) => {
      val c = SIdn(fresh("cont"))
      k(SLambda(c :: params, cps_tail_trans(e, c)))
    }
    case SDefine(name, params, e) => {
      val c = SIdn(fresh("cont"))
      k(SDefine(name, c :: params, cps_tail_trans(e, c)))
    }

    // For applications, evaluate proc first, then all the arguments
    case SAppl(proc, es) => {
      // the application will have (lambda (f) k(f)) added as additional parameter
      // which will be the continuation for the function that is being called
      val f = SIdn(fresh("var"))

      // CPS-Translate the expression for the procedure first
      cps_trans(proc, (cproc: SExp) => {

          // this function calls CPS recursively for all expressions in es and accumulates the result in ces
          // the last call with an empty es creates calls the function with the additional lambda mentioned above
          def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
            case Nil => SAppl(cproc, SLambda(List(f), k(f)) :: ces) //SLet(f, SAppl(cproc, ces), k(f))
            case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
          }
          aux(es, Nil)
        })
    }

    // For primitive applications, translate all the arguments
    // and continue with let f=SAppl in k(f)
    // TODO translate expressions with more than 2 arguments
    case SApplPrimitive(proc, es) => {
      val z = SIdn(fresh("var"))

      // this function calls CPS recursively for all expressions in es and accumulates the result in ces
      // the last call with an empty es creates calls the function with the additional lambda mentioned above
      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SLet(z, SApplPrimitive(proc, ces), k(z))
        case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
      aux(es, Nil)
    }


    case SLet(idn, e1, e2) => {
      // c is a simple continuation lambda
      val f = SIdn(fresh("var"))
      val c = SLambda(List(f), k(f))

      val ce2 = cps_tail_trans(e2, c)

      // This creates a lambda with a parameter of the same name as the identifier in the let
      // e1 is CPS translated and applied to this new lambda
      cps_tail_trans(e1, SLambda(List(idn), ce2))
    }
  }

  // This function is very similar to CPS, the difference being that for CPSTail the second parameter is an
  // identifier which will point to a continuation function that has to be called with the result of e.
  // For explanations, please see the comments to CPS
  def cps_tail_trans(e: SExp, c: SExp) : SExp = e match {
    case SIdn(idn) => SAppl(c, List(e))
    case SInt(i) => SAppl(c, List(e))

    case SIf(e1, e2, e3) => {
      val c_ = SIdn(fresh("var"))
      val p = SIdn(fresh("var"))
      cps_trans(e1, (ce1: SExp) =>
        SLet(c_, SLambda(List(p), SAppl(c, List(p))),
          SIf(ce1, cps_tail_trans(e2, c_), cps_tail_trans(e3, c_))))
    }

    case SLambda(params, e) => {
      val c_ = SIdn(fresh("cont"))
      SAppl(c, List((SLambda(c_ :: params, cps_tail_trans(e, c_)))))
    }

    case SAppl(proc, es) => {
      val f = SIdn(fresh("var"))
      cps_trans(proc, (cproc: SExp) => {
          def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
            case Nil => SAppl(cproc, SLambda(List(f), SAppl(c, List(f))) :: ces)
            case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
          }
          aux(es, Nil)
        })
    }

    case SApplPrimitive(proc, es) => {
      val z = SIdn(fresh("var"))

      def aux(es: List[SExp], ces: List[SExp]) : SExp = es match {
        case Nil => SLet(z, SApplPrimitive(proc, ces), SAppl(c, List(z)))
        case (e::es) => cps_trans(e, (ce: SExp) => aux(es, ces ::: List(ce)))
      }
      aux(es, Nil)
    }

    case SLet(idn, e1, e2) => {
      val f = SIdn(fresh("var"))
      val c_ = SLambda(List(f), SAppl(c, List(f)))

      val ce2 = cps_tail_trans(e2, c_)

      cps_tail_trans(e1, SLambda(List(idn), ce2))
    }
  }
}
