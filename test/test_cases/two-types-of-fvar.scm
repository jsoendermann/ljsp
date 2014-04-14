(define (succ n) (+ n 1))

(let ((f1 succ)
      (f2 (lambda (n) (+ n 1))))
  (f1 (f2 1)))
