; This file contains functions that are used in intermediate stages
; in the compiler. They are used by run_tests.py to test those stages.



; This function takes a list of variable length and evaluates all
; elements of the list that are not lists, procedures and integers
; as evaluating those is invalid scheme
; TODO There should be a better way to do this
(define (make-env . args) 
  (map (lambda (x) (if (or (list? x) 
                           (procedure? x) 
                           (integer? x)) 
                     x 
                     (eval x))) args))

(define (nth n l) (if (= n 0)
                    (car l)
                    (nth (- n 1) (cdr l))))

(define (make-lambda l e) `(,l ,e))

(define (get-proc l*) (nth 0 l*))

(define (get-env l*) (nth 1 l*))

; This function takes a function f and an argument e and returns a lambda
; that, when called, calls function f with a list consisting of e and all
; additional arguments the lambda is called with
(define (hoisted-lambda f e) (lambda args 
                               (apply f (cons e args))))
