fill(scale(2) * unitcircle);

draw((0,0)--(0.25cm,0){right}..{down}(1cm,-0.75cm)--(1cm,-1cm), Arrow);

draw((0.6cm,-1cm)--(1.4cm,-1cm)--(1.4cm,-1.8cm)--(0.6cm,-1.8cm)--(0.6cm,-1cm));
draw((0.6cm,-1.8cm)--(0.6cm,-2.6cm)--(1.4cm,-2.6cm)--(1.4cm,-1.8cm));

draw((1cm,-1.4cm)--(4cm,0), Arrow);
label("index in \texttt{ftables}", (4cm,0), E);

draw(shift((1cm,-2.2cm)) * ((0,0)--(0.25cm,0){right}..{down}(1cm,-0.75cm)--(1cm,-1cm)), Arrow);

draw((1.6cm,-6.2cm)--(1.6cm,-3.2cm)--(2.4cm,-3.2cm)--(2.4cm,-6.2cm));
draw((1.6cm,-4cm)--(2.4cm,-4cm));
draw((1.6cm,-4.8cm)--(2.4cm,-4.8cm));
draw((1.6cm,-5.6cm)--(2.4cm,-5.6cm));

label(shift((2cm,-6cm))*rotate(90)*Label("\dots"));

label("environment", (2.4cm,-4.4cm), E);
label(shift((0,0.075cm)) * Label("closure", (0.6cm,-1.8cm), W));
