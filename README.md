ljsp
====

A compiler for Lisp that emits asm.js written in Scala.

To compile and try out a simple program run

    scalac ./*.scala && scala -cp ljsp/ ljsp.Ljsp "(define (fib n) (if (< n 2) 1 (+ (fib (- n 1)) (fib (- n 2)))))(fib 6)"

on the command line.
