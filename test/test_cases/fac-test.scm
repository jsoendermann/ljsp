(define (fac n) (if (equal? n 0) 
                  1 
                  (* n (fac (- n 1)))))
(fac 6)
