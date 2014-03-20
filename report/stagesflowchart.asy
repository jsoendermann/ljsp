//size(200, 300);

import flowchart;

real column = 6cm;

block parsed=roundrectangle(Label("Parsing"), (0,0));
block expandLetNs=roundrectangle(Label("Expansion of letns"), (column,0));
block reducePrimOps=roundrectangle(Label("Reduction of prim ops"), (2*column,0));
block cpsTrans=roundrectangle(Label("CPS translation"), (0,-2cm));
block cc=roundrectangle(Label("Closure conversion"), (column,-2cm));
block hoist=roundrectangle(Label("Hoisting"), (2*column,-2cm));

block ir=rectangle(Label("Intermediate Representation"), (0cm,-4cm));
block remRedundantAssigns=rectangle(Label("Remove redundant assigns"), (column, -4cm));
block asmjs=rectangle(Label("asm.js"), (2*column,-6cm), lightgrey);
block c=rectangle(Label("C"), (column,-6cm), lightgrey);
block emC=rectangle(Label("emcc ready C"), (0,-8cm), lightgrey);
block llvmIr=rectangle(Label("LLVM IR"), (column,-8cm), lightgrey);
block numLlvmIr=rectangle(Label("Numbered LLVM IR"), (column,-10cm), lightgrey);

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
