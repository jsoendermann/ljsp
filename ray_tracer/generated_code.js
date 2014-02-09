
      function AsmModule(stdlib, foreign, heap) {
      "use asm";

      var mem_top = 0.0;
      var H32 = new stdlib.Int32Array(heap);
      var D32 = new stdlib.Float32Array(heap);

      var log = foreign.consoleDotLog;

      var imul = stdlib.Math.imul;
      var sqrt = stdlib.Math.sqrt;
      var floor = stdlib.Math.floor;


      function alloc(size) {
          size = +size;
          
          var current_mem_top = 0.0;

          current_mem_top = mem_top;
          mem_top = +(mem_top + size);


          return +current_mem_top;
      }

      function min(a, b) {
        a = +a;
        b = +b;

        return +(a<b?a:b);
      }

      function max(a, b) {
        a = +a;
        b = +b;

        return +(a>b?a:b);
      }

      
      function func_0(env_0, r_org_x){
env_0 = +env_0;
r_org_x = +r_org_x;

var ro_dir_x = 0.0, ro_org_z = 0.0, s_z = 0.0, s_y = 0.0, ro_org_y = 0.0, hoisted_lambda_var_5 = 0.0, ro_dir_y = 0.0, var_38 = 0.0, cont_1 = 0.0, s_r = 0.0, env_19 = 0.0, ro_dir_z = 0.0;

ro_dir_x = (+D32[(~~+floor(+(env_0 + (+0.0)))|0) << 2 >> 2]);
ro_org_z = (+D32[(~~+floor(+(env_0 + (+1.0)))|0) << 2 >> 2]);
s_z = (+D32[(~~+floor(+(env_0 + (+2.0)))|0) << 2 >> 2]);
s_y = (+D32[(~~+floor(+(env_0 + (+3.0)))|0) << 2 >> 2]);
ro_org_y = (+D32[(~~+floor(+(env_0 + (+4.0)))|0) << 2 >> 2]);
ro_dir_y = (+D32[(~~+floor(+(env_0 + (+5.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_0 + (+6.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_0 + (+7.0)))|0) << 2 >> 2]);
ro_dir_z = (+D32[(~~+floor(+(env_0 + (+8.0)))|0) << 2 >> 2]);
var_38 = (+((+(ro_org_y))-(+(s_y))));
env_19 = (+alloc(+8));
D32[(~~+floor(+(env_19 + (+0.0)))|0) << 2 >> 2] = +(ro_dir_x);
D32[(~~+floor(+(env_19 + (+1.0)))|0) << 2 >> 2] = +(ro_org_z);
D32[(~~+floor(+(env_19 + (+2.0)))|0) << 2 >> 2] = +(s_z);
D32[(~~+floor(+(env_19 + (+3.0)))|0) << 2 >> 2] = +(ro_dir_y);
D32[(~~+floor(+(env_19 + (+4.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_19 + (+5.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_19 + (+6.0)))|0) << 2 >> 2] = +(r_org_x);
D32[(~~+floor(+(env_19 + (+7.0)))|0) << 2 >> 2] = +(ro_dir_z);
hoisted_lambda_var_5 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_5 + (+0.0)))|0) << 2 >> 2] = +((+1.0));
D32[(~~+floor(+(hoisted_lambda_var_5 + (+1.0)))|0) << 2 >> 2] = +(env_19);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_5))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_5 + (+1.0)))|0) << 2 >> 2]), var_38));

}
function func_1(env_1, r_org_y){
env_1 = +env_1;
r_org_y = +r_org_y;

var ro_dir_x = 0.0, ro_org_z = 0.0, s_z = 0.0, ro_dir_y = 0.0, hoisted_lambda_var_6 = 0.0, cont_1 = 0.0, var_37 = 0.0, s_r = 0.0, r_org_x = 0.0, env_20 = 0.0, ro_dir_z = 0.0;

ro_dir_x = (+D32[(~~+floor(+(env_1 + (+0.0)))|0) << 2 >> 2]);
ro_org_z = (+D32[(~~+floor(+(env_1 + (+1.0)))|0) << 2 >> 2]);
s_z = (+D32[(~~+floor(+(env_1 + (+2.0)))|0) << 2 >> 2]);
ro_dir_y = (+D32[(~~+floor(+(env_1 + (+3.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_1 + (+4.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_1 + (+5.0)))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor(+(env_1 + (+6.0)))|0) << 2 >> 2]);
ro_dir_z = (+D32[(~~+floor(+(env_1 + (+7.0)))|0) << 2 >> 2]);
var_37 = (+((+(ro_org_z))-(+(s_z))));
env_20 = (+alloc(+7));
D32[(~~+floor(+(env_20 + (+0.0)))|0) << 2 >> 2] = +(ro_dir_x);
D32[(~~+floor(+(env_20 + (+1.0)))|0) << 2 >> 2] = +(ro_dir_y);
D32[(~~+floor(+(env_20 + (+2.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_20 + (+3.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_20 + (+4.0)))|0) << 2 >> 2] = +(r_org_x);
D32[(~~+floor(+(env_20 + (+5.0)))|0) << 2 >> 2] = +(ro_dir_z);
D32[(~~+floor(+(env_20 + (+6.0)))|0) << 2 >> 2] = +(r_org_y);
hoisted_lambda_var_6 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_6 + (+0.0)))|0) << 2 >> 2] = +((+2.0));
D32[(~~+floor(+(hoisted_lambda_var_6 + (+1.0)))|0) << 2 >> 2] = +(env_20);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_6))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_6 + (+1.0)))|0) << 2 >> 2]), var_37));

}
function func_2(env_2, r_org_z){
env_2 = +env_2;
r_org_z = +r_org_z;

var env_var_0 = 0.0, ro_dir_x = 0.0, ro_dir_y = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, r_org_y = 0.0, hoisted_lambda_var_0 = 0.0;

ro_dir_x = (+D32[(~~+floor(+(env_2 + (+0.0)))|0) << 2 >> 2]);
ro_dir_y = (+D32[(~~+floor(+(env_2 + (+1.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_2 + (+2.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_2 + (+3.0)))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor(+(env_2 + (+4.0)))|0) << 2 >> 2]);
ro_dir_z = (+D32[(~~+floor(+(env_2 + (+5.0)))|0) << 2 >> 2]);
r_org_y = (+D32[(~~+floor(+(env_2 + (+6.0)))|0) << 2 >> 2]);
env_var_0 = (+alloc(+8));
D32[(~~+floor(+(env_var_0 + (+0.0)))|0) << 2 >> 2] = +(ro_dir_x);
D32[(~~+floor(+(env_var_0 + (+1.0)))|0) << 2 >> 2] = +(ro_dir_y);
D32[(~~+floor(+(env_var_0 + (+2.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_var_0 + (+3.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_var_0 + (+4.0)))|0) << 2 >> 2] = +(r_org_x);
D32[(~~+floor(+(env_var_0 + (+5.0)))|0) << 2 >> 2] = +(ro_dir_z);
D32[(~~+floor(+(env_var_0 + (+6.0)))|0) << 2 >> 2] = +(r_org_y);
D32[(~~+floor(+(env_var_0 + (+7.0)))|0) << 2 >> 2] = +(r_org_z);
hoisted_lambda_var_0 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_0 + (+0.0)))|0) << 2 >> 2] = +((+3.0));
D32[(~~+floor(+(hoisted_lambda_var_0 + (+1.0)))|0) << 2 >> 2] = +(env_var_0);
return +(+vectorsDotProduct(hoisted_lambda_var_0, ro_dir_x, ro_dir_y, ro_dir_z, ro_dir_x, ro_dir_y, ro_dir_z));

}
function func_3(env_3, var_36){
env_3 = +env_3;
var_36 = +var_36;

var ro_dir_x = 0.0, env_21 = 0.0, ro_dir_y = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, hoisted_lambda_var_7 = 0.0, r_org_y = 0.0, r_org_z = 0.0;

ro_dir_x = (+D32[(~~+floor(+(env_3 + (+0.0)))|0) << 2 >> 2]);
ro_dir_y = (+D32[(~~+floor(+(env_3 + (+1.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_3 + (+2.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_3 + (+3.0)))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor(+(env_3 + (+4.0)))|0) << 2 >> 2]);
ro_dir_z = (+D32[(~~+floor(+(env_3 + (+5.0)))|0) << 2 >> 2]);
r_org_y = (+D32[(~~+floor(+(env_3 + (+6.0)))|0) << 2 >> 2]);
r_org_z = (+D32[(~~+floor(+(env_3 + (+7.0)))|0) << 2 >> 2]);
env_21 = (+alloc(+8));
D32[(~~+floor(+(env_21 + (+0.0)))|0) << 2 >> 2] = +(ro_dir_x);
D32[(~~+floor(+(env_21 + (+1.0)))|0) << 2 >> 2] = +(ro_dir_y);
D32[(~~+floor(+(env_21 + (+2.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_21 + (+3.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_21 + (+4.0)))|0) << 2 >> 2] = +(r_org_x);
D32[(~~+floor(+(env_21 + (+5.0)))|0) << 2 >> 2] = +(ro_dir_z);
D32[(~~+floor(+(env_21 + (+6.0)))|0) << 2 >> 2] = +(r_org_y);
D32[(~~+floor(+(env_21 + (+7.0)))|0) << 2 >> 2] = +(r_org_z);
hoisted_lambda_var_7 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_7 + (+0.0)))|0) << 2 >> 2] = +((+4.0));
D32[(~~+floor(+(hoisted_lambda_var_7 + (+1.0)))|0) << 2 >> 2] = +(env_21);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_7))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_7 + (+1.0)))|0) << 2 >> 2]), var_36));

}
function func_4(env_4, A){
env_4 = +env_4;
A = +A;

var ro_dir_x = 0.0, env_var_1 = 0.0, ro_dir_y = 0.0, hoisted_lambda_var_1 = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, r_org_y = 0.0, r_org_z = 0.0;

ro_dir_x = (+D32[(~~+floor(+(env_4 + (+0.0)))|0) << 2 >> 2]);
ro_dir_y = (+D32[(~~+floor(+(env_4 + (+1.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_4 + (+2.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_4 + (+3.0)))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor(+(env_4 + (+4.0)))|0) << 2 >> 2]);
ro_dir_z = (+D32[(~~+floor(+(env_4 + (+5.0)))|0) << 2 >> 2]);
r_org_y = (+D32[(~~+floor(+(env_4 + (+6.0)))|0) << 2 >> 2]);
r_org_z = (+D32[(~~+floor(+(env_4 + (+7.0)))|0) << 2 >> 2]);
env_var_1 = (+alloc(+6));
D32[(~~+floor(+(env_var_1 + (+0.0)))|0) << 2 >> 2] = +(A);
D32[(~~+floor(+(env_var_1 + (+1.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_var_1 + (+2.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_var_1 + (+3.0)))|0) << 2 >> 2] = +(r_org_x);
D32[(~~+floor(+(env_var_1 + (+4.0)))|0) << 2 >> 2] = +(r_org_y);
D32[(~~+floor(+(env_var_1 + (+5.0)))|0) << 2 >> 2] = +(r_org_z);
hoisted_lambda_var_1 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_1 + (+0.0)))|0) << 2 >> 2] = +((+5.0));
D32[(~~+floor(+(hoisted_lambda_var_1 + (+1.0)))|0) << 2 >> 2] = +(env_var_1);
return +(+vectorsDotProduct(hoisted_lambda_var_1, r_org_x, r_org_y, r_org_z, ro_dir_x, ro_dir_y, ro_dir_z));

}
function func_5(env_5, var_35){
env_5 = +env_5;
var_35 = +var_35;

var hoisted_lambda_var_8 = 0.0, var_34 = 0.0, A = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, r_org_y = 0.0, env_22 = 0.0, r_org_z = 0.0;

A = (+D32[(~~+floor(+(env_5 + (+0.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_5 + (+1.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_5 + (+2.0)))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor(+(env_5 + (+3.0)))|0) << 2 >> 2]);
r_org_y = (+D32[(~~+floor(+(env_5 + (+4.0)))|0) << 2 >> 2]);
r_org_z = (+D32[(~~+floor(+(env_5 + (+5.0)))|0) << 2 >> 2]);
var_34 = (+((+((+2.0)))*(+(var_35))));
env_22 = (+alloc(+6));
D32[(~~+floor(+(env_22 + (+0.0)))|0) << 2 >> 2] = +(A);
D32[(~~+floor(+(env_22 + (+1.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_22 + (+2.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_22 + (+3.0)))|0) << 2 >> 2] = +(r_org_x);
D32[(~~+floor(+(env_22 + (+4.0)))|0) << 2 >> 2] = +(r_org_y);
D32[(~~+floor(+(env_22 + (+5.0)))|0) << 2 >> 2] = +(r_org_z);
hoisted_lambda_var_8 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_8 + (+0.0)))|0) << 2 >> 2] = +((+6.0));
D32[(~~+floor(+(hoisted_lambda_var_8 + (+1.0)))|0) << 2 >> 2] = +(env_22);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_8))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_8 + (+1.0)))|0) << 2 >> 2]), var_34));

}
function func_6(env_6, B){
env_6 = +env_6;
B = +B;

var A = 0.0, hoisted_lambda_var_2 = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, env_var_2 = 0.0, r_org_y = 0.0, r_org_z = 0.0;

A = (+D32[(~~+floor(+(env_6 + (+0.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_6 + (+1.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_6 + (+2.0)))|0) << 2 >> 2]);
r_org_x = (+D32[(~~+floor(+(env_6 + (+3.0)))|0) << 2 >> 2]);
r_org_y = (+D32[(~~+floor(+(env_6 + (+4.0)))|0) << 2 >> 2]);
r_org_z = (+D32[(~~+floor(+(env_6 + (+5.0)))|0) << 2 >> 2]);
env_var_2 = (+alloc(+4));
D32[(~~+floor(+(env_var_2 + (+0.0)))|0) << 2 >> 2] = +(A);
D32[(~~+floor(+(env_var_2 + (+1.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_var_2 + (+2.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_var_2 + (+3.0)))|0) << 2 >> 2] = +(B);
hoisted_lambda_var_2 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_2 + (+0.0)))|0) << 2 >> 2] = +((+7.0));
D32[(~~+floor(+(hoisted_lambda_var_2 + (+1.0)))|0) << 2 >> 2] = +(env_var_2);
return +(+vectorsDotProduct(hoisted_lambda_var_2, r_org_x, r_org_y, r_org_z, r_org_x, r_org_y, r_org_z));

}
function func_7(env_7, var_32){
env_7 = +env_7;
var_32 = +var_32;

var env_23 = 0.0, var_31 = 0.0, A = 0.0, hoisted_lambda_var_9 = 0.0, cont_1 = 0.0, s_r = 0.0, B = 0.0, var_33 = 0.0;

A = (+D32[(~~+floor(+(env_7 + (+0.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_7 + (+1.0)))|0) << 2 >> 2]);
s_r = (+D32[(~~+floor(+(env_7 + (+2.0)))|0) << 2 >> 2]);
B = (+D32[(~~+floor(+(env_7 + (+3.0)))|0) << 2 >> 2]);
var_33 = (+((+(s_r))*(+(s_r))));
var_31 = (+((+(var_32))-(+(var_33))));
env_23 = (+alloc(+3));
D32[(~~+floor(+(env_23 + (+0.0)))|0) << 2 >> 2] = +(B);
D32[(~~+floor(+(env_23 + (+1.0)))|0) << 2 >> 2] = +(A);
D32[(~~+floor(+(env_23 + (+2.0)))|0) << 2 >> 2] = +(cont_1);
hoisted_lambda_var_9 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_9 + (+0.0)))|0) << 2 >> 2] = +((+8.0));
D32[(~~+floor(+(hoisted_lambda_var_9 + (+1.0)))|0) << 2 >> 2] = +(env_23);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_9))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_9 + (+1.0)))|0) << 2 >> 2]), var_31));

}
function func_8(env_8, C){
env_8 = +env_8;
C = +C;

var var_30 = 0.0, env_24 = 0.0, A = 0.0, var_27 = 0.0, hoisted_lambda_var_10 = 0.0, cont_1 = 0.0, var_26 = 0.0, B = 0.0, var_29 = 0.0;

B = (+D32[(~~+floor(+(env_8 + (+0.0)))|0) << 2 >> 2]);
A = (+D32[(~~+floor(+(env_8 + (+1.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_8 + (+2.0)))|0) << 2 >> 2]);
var_27 = (+((+(B))*(+(B))));
var_30 = (+((+((+4.0)))*(+(A))));
var_29 = (+((+(var_30))*(+(C))));
var_26 = (+((+(var_27))-(+(var_29))));
env_24 = (+alloc(+3));
D32[(~~+floor(+(env_24 + (+0.0)))|0) << 2 >> 2] = +(A);
D32[(~~+floor(+(env_24 + (+1.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_24 + (+2.0)))|0) << 2 >> 2] = +(B);
hoisted_lambda_var_10 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_10 + (+0.0)))|0) << 2 >> 2] = +((+9.0));
D32[(~~+floor(+(hoisted_lambda_var_10 + (+1.0)))|0) << 2 >> 2] = +(env_24);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_10))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_10 + (+1.0)))|0) << 2 >> 2]), var_26));

}
function func_9(env_9, disc){
env_9 = +env_9;
disc = +disc;

var var_24 = 0.0, var_23 = 0.0, A = 0.0, env_25 = 0.0, cont_1 = 0.0, if_var_0 = 0.0, ret_val_0 = 0.0, var_21 = 0.0, B = 0.0, var_5 = 0.0, hoisted_lambda_var_11 = 0.0, env_var_3 = 0.0, var_7 = 0.0, var_25 = 0.0, var_22 = 0.0;

A = (+D32[(~~+floor(+(env_9 + (+0.0)))|0) << 2 >> 2]);
cont_1 = (+D32[(~~+floor(+(env_9 + (+1.0)))|0) << 2 >> 2]);
B = (+D32[(~~+floor(+(env_9 + (+2.0)))|0) << 2 >> 2]);
var_7 = +(((+(disc))<(+((+0.0))))|0);
env_var_3 = (+alloc(+1));
D32[(~~+floor(+(env_var_3 + (+0.0)))|0) << 2 >> 2] = +(cont_1);
var_5 = (+alloc(+2));
D32[(~~+floor(+(var_5 + (+0.0)))|0) << 2 >> 2] = +((+10.0));
D32[(~~+floor(+(var_5 + (+1.0)))|0) << 2 >> 2] = +(env_var_3);
if_var_0 = var_7;
if ((~~+floor(if_var_0)|0)) {
ret_val_0 = (+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + var_5))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(var_5 + (+1.0)))|0) << 2 >> 2]), (+1000000.0)));
} else {
var_23 = (+(-(B)));
var_24 = (+sqrt(+(disc)));
var_22 = (+((+(var_23))-(+(var_24))));
var_25 = (+((+((+2.0)))*(+(A))));
var_21 = (+((+(var_22))/(+(var_25))));
env_25 = (+alloc(+4));
D32[(~~+floor(+(env_25 + (+0.0)))|0) << 2 >> 2] = +(disc);
D32[(~~+floor(+(env_25 + (+1.0)))|0) << 2 >> 2] = +(A);
D32[(~~+floor(+(env_25 + (+2.0)))|0) << 2 >> 2] = +(B);
D32[(~~+floor(+(env_25 + (+3.0)))|0) << 2 >> 2] = +(var_5);
hoisted_lambda_var_11 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_11 + (+0.0)))|0) << 2 >> 2] = +((+11.0));
D32[(~~+floor(+(hoisted_lambda_var_11 + (+1.0)))|0) << 2 >> 2] = +(env_25);
ret_val_0 = (+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_11))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_11 + (+1.0)))|0) << 2 >> 2]), var_21));
};
return +ret_val_0;

}
function func_10(env_10, var_6){
env_10 = +env_10;
var_6 = +var_6;

var cont_1 = 0.0;

cont_1 = (+D32[(~~+floor(+(env_10 + (+0.0)))|0) << 2 >> 2]);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + cont_1))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(cont_1 + (+1.0)))|0) << 2 >> 2]), var_6));

}
function func_11(env_11, t0){
env_11 = +env_11;
t0 = +t0;

var disc = 0.0, hoisted_lambda_var_12 = 0.0, var_19 = 0.0, var_17 = 0.0, A = 0.0, var_20 = 0.0, var_16 = 0.0, env_26 = 0.0, B = 0.0, var_5 = 0.0, var_18 = 0.0;

disc = (+D32[(~~+floor(+(env_11 + (+0.0)))|0) << 2 >> 2]);
A = (+D32[(~~+floor(+(env_11 + (+1.0)))|0) << 2 >> 2]);
B = (+D32[(~~+floor(+(env_11 + (+2.0)))|0) << 2 >> 2]);
var_5 = (+D32[(~~+floor(+(env_11 + (+3.0)))|0) << 2 >> 2]);
var_18 = (+(-(B)));
var_19 = (+sqrt(+(disc)));
var_17 = (+((+(var_18))+(+(var_19))));
var_20 = (+((+((+2.0)))*(+(A))));
var_16 = (+((+(var_17))/(+(var_20))));
env_26 = (+alloc(+2));
D32[(~~+floor(+(env_26 + (+0.0)))|0) << 2 >> 2] = +(t0);
D32[(~~+floor(+(env_26 + (+1.0)))|0) << 2 >> 2] = +(var_5);
hoisted_lambda_var_12 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_12 + (+0.0)))|0) << 2 >> 2] = +((+12.0));
D32[(~~+floor(+(hoisted_lambda_var_12 + (+1.0)))|0) << 2 >> 2] = +(env_26);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_12))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_12 + (+1.0)))|0) << 2 >> 2]), var_16));

}
function func_12(env_12, t1){
env_12 = +env_12;
t1 = +t1;

var t0 = 0.0, hoisted_lambda_var_13 = 0.0, var_15 = 0.0, var_5 = 0.0, env_27 = 0.0;

t0 = (+D32[(~~+floor(+(env_12 + (+0.0)))|0) << 2 >> 2]);
var_5 = (+D32[(~~+floor(+(env_12 + (+1.0)))|0) << 2 >> 2]);
var_15 = (+max(+(t0), +(t1)));
env_27 = (+alloc(+3));
D32[(~~+floor(+(env_27 + (+0.0)))|0) << 2 >> 2] = +(t0);
D32[(~~+floor(+(env_27 + (+1.0)))|0) << 2 >> 2] = +(t1);
D32[(~~+floor(+(env_27 + (+2.0)))|0) << 2 >> 2] = +(var_5);
hoisted_lambda_var_13 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_13 + (+0.0)))|0) << 2 >> 2] = +((+13.0));
D32[(~~+floor(+(hoisted_lambda_var_13 + (+1.0)))|0) << 2 >> 2] = +(env_27);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_13))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_13 + (+1.0)))|0) << 2 >> 2]), var_15));

}
function func_13(env_13, t_max){
env_13 = +env_13;
t_max = +t_max;

var t0 = 0.0, env_28 = 0.0, hoisted_lambda_var_14 = 0.0, var_5 = 0.0, t1 = 0.0, var_14 = 0.0;

t0 = (+D32[(~~+floor(+(env_13 + (+0.0)))|0) << 2 >> 2]);
t1 = (+D32[(~~+floor(+(env_13 + (+1.0)))|0) << 2 >> 2]);
var_5 = (+D32[(~~+floor(+(env_13 + (+2.0)))|0) << 2 >> 2]);
var_14 = (+min(+(t0), +(t1)));
env_28 = (+alloc(+2));
D32[(~~+floor(+(env_28 + (+0.0)))|0) << 2 >> 2] = +(t_max);
D32[(~~+floor(+(env_28 + (+1.0)))|0) << 2 >> 2] = +(var_5);
hoisted_lambda_var_14 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_14 + (+0.0)))|0) << 2 >> 2] = +((+14.0));
D32[(~~+floor(+(hoisted_lambda_var_14 + (+1.0)))|0) << 2 >> 2] = +(env_28);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_14))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_14 + (+1.0)))|0) << 2 >> 2]), var_14));

}
function func_14(env_14, t_min){
env_14 = +env_14;
t_min = +t_min;

var var_8 = 0.0, if_var_2 = 0.0, var_13 = 0.0, env_var_5 = 0.0, if_var_1 = 0.0, env_var_4 = 0.0, ret_val_1 = 0.0, var_10 = 0.0, var_5 = 0.0, var_11 = 0.0, t_max = 0.0;

t_max = (+D32[(~~+floor(+(env_14 + (+0.0)))|0) << 2 >> 2]);
var_5 = (+D32[(~~+floor(+(env_14 + (+1.0)))|0) << 2 >> 2]);
var_10 = +(((+(t_max))<(+((+0.0))))|0);
env_var_4 = (+alloc(+1));
D32[(~~+floor(+(env_var_4 + (+0.0)))|0) << 2 >> 2] = +(var_5);
var_8 = (+alloc(+2));
D32[(~~+floor(+(var_8 + (+0.0)))|0) << 2 >> 2] = +((+15.0));
D32[(~~+floor(+(var_8 + (+1.0)))|0) << 2 >> 2] = +(env_var_4);
if_var_1 = var_10;
if ((~~+floor(if_var_1)|0)) {
ret_val_1 = (+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + var_8))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(var_8 + (+1.0)))|0) << 2 >> 2]), (+1000000.0)));
} else {
var_13 = +(((+(t_min))<(+((+0.0))))|0);
env_var_5 = (+alloc(+1));
D32[(~~+floor(+(env_var_5 + (+0.0)))|0) << 2 >> 2] = +(var_8);
var_11 = (+alloc(+2));
D32[(~~+floor(+(var_11 + (+0.0)))|0) << 2 >> 2] = +((+16.0));
D32[(~~+floor(+(var_11 + (+1.0)))|0) << 2 >> 2] = +(env_var_5);
if_var_2 = var_13;
if ((~~+floor(if_var_2)|0)) {
ret_val_1 = (+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + var_11))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(var_11 + (+1.0)))|0) << 2 >> 2]), t_max));
} else {
ret_val_1 = (+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + var_11))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(var_11 + (+1.0)))|0) << 2 >> 2]), t_min));
};
};
return +ret_val_1;

}
function func_15(env_15, var_9){
env_15 = +env_15;
var_9 = +var_9;

var var_5 = 0.0;

var_5 = (+D32[(~~+floor(+(env_15 + (+0.0)))|0) << 2 >> 2]);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + var_5))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(var_5 + (+1.0)))|0) << 2 >> 2]), var_9));

}
function func_16(env_16, var_12){
env_16 = +env_16;
var_12 = +var_12;

var var_8 = 0.0;

var_8 = (+D32[(~~+floor(+(env_16 + (+0.0)))|0) << 2 >> 2]);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + var_8))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(var_8 + (+1.0)))|0) << 2 >> 2]), var_12));

}
function func_17(env_17, ident_param_0){
env_17 = +env_17;
ident_param_0 = +ident_param_0;

return +ident_param_0;

}
function func_18(env_18, ident_param_1){
env_18 = +env_18;
ident_param_1 = +ident_param_1;

return +ident_param_1;

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

var_2 = (+((+(v1_x))*(+(v2_x))));
var_3 = (+((+(v1_y))*(+(v2_y))));
var_1 = (+((+(var_2))+(+(var_3))));
var_4 = (+((+(v1_z))*(+(v2_z))));
var_0 = (+((+(var_1))+(+(var_4))));
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + cont_0))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(cont_0 + (+1.0)))|0) << 2 >> 2]), var_0));

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

