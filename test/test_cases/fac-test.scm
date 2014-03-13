(define (fac n) (if (equal? n 0.0) 
                  1.0
                  (* n (fac (- n 1.0)))))
(fac 6.0)
