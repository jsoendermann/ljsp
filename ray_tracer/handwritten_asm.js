function HandwrittenAsmjsModule(stdlib, foreign, heap) {
    "use asm";

    var mem_top = 0.0;
    var D32 = new stdlib.Float32Array(heap);

    var sqrt = stdlib.Math.sqrt;
    var floor = stdlib.Math.floor;



    function vectorsDotProduct(v1_x, v1_y, v1_z, v2_x, v2_y, v2_z){
        v1_x = +v1_x;
        v1_y = +v1_y;
        v1_z = +v1_z;
        v2_x = +v2_x;
        v2_y = +v2_y;
        v2_z = +v2_z;

        return +(v1_x * v2_x + v1_y * v2_y + v1_z * v2_z);

    }
    function raySphereIntersectionPoint(ro_org_x, ro_org_y, ro_org_z, ro_dir_x, ro_dir_y, ro_dir_z, s_x, s_y, s_z, s_r){
        ro_org_x = +ro_org_x;
        ro_org_y = +ro_org_y;
        ro_org_z = +ro_org_z;
        ro_dir_x = +ro_dir_x;
        ro_dir_y = +ro_dir_y;
        ro_dir_z = +ro_dir_z;
        s_x = +s_x;
        s_y = +s_y;
        s_z = +s_z;
        s_r = +s_r;

        var r_org_x = 0.0, r_org_y = 0.0, r_org_z = 0.0,
            A = 0.0, B = 0.0, C = 0.0, discriminant = 0.0, disc_sqrt = 0.0, t0 = 0.0, t1 = 0.0, temp = 0.0, min = 0.0, max = 0.0;

        r_org_x = ro_org_x - s_x;
        r_org_y = ro_org_y - s_y;
        r_org_z = ro_org_z - s_z;


        A = +vectorsDotProduct(ro_dir_x, ro_dir_y, ro_dir_z, ro_dir_x, ro_dir_y, ro_dir_z);
        B = +(2.0 * +vectorsDotProduct(ro_dir_x, ro_dir_y, ro_dir_z, r_org_x, r_org_y, r_org_z));
        C = +vectorsDotProduct(r_org_x, r_org_y, r_org_z, r_org_x, r_org_y, r_org_z) - (s_r * s_r);

        discriminant = B*B - 4.0 * A * C;

        if (discriminant < 0.0) {
            return 1000000.0;
        }

        disc_sqrt = +sqrt(discriminant);
        temp = (2.0 * A);
        t0 =  (-B - disc_sqrt)/ temp;
        t1 = (-B + disc_sqrt) / temp;

        max = (t0>t1?t0:t1);
        min = (t0<t1?t0:t1);

        if (max < 0.0) {
            return 1000000.0;
        }

        if (min < 0.0) {
            return max;
        }

        return +min;
    }

    return {vectorsDotProduct: vectorsDotProduct, raySphereIntersectionPoint: raySphereIntersectionPoint};
}
var handwrittenModule = HandwrittenAsmjsModule({ Math: Math, Float32Array: Float32Array}, {}, new ArrayBuffer(4096));