var hoisted_lambda_var_15 = 0.0, env_29 = 0.0, var_39 = 0.0;

var_39 = (+((+(ro_org_x))-(+(s_x))));
env_29 = (+alloc(+9));
D32[(~~+floor(+(env_29 + (+0.0)))|0) << 2 >> 2] = +(ro_dir_x);
D32[(~~+floor(+(env_29 + (+1.0)))|0) << 2 >> 2] = +(ro_org_z);
D32[(~~+floor(+(env_29 + (+2.0)))|0) << 2 >> 2] = +(s_z);
D32[(~~+floor(+(env_29 + (+3.0)))|0) << 2 >> 2] = +(s_y);
D32[(~~+floor(+(env_29 + (+4.0)))|0) << 2 >> 2] = +(ro_org_y);
D32[(~~+floor(+(env_29 + (+5.0)))|0) << 2 >> 2] = +(ro_dir_y);
D32[(~~+floor(+(env_29 + (+6.0)))|0) << 2 >> 2] = +(cont_1);
D32[(~~+floor(+(env_29 + (+7.0)))|0) << 2 >> 2] = +(s_r);
D32[(~~+floor(+(env_29 + (+8.0)))|0) << 2 >> 2] = +(ro_dir_z);
hoisted_lambda_var_15 = (+alloc(+2));
D32[(~~+floor(+(hoisted_lambda_var_15 + (+0.0)))|0) << 2 >> 2] = +((+0.0));
D32[(~~+floor(+(hoisted_lambda_var_15 + (+1.0)))|0) << 2 >> 2] = +(env_29);
return +(+ftable2[((~~+floor((+D32[(~~+floor(+((+0.0) + hoisted_lambda_var_15))|0) << 2 >> 2]))|0)) & 31]((+D32[(~~+floor(+(hoisted_lambda_var_15 + (+1.0)))|0) << 2 >> 2]), var_39));

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
D32[(~~+floor(+(hoisted_lambda_var_3 + (+0.0)))|0) << 2 >> 2] = +((+17.0));
D32[(~~+floor(+(hoisted_lambda_var_3 + (+1.0)))|0) << 2 >> 2] = +(env_var_6);
return +(+vectorsDotProduct(hoisted_lambda_var_3, v1_x, v1_y, v1_z, v2_x, v2_y, v2_z));

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
D32[(~~+floor(+(hoisted_lambda_var_4 + (+0.0)))|0) << 2 >> 2] = +((+18.0));
D32[(~~+floor(+(hoisted_lambda_var_4 + (+1.0)))|0) << 2 >> 2] = +(env_var_7);
return +(+raySphereIntersectionPoint(hoisted_lambda_var_4, ro_org_x, ro_org_y, ro_org_z, ro_dir_x, ro_dir_y, ro_dir_z, s_x, s_y, s_z, s_r));

}

var ftable6 = [vectorsDotProduct_copy];
var ftable7 = [vectorsDotProduct];
var ftable2 = [func_0,func_1,func_2,func_3,func_4,func_5,func_6,func_7,func_8,func_9,func_10,func_11,func_12,func_13,func_14,func_15,func_16,func_17,func_18,func_0,func_0,func_0,func_0,func_0,func_0,func_0,func_0,func_0,func_0,func_0,func_0,func_0];
var ftable10 = [raySphereIntersectionPoint_copy];
var ftable11 = [raySphereIntersectionPoint];

return {vectorsDotProduct: vectorsDotProduct_copy, raySphereIntersectionPoint: raySphereIntersectionPoint_copy};
}

var module = AsmModule({ Math: Math, Int32Array: Int32Array, Float32Array: Float32Array}, {consoleDotLog: console.log}, new ArrayBuffer(10*4096));

