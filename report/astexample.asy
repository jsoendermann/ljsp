//import drawtree;

path connection(pair c1, pair c2, path n1, path n2) {
    path c = c1--c2;
    slice s1 = cut(c, n1, 0);
    path l1 = s1.after;
    slice s2 = cut(l1, n2, 0);
    path l2 = s2.before;

    return l2;
}

real radius = 0.3cm;

real l1 = -1.25cm;
real l2 = -2.5cm;

pair cRoot = (0,0);
pair c1l = (-0.75cm,l1);
pair c1r = (0.75cm, l1);
pair c2ll = (-1.5cm, l2);
pair c2lr = (-0cm, l2);

path nRoot = circle(cRoot, radius);
path n1l = circle(c1l, radius);
path n1r = circle(c1r, radius);
path n2ll = circle(c2ll, radius);
path n2lr = circle(c2lr, radius);

label("$*$", cRoot-(0,0.02cm));
label("$+$", c1l);
label("$2$", c1r);
label("$x$", c2ll);
label("$1$", c2lr);

draw(nRoot);
draw(n1l);
draw(n1r);
draw(n2ll);
draw(n2lr);

draw(connection(cRoot, c1l, nRoot, n1l));
draw(connection(cRoot, c1r, nRoot, n1r));
draw(connection(c1l, c2ll, n1l, n2ll));
draw(connection(c1l, c2lr, n1l, n2lr));
