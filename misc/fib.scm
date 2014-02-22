; defines
(define (func_0 env_0 var_1) 
  (let ((cont_0 (nth 0 env_0))) 
    (cont_0 var_1)))

(define (func_1 env_1 var_4) 
  (let ((n (nth 0 env_1))) 
    (let ((var_0 (nth 1 env_1))) 
      (let ((var_7 (- n 2.0))) 
        (let ((env_var_0 (make-env var_4 var_0))) 
          (let ((hoisted_lambda_var_0 (hoisted-lambda func_2 env_var_0))) 
            (fib hoisted_lambda_var_0 var_7)))))))

(define (func_2 env_2 var_6) 
  (let ((var_4 (nth 0 env_2))) 
    (let ((var_0 (nth 1 env_2))) 
      (let ((var_3 (+ var_4 var_6))) 
        (var_0 var_3)))))

(define (func_3 env_3 ident_param_0) 
  ident_param_0)

(define (fib cont_0 n) 
  (let ((var_2 (< n 2.0))) 
    (let ((env_var_1 (make-env cont_0))) 
      (let ((var_0 (hoisted-lambda func_0 env_var_1))) 
        (if var_2 
          (var_0 1.0) 
          (let ((var_5 (- n 1.0))) 
            (let ((env_var_2 (make-env n var_0))) 
              (let ((hoisted_lambda_var_1 (hoisted-lambda func_1 env_var_2))) 
                (fib hoisted_lambda_var_1 var_5)))))))))

(define (fib_copy n) 
  (let ((env_var_3 (make-env))) 
    (let ((hoisted_lambda_var_2 
            (hoisted-lambda func_3 env_var_3)))
      (fib hoisted_lambda_var_2 n))))

; expression
(fib_copy 6)
