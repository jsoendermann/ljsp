(define (vectorsDotProduct v1_x v1_y v1_z v2_x v2_y v2_z) (+ (+ (* v1_x v2_x) (* v1_y v2_y)) (* v1_z v2_z)))

(define (raySphereIntersectionPoint ro_org_x ro_org_y ro_org_z ro_dir_x ro_dir_y ro_dir_z s_x s_y s_z s_r)
  (let ((r_org_x (- ro_org_x s_x)))
    (let ((r_org_y (- ro_org_y s_y)))
      (let ((r_org_z (- ro_org_z s_z)))
        (let ((A (vectorsDotProduct ro_dir_x ro_dir_y ro_dir_z ro_dir_x ro_dir_y ro_dir_z)))
          (let ((B (* 2 (vectorsDotProduct r_org_x r_org_y r_org_z ro_dir_x ro_dir_y ro_dir_z))))
            (let ((C (- (vectorsDotProduct r_org_x r_org_y r_org_z r_org_x r_org_y r_org_z) (* s_r s_r))))
              (let ((disc (- (* B B) (* 4 A C))))
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

