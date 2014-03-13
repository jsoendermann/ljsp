(define (fac n) (if (< n 0.1)  ; TODO fix this once == has been implemented
                  1.0
                  (* n (fac (- n 1.0)))))
(fac 6.0)
