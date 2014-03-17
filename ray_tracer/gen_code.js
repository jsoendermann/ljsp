function AsmModule(stdlib, foreign, heap) {
    "use asm";

    var mem_top = 0.0;
    var D32 = new stdlib.Float32Array(heap);

    var sqrt = stdlib.Math.sqrt;
    var floor = stdlib.Math.floor;


    function alloc(size) {
        size = +size;
        
        var current_mem_top = 0.0;

        current_mem_top = mem_top;
        mem_top = +(mem_top + size);


        return +current_mem_top;
    }
    
    function func_0(env_0, var_36){
env_0 = +env_0;
var_36 = +var_36;

var env_var_0 = 0.0, ro_dir_x = 0.0, A = 0.0, ro_dir_y = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, r_org_y = 0.0, hoisted_lambda_var_0 = 0.0, r_org_z = 0.0;

ro_dir_x = (+D32[(~~+floor((+((+env_0)+(+0.0))))|0) << 2 >> 2]);
ro_dir_y = (+D32[(~~+floor((+((+env_0)+(+1.0))))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor((+((+env_0)+(+2.0))))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor((+((+env_0)+(+3.0))))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor((+((+env_0)+(+4.0))))|0) << 2 >> 2]);
ro_dir_z = (+D32[(~~+floor((+((+env_0)+(+5.0))))|0) << 2 >> 2]);
r_org_y = (+D32[(~~+floor((+((+env_0)+(+6.0))))|0) << 2 >> 2]);
r_org_z = (+D32[(~~+floor((+((+env_0)+(+7.0))))|0) << 2 >> 2]);
A = (+var_36);
env_var_0 = (+alloc(+6));
D32[(~~+floor((+(env_var_0+(+0.0))))|0) << 2 >> 2] = A;
D32[(~~+floor((+(env_var_0+(+1.0))))|0) << 2 >> 2] = cont_1;
D32[(~~+floor((+(env_var_0+(+2.0))))|0) << 2 >> 2] = s_r;
D32[(~~+floor((+(env_var_0+(+3.0))))|0) << 2 >> 2] = r_org_x;
D32[(~~+floor((+(env_var_0+(+4.0))))|0) << 2 >> 2] = r_org_y;
D32[(~~+floor((+(env_var_0+(+5.0))))|0) << 2 >> 2] = r_org_z;
hoisted_lambda_var_0 = (+alloc(+2));
D32[(~~+floor((+(hoisted_lambda_var_0+(+0.0))))|0) << 2 >> 2] = (+1.0);
D32[(~~+floor((+(hoisted_lambda_var_0+(+1.0))))|0) << 2 >> 2] = env_var_0;
return (+vectorsDotProduct((+hoisted_lambda_var_0), (+r_org_x), (+r_org_y), (+r_org_z), (+ro_dir_x), (+ro_dir_y), (+ro_dir_z)));

}
function func_1(env_1, var_35){
env_1 = +env_1;
var_35 = +var_35;

var var_34 = 0.0, A = 0.0, env_var_1 = 0.0, hoisted_lambda_var_1 = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, B = 0.0, r_org_y = 0.0, r_org_z = 0.0;

A = (+D32[(~~+floor((+((+env_1)+(+0.0))))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor((+((+env_1)+(+1.0))))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor((+((+env_1)+(+2.0))))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor((+((+env_1)+(+3.0))))|0) << 2 >> 2]);
r_org_y = (+D32[(~~+floor((+((+env_1)+(+4.0))))|0) << 2 >> 2]);
r_org_z = (+D32[(~~+floor((+((+env_1)+(+5.0))))|0) << 2 >> 2]);
var_34 = (+((+2.0)*(+var_35)));
B = (+var_34);
env_var_1 = (+alloc(+4));
D32[(~~+floor((+(env_var_1+(+0.0))))|0) << 2 >> 2] = A;
D32[(~~+floor((+(env_var_1+(+1.0))))|0) << 2 >> 2] = cont_1;
D32[(~~+floor((+(env_var_1+(+2.0))))|0) << 2 >> 2] = s_r;
D32[(~~+floor((+(env_var_1+(+3.0))))|0) << 2 >> 2] = B;
hoisted_lambda_var_1 = (+alloc(+2));
D32[(~~+floor((+(hoisted_lambda_var_1+(+0.0))))|0) << 2 >> 2] = (+2.0);
D32[(~~+floor((+(hoisted_lambda_var_1+(+1.0))))|0) << 2 >> 2] = env_var_1;
return (+vectorsDotProduct((+hoisted_lambda_var_1), (+r_org_x), (+r_org_y), (+r_org_z), (+r_org_x), (+r_org_y), (+r_org_z)));

}
function func_2(env_2, var_32){
env_2 = +env_2;
var_32 = +var_32;

var var_24 = 0.0, var_8 = 0.0, if_var_2 = 0.0, disc = 0.0, var_30 = 0.0, t0 = 0.0, var_13 = 0.0, var_19 = 0.0, var_17 = 0.0, var_23 = 0.0, var_31 = 0.0, A = 0.0, var_20 = 0.0, if_var_1 = 0.0, var_16 = 0.0, var_27 = 0.0, env_var_4 = 0.0, cont_1 = 0.0, if_var_0 = 0.0, var_15 = 0.0, ret_val_0 = 0.0, var_26 = 0.0, s_r = 0.0, var_10 = 0.0, var_21 = 0.0, B = 0.0, env_var_2 = 0.0, var_5 = 0.0, env_var_3 = 0.0, t_min = 0.0, C = 0.0, var_7 = 0.0, var_11 = 0.0, t1 = 0.0, var_25 = 0.0, var_18 = 0.0, var_29 = 0.0, t_max = 0.0, var_22 = 0.0, var_33 = 0.0, var_14 = 0.0;

A = (+D32[(~~+floor((+((+env_2)+(+0.0))))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor((+((+env_2)+(+1.0))))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor((+((+env_2)+(+2.0))))|0) << 2 >> 2]);
B = (+D32[(~~+floor((+((+env_2)+(+3.0))))|0) << 2 >> 2]);
var_33 = (+((+s_r)*(+s_r)));
var_31 = (+((+var_32)-(+var_33)));
C = (+var_31);
var_27 = (+((+B)*(+B)));
var_30 = (+((+4.0)*(+A)));
var_29 = (+((+var_30)*(+C)));
var_26 = (+((+var_27)-(+var_29)));
disc = (+var_26);
var_7 = (+(((+disc)<(+0.0))|0));
env_var_2 = (+alloc(+1));
D32[(~~+floor((+(env_var_2+(+0.0))))|0) << 2 >> 2] = cont_1;
var_5 = (+alloc(+2));
D32[(~~+floor((+(var_5+(+0.0))))|0) << 2 >> 2] = (+3.0);
D32[(~~+floor((+(var_5+(+1.0))))|0) << 2 >> 2] = env_var_2;
if_var_0 = (+var_7);
if ((~~+floor(if_var_0)|0))
{
ret_val_0 = (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+var_5)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(var_5+(+1.0))))|0) << 2 >> 2]), (+1000000.0)));
} else {
var_23 = (+((+0.0)-(+B)));
var_24 = (+sqrt((+disc)));
var_22 = (+((+var_23)-(+var_24)));
var_25 = (+((+2.0)*(+A)));
var_21 = (+((+var_22)/(+var_25)));
t0 = (+var_21);
var_18 = (+((+0.0)-(+B)));
var_19 = (+sqrt((+disc)));
var_17 = (+((+var_18)+(+var_19)));
var_20 = (+((+2.0)*(+A)));
var_16 = (+((+var_17)/(+var_20)));
t1 = (+var_16);
var_15 = (+((+t0)>(+t1)?(+t0):(+t1)));
t_max = (+var_15);
var_14 = (+((+t0)<(+t1)?(+t0):(+t1)));
t_min = (+var_14);
var_10 = (+(((+t_max)<(+0.0))|0));
env_var_3 = (+alloc(+1));
D32[(~~+floor((+(env_var_3+(+0.0))))|0) << 2 >> 2] = var_5;
var_8 = (+alloc(+2));
D32[(~~+floor((+(var_8+(+0.0))))|0) << 2 >> 2] = (+4.0);
D32[(~~+floor((+(var_8+(+1.0))))|0) << 2 >> 2] = env_var_3;
if_var_1 = (+var_10);
if ((~~+floor(if_var_1)|0))
{
ret_val_0 = (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+var_8)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(var_8+(+1.0))))|0) << 2 >> 2]), (+1000000.0)));
} else {
var_13 = (+(((+t_min)<(+0.0))|0));
env_var_4 = (+alloc(+1));
D32[(~~+floor((+(env_var_4+(+0.0))))|0) << 2 >> 2] = var_8;
var_11 = (+alloc(+2));
D32[(~~+floor((+(var_11+(+0.0))))|0) << 2 >> 2] = (+5.0);
D32[(~~+floor((+(var_11+(+1.0))))|0) << 2 >> 2] = env_var_4;
if_var_2 = (+var_13);
if ((~~+floor(if_var_2)|0))
{
ret_val_0 = (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+var_11)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(var_11+(+1.0))))|0) << 2 >> 2]), (+t_max)));
} else {
ret_val_0 = (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+var_11)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(var_11+(+1.0))))|0) << 2 >> 2]), (+t_min)));
};
};
};
return (+ret_val_0);

}
function func_3(env_3, var_6){
env_3 = +env_3;
var_6 = +var_6;

var cont_1 = 0.0;

cont_1 = (+D32[(~~+floor((+((+env_3)+(+0.0))))|0) << 2 >> 2]);
return (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+cont_1)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(cont_1+(+1.0))))|0) << 2 >> 2]), (+var_6)));

}
function func_4(env_4, var_9){
env_4 = +env_4;
var_9 = +var_9;

var var_5 = 0.0;

var_5 = (+D32[(~~+floor((+((+env_4)+(+0.0))))|0) << 2 >> 2]);
return (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+var_5)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(var_5+(+1.0))))|0) << 2 >> 2]), (+var_9)));

}
function func_5(env_5, var_12){
env_5 = +env_5;
var_12 = +var_12;

var var_8 = 0.0;

var_8 = (+D32[(~~+floor((+((+env_5)+(+0.0))))|0) << 2 >> 2]);
return (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+var_8)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(var_8+(+1.0))))|0) << 2 >> 2]), (+var_12)));

}
function func_6(env_6, ident_param_0){
env_6 = +env_6;
ident_param_0 = +ident_param_0;

return (+ident_param_0);

}
function func_7(env_7, ident_param_1){
env_7 = +env_7;
ident_param_1 = +ident_param_1;

return (+ident_param_1);

}
function vectorsDotProduct(cont_0, v1_x, v1_y, v1_z, v2_x, v2_y, v2_z){
cont_0 = +cont_0;
v1_x = +v1_x;
v1_y = +v1_y;
v1_z = +v1_z;
v2_x = +v2_x;
v2_y = +v2_y;
v2_z = +v2_z;

var var_3 = 0.0, var_1 = 0.0, var_4 = 0.0, var_0 = 0.0, var_2 = 0.0;

var_2 = (+((+v1_x)*(+v2_x)));
var_3 = (+((+v1_y)*(+v2_y)));
var_1 = (+((+var_2)+(+var_3)));
var_4 = (+((+v1_z)*(+v2_z)));
var_0 = (+((+var_1)+(+var_4)));
return (+ftable2[(~~+floor((+D32[(~~+floor((+((+0.0)+cont_0)))|0) << 2 >> 2]))|0) & 7]((+D32[(~~+floor((+(cont_0+(+1.0))))|0) << 2 >> 2]), (+var_0)));

}
function raySphereIntersectionPoint(cont_1, ro_org_x, ro_org_y, ro_org_z, ro_dir_x, ro_dir_y, ro_dir_z, s_x, s_y, s_z, s_r){
cont_1 = +cont_1;
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

var var_39 = 0.0, env_var_5 = 0.0, var_38 = 0.0, hoisted_lambda_var_2 = 0.0, var_37 = 0.0, r_org_x = 0.0, r_org_y = 0.0, r_org_z = 0.0;

var_39 = (+((+ro_org_x)-(+s_x)));
r_org_x = (+var_39);
var_38 = (+((+ro_org_y)-(+s_y)));
r_org_y = (+var_38);
var_37 = (+((+ro_org_z)-(+s_z)));
r_org_z = (+var_37);
env_var_5 = (+alloc(+8));
D32[(~~+floor((+(env_var_5+(+0.0))))|0) << 2 >> 2] = ro_dir_x;
D32[(~~+floor((+(env_var_5+(+1.0))))|0) << 2 >> 2] = ro_dir_y;
D32[(~~+floor((+(env_var_5+(+2.0))))|0) << 2 >> 2] = cont_1;
D32[(~~+floor((+(env_var_5+(+3.0))))|0) << 2 >> 2] = s_r;
D32[(~~+floor((+(env_var_5+(+4.0))))|0) << 2 >> 2] = r_org_x;
D32[(~~+floor((+(env_var_5+(+5.0))))|0) << 2 >> 2] = ro_dir_z;
D32[(~~+floor((+(env_var_5+(+6.0))))|0) << 2 >> 2] = r_org_y;
D32[(~~+floor((+(env_var_5+(+7.0))))|0) << 2 >> 2] = r_org_z;
hoisted_lambda_var_2 = (+alloc(+2));
D32[(~~+floor((+(hoisted_lambda_var_2+(+0.0))))|0) << 2 >> 2] = (+0.0);
D32[(~~+floor((+(hoisted_lambda_var_2+(+1.0))))|0) << 2 >> 2] = env_var_5;
return (+vectorsDotProduct((+hoisted_lambda_var_2), (+ro_dir_x), (+ro_dir_y), (+ro_dir_z), (+ro_dir_x), (+ro_dir_y), (+ro_dir_z)));

}
function vectorsDotProduct_copy(v1_x, v1_y, v1_z, v2_x, v2_y, v2_z){
v1_x = +v1_x;
v1_y = +v1_y;
v1_z = +v1_z;
v2_x = +v2_x;
v2_y = +v2_y;
v2_z = +v2_z;

var hoisted_lambda_var_3 = 0.0, env_var_6 = 0.0;

mem_top = (+0.0);
env_var_6 = (+alloc(+0));
hoisted_lambda_var_3 = (+alloc(+2));
D32[(~~+floor((+(hoisted_lambda_var_3+(+0.0))))|0) << 2 >> 2] = (+6.0);
D32[(~~+floor((+(hoisted_lambda_var_3+(+1.0))))|0) << 2 >> 2] = env_var_6;
return (+vectorsDotProduct((+hoisted_lambda_var_3), (+v1_x), (+v1_y), (+v1_z), (+v2_x), (+v2_y), (+v2_z)));

}
function raySphereIntersectionPoint_copy(ro_org_x, ro_org_y, ro_org_z, ro_dir_x, ro_dir_y, ro_dir_z, s_x, s_y, s_z, s_r){
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

var hoisted_lambda_var_4 = 0.0, env_var_7 = 0.0;

mem_top = (+0.0);
env_var_7 = (+alloc(+0));
hoisted_lambda_var_4 = (+alloc(+2));
D32[(~~+floor((+(hoisted_lambda_var_4+(+0.0))))|0) << 2 >> 2] = (+7.0);
D32[(~~+floor((+(hoisted_lambda_var_4+(+1.0))))|0) << 2 >> 2] = env_var_7;
return (+raySphereIntersectionPoint((+hoisted_lambda_var_4), (+ro_org_x), (+ro_org_y), (+ro_org_z), (+ro_dir_x), (+ro_dir_y), (+ro_dir_z), (+s_x), (+s_y), (+s_z), (+s_r)));

}

var ftable6 = [vectorsDotProduct_copy];
var ftable7 = [vectorsDotProduct];
var ftable2 = [func_0,func_1,func_2,func_3,func_4,func_5,func_6,func_7];
var ftable10 = [raySphereIntersectionPoint_copy];
var ftable11 = [raySphereIntersectionPoint];

return {vectorsDotProduct: vectorsDotProduct_copy, raySphereIntersectionPoint: raySphereIntersectionPoint_copy};
}
var jModule = AsmModule({ Math: Math, Float32Array: Float32Array}, {}, new ArrayBuffer(10*4096));
