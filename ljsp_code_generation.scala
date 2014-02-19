package ljsp

import ljsp.AST._

object ljsp_code_generation {
  
  def ljsp_prog_to_string(p: SProgram) : String = {
    "; defines\n" +
    p.ds.map{ljsp_exp_to_string}.mkString("\n") + "\n" +
    "; expression\n" +
    ljsp_exp_to_string(p.e)
  }

  def ljsp_exp_to_string(e: SExp) : String = e match {
    case SDefine(name, params, e) => {
      "(define (" + 
      ljsp_exp_to_string(name) + " " +  params.map{ljsp_exp_to_string}.mkString(" ") + ") " +
      ljsp_exp_to_string(e) + ")"
    }
    case SIdn(idn) => idn
    case SDouble(d) => d.toString
    case SIf(e1, e2, e3) => {
      "(if " + 
      ljsp_exp_to_string(e1) + " " + 
      ljsp_exp_to_string(e2) + " " + 
      ljsp_exp_to_string(e3) + ")"
    }
    case SLambda(params, e) => {
      "(lambda (" + params.mkString(" ") + ") " + 
      ljsp_exp_to_string(e) + ")"
    }
    case SAppl(proc, es) => {
      "(" + ljsp_exp_to_string(proc) + " " +
      es.map{ljsp_exp_to_string}.mkString(" ") + ")"
    }
    case SApplPrimitive(proc, es) => {
      "(" + proc.idn + " " + es.map{ljsp_exp_to_string}.mkString(" ") + ")"
    }
    case SLet(idn, e1, e2) => {
      "(let ((" + idn.idn + " " + ljsp_exp_to_string(e1) + ")) " + 
      ljsp_exp_to_string(e2) + ")"
    }
    case SLetN(idnsAndE1s, e2) => {
      "(let (" + 
      idnsAndE1s.map{iae => "(" + iae.idn.idn + " " + ljsp_exp_to_string(iae.e) + ")"}.mkString(" ") + 
      ") " + 
      ljsp_exp_to_string(e2) + ")"
    }

    case SMakeEnv(idns) => {
      "(make-env" +
      (if (idns.size == 0) 
        ")" 
      else 
        " " + idns.map{i => i.idn}.mkString(" ") + ")")
    }
    case SMakeLambda(lambda, env) => {
      "(make-lambda " +
      ljsp_exp_to_string(lambda) + " " +
      ljsp_exp_to_string(env) + ")"
    }
    case SNth(n, e) => {
      "(nth " + n.toString + " " +
      ljsp_exp_to_string(e) + ")"
    }
    case SGetEnv(e) => "(get-env " + ljsp_exp_to_string(e) + ")"
    case SGetProc(e) => "(get-proc " + ljsp_exp_to_string(e) + ")"

    case SHoistedLambda(f, env) => {
      "(hoisted-lambda " +
      f.idn + " " +
      ljsp_exp_to_string(env) + ")"
    }

  }
}
