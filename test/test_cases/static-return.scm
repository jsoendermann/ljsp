; LJSP currently doesn't allow defines without parameters
(define (one n) 1)

; This should evaluate to 3, the 99 should get discarded by one
(+ (one 99) 2)
