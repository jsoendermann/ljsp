//size(200, 300);

import flowchart;

real column = 6cm;

block parsed=rectangle(Label("Parsing"), (0,0), lightgreen);
block expandLetNs=rectangle(Label("\texttt{letn} expansion"), (column,0), lightgreen);
block reducePrimOps=rectangle(Label("Prim op reduction"), (2*column,0), lightgreen);
block cpsTrans=rectangle(Label("CPS-translation"), (0,-2cm), lightgreen);
block cc=rectangle(Label("Closure conversion"), (column,-2cm), lightgreen);
block hoist=rectangle(Label("Hoisting"), (2*column,-2cm), lightgreen);

block ir=rectangle(Label("Intermediate Representation"), (0cm,-4cm), lightred);
block remRedundantAssigns=rectangle(Label("Redundant assignment removal"), (column, -4cm), lightred);

block asmjs=rectangle(Label("asm.js"), (2*column,-6cm), lightblue);
block c=rectangle(Label("C"), (column,-6cm), lightblue);
block emC=rectangle(Label("emcc ready C"), (0,-8cm), lightblue);
block llvmIr=rectangle(Label("LLVM IR"), (column,-8cm), lightblue);
block numLlvmIr=rectangle(Label("Numbered LLVM IR"), (column,-10cm), lightblue);

draw(parsed);
draw(expandLetNs);
draw(reducePrimOps);
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
    expandLetNs--Arrow--reducePrimOps;

    reducePrimOps--Down--block(10cm,-1cm)--Left--Down--Arrow--cpsTrans;

    cpsTrans--Arrow--cc;
    cc--Arrow--hoist;

    hoist--Down--block(2*column,-3cm)--Left--Down--Arrow--ir;
    ir--Arrow--remRedundantAssigns;
    remRedundantAssigns--Arrow--c;
    remRedundantAssigns--Down--block(2*column,-5cm)--Right--Down--Arrow--asmjs;
    c--Left--Down--Arrow--emC;
    c--Arrow--llvmIr;
    llvmIr--Arrow--numLlvmIr;
  });
