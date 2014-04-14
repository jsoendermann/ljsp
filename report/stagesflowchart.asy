//size(200, 300);

import flowchart;

real column = 6cm;

block parsed=rectangle(Label("Parsing"), (0,0), lightgreen);
block expandLetNs=rectangle(Label("\texttt{letn} expansion"), (column,0), lightgreen);
block fvarWrap=rectangle(Label("Function variable wrapping"), (2*column,0), lightgreen);
block reducePrimOps=rectangle(Label("Prim op reduction"), (0,-2cm), lightgreen);
block cpsTrans=rectangle(Label("CPS-translation"), (column,-2cm), lightgreen);
block cc=rectangle(Label("Closure conversion"), (2*column,-2cm), lightgreen);
block hoist=rectangle(Label("Hoisting"), (0,-4cm), lightgreen);

block ir=rectangle(Label("Intermediate Representation"), (0cm,-6cm), lightred);
block remRedundantAssigns=rectangle(Label("Redundant assignment removal"), (column, -6cm), lightred);

block asmjs=rectangle(Label("asm.js"), (2*column,-8cm), lightblue);
block c=rectangle(Label("C"), (column,-8cm), lightblue);
block emC=rectangle(Label("emcc ready C"), (0,-10cm), lightblue);
block llvmIr=rectangle(Label("LLVM IR"), (column,-10cm), lightblue);
block numLlvmIr=rectangle(Label("Numbered LLVM IR"), (column,-12cm), lightblue);

draw(parsed);
draw(expandLetNs);
draw(reducePrimOps);
draw(fvarWrap);
draw(cpsTrans);
draw(cc);
draw(hoist);

draw(ir);
draw(remRedundantAssigns);
draw(asmjs);
draw(c);
draw(emC);
draw(llvmIr);
draw(numLlvmIr);

add(new void(picture pic, transform t) {
    blockconnector operator --=blockconnector(pic,t);

    block(0,1cm)--Arrow--parsed;

    parsed--Arrow--expandLetNs;
    expandLetNs--Arrow--fvarWrap;

    fvarWrap--Down--block(10cm,-1cm)--Left--Down--Arrow--reducePrimOps;

    reducePrimOps--Arrow--cpsTrans; 
    cpsTrans--Arrow--cc;
    //cc--Arrow--hoist;

    cc--Down--block(10cm,-3cm)--Left--Down--Arrow--hoist;


    hoist--Arrow--ir;
    ir--Arrow--remRedundantAssigns;
    remRedundantAssigns--Arrow--c;
    remRedundantAssigns--Down--block(2*column,-7cm)--Right--Down--Arrow--asmjs;
    c--Left--Down--Arrow--emC;
    c--Arrow--llvmIr;
    llvmIr--Arrow--numLlvmIr;
  });
