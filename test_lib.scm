(define (make-env . args) 
  (map (lambda (x) (eval x)) args))

(define (nth n l) (if (= n 0)
  (car l)
  (nth (- n 1) (cdr l))))

(define (make-lambda l e) `(,l ,e))

(define (get-proc l*) (nth 0 l*))

(define (get-env l*) (nth 1 l*))

(define (hoisted-lambda f e) (lambda args (apply f (cons e args))))
