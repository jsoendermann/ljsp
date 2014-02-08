; This function computes the dot product of v1 and v2
(define (vectorsDotProduct v1_x v1_y v1_z v2_x v2_y v2_z) (+ (+ (* v1_x v2_x) (* v1_y v2_y)) (* v1_z v2_z)))

; This function takes a ray given by ro_org and ro_dir and a sphere given by a position s and a radius s_r and
; computes the scalar that ro_dir has to be multiplied with for the ray to reach the sphere. If the ray does not 
; intersect the sphere, the function returns 1,000,000.0
(define (raySphereIntersectionPoint ro_org_x ro_org_y ro_org_z ro_dir_x ro_dir_y ro_dir_z s_x s_y s_z s_r)
  ; Shift ro_org to the object space of the sphere
  (let ((r_org_x (- ro_org_x s_x)))
    (let ((r_org_y (- ro_org_y s_y)))
      (let ((r_org_z (- ro_org_z s_z)))
        
        (let ((A (vectorsDotProduct ro_dir_x ro_dir_y ro_dir_z ro_dir_x ro_dir_y ro_dir_z)))
          (let ((B (* 2 (vectorsDotProduct r_org_x r_org_y r_org_z ro_dir_x ro_dir_y ro_dir_z))))
            (let ((C (- (vectorsDotProduct r_org_x r_org_y r_org_z r_org_x r_org_y r_org_z) (* s_r s_r))))
              (let ((disc (- (* B B) (* 4 A C))))
                ; If the discriminant is negative, the equation has no solution and the ray does not intersect
                ; the sphere
                (if (< disc 0.0)
                  1000000.0
                  (let ((t0 (/ (- (neg B) (sqrt disc)) (* 2.0 A))))
                    (let ((t1 (/ (+ (neg B) (sqrt disc)) (* 2.0 A))))
                      (let ((t_max (max t0 t1)))
                        (let ((t_min (min t0 t1)))
                          (if (< t_max 0.0)
                            1000000.0
                            (if (< t_min 0)
                              t_max
                              t_min)))))))))))))))

