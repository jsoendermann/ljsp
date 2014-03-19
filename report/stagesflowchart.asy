//size(200, 300);

import flowchart;

block parsed=roundrectangle(Label("Parsing"), (0,0));
block expandLetNs=roundrectangle(Label("Expansion of letns"), (5cm,0));
block reducePrimOps=roundrectangle(Label("Reduction of prim ops"), (10cm,0));
block cpsTrans=roundrectangle(Label("CPS translation"), (0cm,-2cm));
block cc=roundrectangle(Label("Closure conversion"), (5cm,-2cm));
block hoist=roundrectangle(Label("Hoisting"), (10cm,-2cm));

block asmjs=rectangle(Label("asm.js"), (10cm,-4cm), lightgrey);
block ir=rectangle(Label("Intermediate Representation"), (0cm,-4cm));
block c=rectangle(Label("C"), (0cm,-6cm), lightgrey);
block emC=rectangle(Label("emcc ready C"), (5cm,-8cm), lightgrey);
block llvmIr=rectangle(Label("LLVM IR"), (0cm,-8cm), lightgrey);
block numLlvmIr=rectangle(Label("Numbered LLVM IR"), (0cm,-10cm), lightgrey);

//block block1=rectangle(Label("Example",magenta),
//		       pack(Label("Start:",heavygreen),"",Label("$A:=0$",blue),
//			    "$B:=1$"),(-0.5,3),palegreen,paleblue,red);
//block block2=diamond(Label("Choice?",blue),(0,2),palegreen,red);
//block block3=roundrectangle("Do something",(-1,1));
//block block4=bevel("Don't do something",(1,1));
//block block5=circle("End",(0,0));

draw(parsed);
draw(expandLetNs);
draw(reducePrimOps);
draw(cpsTrans);
draw(cc);
draw(hoist);

draw(asmjs);
draw(ir);
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

    hoist--Arrow--asmjs;
    hoist--Down--block(10cm,-3cm)--Left--Down--Arrow--ir;
    ir--Arrow--c;
    c--Right--Down--Arrow--emC;
    c--Arrow--llvmIr;
    llvmIr--Arrow--numLlvmIr;
  });
