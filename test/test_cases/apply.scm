(define (succ n) (+ n 1))
(define (pred n) (- n 1))
(define (apply_func f n) (f n))

(if (< 0 1) 
  (apply_func succ 3)
  (apply_func pred 3))
