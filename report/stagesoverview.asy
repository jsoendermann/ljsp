path makeBox(pair org, real width, string text, pen colour) {
    path p = shift(org)*yscale(1cm)*xscale(width)*shift((-0.5,-0.5))*unitsquare;

    fill(p, colour);
    draw(p);

    label(text, org);

    return p;
}

path connection(pair c1, pair c2, path n1, path n2) {
    path c = c1--c2;
    slice s1 = cut(c, n1, 0);
    path l1 = s1.after;
    slice s2 = cut(l1, n2, 0);
    path l2 = s2.before;

    return l2;
}


pair ljspFEPos = (-6cm, 2cm);

pair intStPos = (0,0);

real BEX = 6cm;
real BEYDist = 2cm;

pair asmBEPos = (BEX,BEYDist);
pair cBEPos = (BEX,0);
pair llvmIrBEPos = (BEX,-BEYDist);



path ljspFE = makeBox(ljspFEPos, 4cm, "LJSP Front End", lightgreen); 


path intSt = makeBox(intStPos, 5cm, "Intermediate Stages", lightred);


path asmBE = makeBox(asmBEPos, 4cm, "Asm.js Back End", lightblue);
path cBE = makeBox(cBEPos, 4cm, "C Back End", lightblue);
path llvmIrBE = makeBox(llvmIrBEPos, 4cm, "LLVM IR Back End", lightblue);

draw(connection(ljspFEPos+(2cm,0), intStPos+(-2.5cm,0), ljspFE, intSt), Arrow);
draw(connection(intStPos+(2.5cm,0.3cm), asmBEPos+(-2cm,0), intSt, asmBE), Arrow);
draw(connection(intStPos+(2.5cm,0), cBEPos, intSt, cBE), Arrow);
draw(connection(cBEPos, llvmIrBEPos, cBE, llvmIrBE), Arrow);

label("Potentially more front ends", (-6cm,0), grey);
label(shift(-6cm,0.75cm)*rotate(90)*Label("\dots"));
label(shift(-6cm,-0.75cm)*rotate(90)*Label("\dots"));

label(shift(-6cm,4cm)*scale(10)*rotate(270)*Label("\{"));
label(shift(0,4cm)*scale(10)*rotate(270)*Label("\{"));
label(shift(6cm,4cm)*scale(10)*rotate(270)*Label("\{"));

label("Front End Stages", (-6cm,5.25cm));
label("Intermediate Stages", (0,5.25cm));
label("Back End Stages", (6cm,5.25cm));
