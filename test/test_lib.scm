; This file contains functions that are used in intermediate stages
; in the compiler. They are used by run_tests.py to test those stages.


; The negation operator
(define (neg x) (- x))

; This function takes a list of variable length and evaluates all
; elements of the list that are not lists, procedures and integers
; as evaluating those produces an error. Identifiers are evaluated,
; however, as their value should be saved in the environment
; TODO There might be a cleaner way to do this.
(define (make-env . args) 
  (map (lambda (x) (if (or (list? x) 
                           (procedure? x) 
                           (integer? x)) 
                     x 
                     (eval x))) args))

; This function takes two expressions and retuns a two-element list
; that consists of whatever the two expressions evaluate to.
(define (make-closure l e) `(,l ,e))

; This function takes an integer n and a list l and returns the
; nth element in l.
(define (nth n l) (if (= n 0)
                    (car l)
                    (nth (- n 1) (cdr l))))

; This two functiosn take a closure and return proc and env of the
; closure respectively. Because closures are implemented as lists,
; this means returning the 0th and 1st elements.
(define (get-proc l) (nth 0 l))
(define (get-env l) (nth 1 l))

; This function takes a function f and an argument env and returns a lambda
; that, when called, calls function f with a list consisting of env and all
; additional arguments the lambda is called with
(define (hoisted-lambda f env) (lambda args 
                                 (apply f (cons env args))))
