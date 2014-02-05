
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

      function get_array_element(base, offset) {
          base = +base;
          offset = +offset;

          var addr = 0.0;

          addr = +(base + offset);
          return +D32[(~~+floor(addr)|0) << 2 >> 2];
      }


      function set_array_element(base, offset, value) {
          base = +base;
          offset = +offset;
          value = +value;
          
          var addr = 0.0;

          addr = +(base + offset);
          D32[(~~+floor(addr)|0) << 2 >> 2] = value;
      }

      function make_hoisted_lambda(f_index, env_pointer) {
          f_index = +f_index;
          env_pointer = +env_pointer;

          var a = 0.0;

          a = +alloc(2.0);
          set_array_element(a, 0.0, f_index);
          set_array_element(a, 1.0, env_pointer);
          return +a;
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

var ro_dir_x = 0.0, ro_org_z = 0.0, s_z = 0.0, env_30 = 0.0, s_y = 0.0, ro_org_y = 0.0, hoisted_lambda_var_5 = 0.0, var_49 = 0.0, ro_dir_y = 0.0, cont_1 = 0.0, s_r = 0.0, ro_dir_z = 0.0;

ro_dir_x = (+get_array_element(env_0, (+0.0)));
ro_org_z = (+get_array_element(env_0, (+1.0)));
s_z = (+get_array_element(env_0, (+2.0)));
s_y = (+get_array_element(env_0, (+3.0)));
ro_org_y = (+get_array_element(env_0, (+4.0)));
ro_dir_y = (+get_array_element(env_0, (+5.0)));
cont_1 = (+get_array_element(env_0, (+6.0)));
s_r = (+get_array_element(env_0, (+7.0)));
ro_dir_z = (+get_array_element(env_0, (+8.0)));
var_49 = (+((+(ro_org_y))-(+(s_y))));
env_30 = (+alloc(+8));
set_array_element(env_30, (+0.0), ro_dir_x);
set_array_element(env_30, (+1.0), ro_org_z);
set_array_element(env_30, (+2.0), s_z);
set_array_element(env_30, (+3.0), ro_dir_y);
set_array_element(env_30, (+4.0), cont_1);
set_array_element(env_30, (+5.0), s_r);
set_array_element(env_30, (+6.0), r_org_x);
set_array_element(env_30, (+7.0), ro_dir_z);
hoisted_lambda_var_5 = (+make_hoisted_lambda((+1.0), env_30));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_5)))|0)) & 31]((+get_array_element(hoisted_lambda_var_5, (+1.0))), var_49));

}
function func_1(env_1, r_org_y){
env_1 = +env_1;
r_org_y = +r_org_y;

var ro_dir_x = 0.0, ro_org_z = 0.0, s_z = 0.0, ro_dir_y = 0.0, hoisted_lambda_var_6 = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, var_48 = 0.0, env_31 = 0.0;

ro_dir_x = (+get_array_element(env_1, (+0.0)));
ro_org_z = (+get_array_element(env_1, (+1.0)));
s_z = (+get_array_element(env_1, (+2.0)));
ro_dir_y = (+get_array_element(env_1, (+3.0)));
cont_1 = (+get_array_element(env_1, (+4.0)));
s_r = (+get_array_element(env_1, (+5.0)));
r_org_x = (+get_array_element(env_1, (+6.0)));
ro_dir_z = (+get_array_element(env_1, (+7.0)));
var_48 = (+((+(ro_org_z))-(+(s_z))));
env_31 = (+alloc(+7));
set_array_element(env_31, (+0.0), ro_dir_x);
set_array_element(env_31, (+1.0), ro_dir_y);
set_array_element(env_31, (+2.0), cont_1);
set_array_element(env_31, (+3.0), s_r);
set_array_element(env_31, (+4.0), r_org_x);
set_array_element(env_31, (+5.0), ro_dir_z);
set_array_element(env_31, (+6.0), r_org_y);
hoisted_lambda_var_6 = (+make_hoisted_lambda((+2.0), env_31));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_6)))|0)) & 31]((+get_array_element(hoisted_lambda_var_6, (+1.0))), var_48));

}
function func_2(env_2, r_org_z){
env_2 = +env_2;
r_org_z = +r_org_z;

var env_var_0 = 0.0, ro_dir_x = 0.0, ro_dir_y = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, r_org_y = 0.0, hoisted_lambda_var_0 = 0.0;

ro_dir_x = (+get_array_element(env_2, (+0.0)));
ro_dir_y = (+get_array_element(env_2, (+1.0)));
cont_1 = (+get_array_element(env_2, (+2.0)));
s_r = (+get_array_element(env_2, (+3.0)));
r_org_x = (+get_array_element(env_2, (+4.0)));
ro_dir_z = (+get_array_element(env_2, (+5.0)));
r_org_y = (+get_array_element(env_2, (+6.0)));
env_var_0 = (+alloc(+8));
set_array_element(env_var_0, (+0.0), ro_dir_x);
set_array_element(env_var_0, (+1.0), ro_dir_y);
set_array_element(env_var_0, (+2.0), cont_1);
set_array_element(env_var_0, (+3.0), s_r);
set_array_element(env_var_0, (+4.0), r_org_x);
set_array_element(env_var_0, (+5.0), ro_dir_z);
set_array_element(env_var_0, (+6.0), r_org_y);
set_array_element(env_var_0, (+7.0), r_org_z);
hoisted_lambda_var_0 = (+make_hoisted_lambda((+3.0), env_var_0));
return +(+vectorsDotProduct(hoisted_lambda_var_0, ro_dir_x, ro_dir_y, ro_dir_z, ro_dir_x, ro_dir_y, ro_dir_z));

}
function func_3(env_3, var_47){
env_3 = +env_3;
var_47 = +var_47;

var ro_dir_x = 0.0, ro_dir_y = 0.0, cont_1 = 0.0, s_r = 0.0, env_32 = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, hoisted_lambda_var_7 = 0.0, r_org_y = 0.0, r_org_z = 0.0;

ro_dir_x = (+get_array_element(env_3, (+0.0)));
ro_dir_y = (+get_array_element(env_3, (+1.0)));
cont_1 = (+get_array_element(env_3, (+2.0)));
s_r = (+get_array_element(env_3, (+3.0)));
r_org_x = (+get_array_element(env_3, (+4.0)));
ro_dir_z = (+get_array_element(env_3, (+5.0)));
r_org_y = (+get_array_element(env_3, (+6.0)));
r_org_z = (+get_array_element(env_3, (+7.0)));
env_32 = (+alloc(+8));
set_array_element(env_32, (+0.0), ro_dir_x);
set_array_element(env_32, (+1.0), ro_dir_y);
set_array_element(env_32, (+2.0), cont_1);
set_array_element(env_32, (+3.0), s_r);
set_array_element(env_32, (+4.0), r_org_x);
set_array_element(env_32, (+5.0), ro_dir_z);
set_array_element(env_32, (+6.0), r_org_y);
set_array_element(env_32, (+7.0), r_org_z);
hoisted_lambda_var_7 = (+make_hoisted_lambda((+4.0), env_32));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_7)))|0)) & 31]((+get_array_element(hoisted_lambda_var_7, (+1.0))), var_47));

}
function func_4(env_4, A){
env_4 = +env_4;
A = +A;

var ro_dir_x = 0.0, env_var_1 = 0.0, ro_dir_y = 0.0, hoisted_lambda_var_1 = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, ro_dir_z = 0.0, r_org_y = 0.0, r_org_z = 0.0;

ro_dir_x = (+get_array_element(env_4, (+0.0)));
ro_dir_y = (+get_array_element(env_4, (+1.0)));
cont_1 = (+get_array_element(env_4, (+2.0)));
s_r = (+get_array_element(env_4, (+3.0)));
r_org_x = (+get_array_element(env_4, (+4.0)));
ro_dir_z = (+get_array_element(env_4, (+5.0)));
r_org_y = (+get_array_element(env_4, (+6.0)));
r_org_z = (+get_array_element(env_4, (+7.0)));
env_var_1 = (+alloc(+6));
set_array_element(env_var_1, (+0.0), A);
set_array_element(env_var_1, (+1.0), cont_1);
set_array_element(env_var_1, (+2.0), s_r);
set_array_element(env_var_1, (+3.0), r_org_x);
set_array_element(env_var_1, (+4.0), r_org_y);
set_array_element(env_var_1, (+5.0), r_org_z);
hoisted_lambda_var_1 = (+make_hoisted_lambda((+5.0), env_var_1));
return +(+vectorsDotProduct(hoisted_lambda_var_1, r_org_x, r_org_y, r_org_z, ro_dir_x, ro_dir_y, ro_dir_z));

}
function func_5(env_5, var_46){
env_5 = +env_5;
var_46 = +var_46;

var hoisted_lambda_var_8 = 0.0, var_45 = 0.0, A = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, r_org_y = 0.0, env_33 = 0.0, r_org_z = 0.0;

A = (+get_array_element(env_5, (+0.0)));
cont_1 = (+get_array_element(env_5, (+1.0)));
s_r = (+get_array_element(env_5, (+2.0)));
r_org_x = (+get_array_element(env_5, (+3.0)));
r_org_y = (+get_array_element(env_5, (+4.0)));
r_org_z = (+get_array_element(env_5, (+5.0)));
var_45 = (+((+((+2.0)))*(+(var_46))));
env_33 = (+alloc(+6));
set_array_element(env_33, (+0.0), A);
set_array_element(env_33, (+1.0), cont_1);
set_array_element(env_33, (+2.0), s_r);
set_array_element(env_33, (+3.0), r_org_x);
set_array_element(env_33, (+4.0), r_org_y);
set_array_element(env_33, (+5.0), r_org_z);
hoisted_lambda_var_8 = (+make_hoisted_lambda((+6.0), env_33));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_8)))|0)) & 31]((+get_array_element(hoisted_lambda_var_8, (+1.0))), var_45));

}
function func_6(env_6, B){
env_6 = +env_6;
B = +B;

var A = 0.0, hoisted_lambda_var_2 = 0.0, cont_1 = 0.0, s_r = 0.0, r_org_x = 0.0, env_var_2 = 0.0, r_org_y = 0.0, r_org_z = 0.0;

A = (+get_array_element(env_6, (+0.0)));
cont_1 = (+get_array_element(env_6, (+1.0)));
s_r = (+get_array_element(env_6, (+2.0)));
r_org_x = (+get_array_element(env_6, (+3.0)));
r_org_y = (+get_array_element(env_6, (+4.0)));
r_org_z = (+get_array_element(env_6, (+5.0)));
env_var_2 = (+alloc(+4));
set_array_element(env_var_2, (+0.0), A);
set_array_element(env_var_2, (+1.0), cont_1);
set_array_element(env_var_2, (+2.0), s_r);
set_array_element(env_var_2, (+3.0), B);
hoisted_lambda_var_2 = (+make_hoisted_lambda((+7.0), env_var_2));
return +(+vectorsDotProduct(hoisted_lambda_var_2, r_org_x, r_org_y, r_org_z, r_org_x, r_org_y, r_org_z));

}
function func_7(env_7, var_43){
env_7 = +env_7;
var_43 = +var_43;

var env_34 = 0.0, A = 0.0, hoisted_lambda_var_9 = 0.0, cont_1 = 0.0, s_r = 0.0, var_42 = 0.0, B = 0.0, var_44 = 0.0;

A = (+get_array_element(env_7, (+0.0)));
cont_1 = (+get_array_element(env_7, (+1.0)));
s_r = (+get_array_element(env_7, (+2.0)));
B = (+get_array_element(env_7, (+3.0)));
var_44 = (+((+(s_r))*(+(s_r))));
var_42 = (+((+(var_43))-(+(var_44))));
env_34 = (+alloc(+3));
set_array_element(env_34, (+0.0), B);
set_array_element(env_34, (+1.0), A);
set_array_element(env_34, (+2.0), cont_1);
hoisted_lambda_var_9 = (+make_hoisted_lambda((+8.0), env_34));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_9)))|0)) & 31]((+get_array_element(hoisted_lambda_var_9, (+1.0))), var_42));

}
function func_8(env_8, C){
env_8 = +env_8;
C = +C;

var var_41 = 0.0, env_35 = 0.0, A = 0.0, hoisted_lambda_var_10 = 0.0, var_38 = 0.0, cont_1 = 0.0, var_37 = 0.0, B = 0.0, var_40 = 0.0;

B = (+get_array_element(env_8, (+0.0)));
A = (+get_array_element(env_8, (+1.0)));
cont_1 = (+get_array_element(env_8, (+2.0)));
var_38 = (+((+(B))*(+(B))));
var_41 = (+((+((+4.0)))*(+(A))));
var_40 = (+((+(var_41))*(+(C))));
var_37 = (+((+(var_38))-(+(var_40))));
env_35 = (+alloc(+3));
set_array_element(env_35, (+0.0), A);
set_array_element(env_35, (+1.0), cont_1);
set_array_element(env_35, (+2.0), B);
hoisted_lambda_var_10 = (+make_hoisted_lambda((+9.0), env_35));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_10)))|0)) & 31]((+get_array_element(hoisted_lambda_var_10, (+1.0))), var_37));

}
function func_9(env_9, disc){
env_9 = +env_9;
disc = +disc;

var var_35 = 0.0, env_36 = 0.0, var_34 = 0.0, A = 0.0, var_12 = 0.0, var_32 = 0.0, cont_1 = 0.0, if_var_0 = 0.0, ret_val_0 = 0.0, B = 0.0, hoisted_lambda_var_11 = 0.0, env_var_3 = 0.0, var_36 = 0.0, var_33 = 0.0, var_14 = 0.0;

A = (+get_array_element(env_9, (+0.0)));
cont_1 = (+get_array_element(env_9, (+1.0)));
B = (+get_array_element(env_9, (+2.0)));
var_14 = +(((+(disc))<(+((+0.0))))|0);
env_var_3 = (+alloc(+1));
set_array_element(env_var_3, (+0.0), cont_1);
var_12 = (+make_hoisted_lambda((+10.0), env_var_3));
if_var_0 = var_14;
if ((~~+floor(if_var_0)|0)) {
ret_val_0 = (+ftable2[((~~+floor((+get_array_element((+0.0), var_12)))|0)) & 31]((+get_array_element(var_12, (+1.0))), (+1000000.0)));
} else {
var_34 = (+(-(B)));
var_35 = (+sqrt(+(disc)));
var_33 = (+((+(var_34))-(+(var_35))));
var_36 = (+((+((+2.0)))*(+(A))));
var_32 = (+((+(var_33))/(+(var_36))));
env_36 = (+alloc(+4));
set_array_element(env_36, (+0.0), disc);
set_array_element(env_36, (+1.0), A);
set_array_element(env_36, (+2.0), var_12);
set_array_element(env_36, (+3.0), B);
hoisted_lambda_var_11 = (+make_hoisted_lambda((+18.0), env_36));
ret_val_0 = (+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_11)))|0)) & 31]((+get_array_element(hoisted_lambda_var_11, (+1.0))), var_32));
};
return +ret_val_0;

}
function func_10(env_10, var_13){
env_10 = +env_10;
var_13 = +var_13;

var hoisted_lambda_var_12 = 0.0, env_37 = 0.0, cont_1 = 0.0;

cont_1 = (+get_array_element(env_10, (+0.0)));
env_37 = (+alloc(+1));
set_array_element(env_37, (+0.0), cont_1);
hoisted_lambda_var_12 = (+make_hoisted_lambda((+11.0), env_37));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_12)))|0)) & 31]((+get_array_element(hoisted_lambda_var_12, (+1.0))), var_13));

}
function func_11(env_11, var_11){
env_11 = +env_11;
var_11 = +var_11;

var hoisted_lambda_var_13 = 0.0, env_38 = 0.0, cont_1 = 0.0;

cont_1 = (+get_array_element(env_11, (+0.0)));
env_38 = (+alloc(+1));
set_array_element(env_38, (+0.0), cont_1);
hoisted_lambda_var_13 = (+make_hoisted_lambda((+12.0), env_38));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_13)))|0)) & 31]((+get_array_element(hoisted_lambda_var_13, (+1.0))), var_11));

}
function func_12(env_12, var_10){
env_12 = +env_12;
var_10 = +var_10;

var hoisted_lambda_var_14 = 0.0, env_39 = 0.0, cont_1 = 0.0;

cont_1 = (+get_array_element(env_12, (+0.0)));
env_39 = (+alloc(+1));
set_array_element(env_39, (+0.0), cont_1);
hoisted_lambda_var_14 = (+make_hoisted_lambda((+13.0), env_39));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_14)))|0)) & 31]((+get_array_element(hoisted_lambda_var_14, (+1.0))), var_10));

}
function func_13(env_13, var_9){
env_13 = +env_13;
var_9 = +var_9;

var hoisted_lambda_var_15 = 0.0, env_40 = 0.0, cont_1 = 0.0;

cont_1 = (+get_array_element(env_13, (+0.0)));
env_40 = (+alloc(+1));
set_array_element(env_40, (+0.0), cont_1);
hoisted_lambda_var_15 = (+make_hoisted_lambda((+14.0), env_40));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_15)))|0)) & 31]((+get_array_element(hoisted_lambda_var_15, (+1.0))), var_9));

}
function func_14(env_14, var_8){
env_14 = +env_14;
var_8 = +var_8;

var hoisted_lambda_var_16 = 0.0, env_41 = 0.0, cont_1 = 0.0;

cont_1 = (+get_array_element(env_14, (+0.0)));
env_41 = (+alloc(+1));
set_array_element(env_41, (+0.0), cont_1);
hoisted_lambda_var_16 = (+make_hoisted_lambda((+15.0), env_41));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_16)))|0)) & 31]((+get_array_element(hoisted_lambda_var_16, (+1.0))), var_8));

}
function func_15(env_15, var_7){
env_15 = +env_15;
var_7 = +var_7;

var hoisted_lambda_var_17 = 0.0, env_42 = 0.0, cont_1 = 0.0;

cont_1 = (+get_array_element(env_15, (+0.0)));
env_42 = (+alloc(+1));
set_array_element(env_42, (+0.0), cont_1);
hoisted_lambda_var_17 = (+make_hoisted_lambda((+16.0), env_42));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_17)))|0)) & 31]((+get_array_element(hoisted_lambda_var_17, (+1.0))), var_7));

}
function func_16(env_16, var_6){
env_16 = +env_16;
var_6 = +var_6;

var hoisted_lambda_var_18 = 0.0, env_43 = 0.0, cont_1 = 0.0;

cont_1 = (+get_array_element(env_16, (+0.0)));
env_43 = (+alloc(+1));
set_array_element(env_43, (+0.0), cont_1);
hoisted_lambda_var_18 = (+make_hoisted_lambda((+17.0), env_43));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_18)))|0)) & 31]((+get_array_element(hoisted_lambda_var_18, (+1.0))), var_6));

}
function func_17(env_17, var_5){
env_17 = +env_17;
var_5 = +var_5;

var cont_1 = 0.0;

cont_1 = (+get_array_element(env_17, (+0.0)));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), cont_1)))|0)) & 31]((+get_array_element(cont_1, (+1.0))), var_5));

}
function func_18(env_18, t0){
env_18 = +env_18;
t0 = +t0;

var disc = 0.0, var_30 = 0.0, var_28 = 0.0, var_31 = 0.0, A = 0.0, var_12 = 0.0, var_27 = 0.0, B = 0.0, env_44 = 0.0, hoisted_lambda_var_19 = 0.0, var_29 = 0.0;

disc = (+get_array_element(env_18, (+0.0)));
A = (+get_array_element(env_18, (+1.0)));
var_12 = (+get_array_element(env_18, (+2.0)));
B = (+get_array_element(env_18, (+3.0)));
var_29 = (+(-(B)));
var_30 = (+sqrt(+(disc)));
var_28 = (+((+(var_29))+(+(var_30))));
var_31 = (+((+((+2.0)))*(+(A))));
var_27 = (+((+(var_28))/(+(var_31))));
env_44 = (+alloc(+2));
set_array_element(env_44, (+0.0), t0);
set_array_element(env_44, (+1.0), var_12);
hoisted_lambda_var_19 = (+make_hoisted_lambda((+19.0), env_44));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_19)))|0)) & 31]((+get_array_element(hoisted_lambda_var_19, (+1.0))), var_27));

}
function func_19(env_19, t1){
env_19 = +env_19;
t1 = +t1;

var t0 = 0.0, var_12 = 0.0, hoisted_lambda_var_20 = 0.0, var_26 = 0.0, env_45 = 0.0;

t0 = (+get_array_element(env_19, (+0.0)));
var_12 = (+get_array_element(env_19, (+1.0)));
var_26 = (+max(+(t0), +(t1)));
env_45 = (+alloc(+3));
set_array_element(env_45, (+0.0), t0);
set_array_element(env_45, (+1.0), t1);
set_array_element(env_45, (+2.0), var_12);
hoisted_lambda_var_20 = (+make_hoisted_lambda((+20.0), env_45));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_20)))|0)) & 31]((+get_array_element(hoisted_lambda_var_20, (+1.0))), var_26));

}
function func_20(env_20, t_max){
env_20 = +env_20;
t_max = +t_max;

var env_46 = 0.0, t0 = 0.0, var_12 = 0.0, hoisted_lambda_var_21 = 0.0, t1 = 0.0, var_25 = 0.0;

t0 = (+get_array_element(env_20, (+0.0)));
t1 = (+get_array_element(env_20, (+1.0)));
var_12 = (+get_array_element(env_20, (+2.0)));
var_25 = (+min(+(t0), +(t1)));
env_46 = (+alloc(+2));
set_array_element(env_46, (+0.0), t_max);
set_array_element(env_46, (+1.0), var_12);
hoisted_lambda_var_21 = (+make_hoisted_lambda((+21.0), env_46));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_21)))|0)) & 31]((+get_array_element(hoisted_lambda_var_21, (+1.0))), var_25));

}
function func_21(env_21, t_min){
env_21 = +env_21;
t_min = +t_min;

var var_24 = 0.0, if_var_2 = 0.0, env_var_5 = 0.0, var_19 = 0.0, var_12 = 0.0, if_var_1 = 0.0, env_var_4 = 0.0, ret_val_1 = 0.0, var_21 = 0.0, t_max = 0.0, var_22 = 0.0;

t_max = (+get_array_element(env_21, (+0.0)));
var_12 = (+get_array_element(env_21, (+1.0)));
var_21 = +(((+(t_max))<(+((+0.0))))|0);
env_var_4 = (+alloc(+1));
set_array_element(env_var_4, (+0.0), var_12);
var_19 = (+make_hoisted_lambda((+22.0), env_var_4));
if_var_1 = var_21;
if ((~~+floor(if_var_1)|0)) {
ret_val_1 = (+ftable2[((~~+floor((+get_array_element((+0.0), var_19)))|0)) & 31]((+get_array_element(var_19, (+1.0))), (+1000000.0)));
} else {
var_24 = +(((+(t_min))<(+((+0.0))))|0);
env_var_5 = (+alloc(+1));
set_array_element(env_var_5, (+0.0), var_19);
var_22 = (+make_hoisted_lambda((+27.0), env_var_5));
if_var_2 = var_24;
if ((~~+floor(if_var_2)|0)) {
ret_val_1 = (+ftable2[((~~+floor((+get_array_element((+0.0), var_22)))|0)) & 31]((+get_array_element(var_22, (+1.0))), t_max));
} else {
ret_val_1 = (+ftable2[((~~+floor((+get_array_element((+0.0), var_22)))|0)) & 31]((+get_array_element(var_22, (+1.0))), t_min));
};
};
return +ret_val_1;

}
function func_22(env_22, var_20){
env_22 = +env_22;
var_20 = +var_20;

var hoisted_lambda_var_22 = 0.0, env_47 = 0.0, var_12 = 0.0;

var_12 = (+get_array_element(env_22, (+0.0)));
env_47 = (+alloc(+1));
set_array_element(env_47, (+0.0), var_12);
hoisted_lambda_var_22 = (+make_hoisted_lambda((+23.0), env_47));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_22)))|0)) & 31]((+get_array_element(hoisted_lambda_var_22, (+1.0))), var_20));

}
function func_23(env_23, var_18){
env_23 = +env_23;
var_18 = +var_18;

var hoisted_lambda_var_23 = 0.0, env_48 = 0.0, var_12 = 0.0;

var_12 = (+get_array_element(env_23, (+0.0)));
env_48 = (+alloc(+1));
set_array_element(env_48, (+0.0), var_12);
hoisted_lambda_var_23 = (+make_hoisted_lambda((+24.0), env_48));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_23)))|0)) & 31]((+get_array_element(hoisted_lambda_var_23, (+1.0))), var_18));

}
function func_24(env_24, var_17){
env_24 = +env_24;
var_17 = +var_17;

var hoisted_lambda_var_24 = 0.0, env_49 = 0.0, var_12 = 0.0;

var_12 = (+get_array_element(env_24, (+0.0)));
env_49 = (+alloc(+1));
set_array_element(env_49, (+0.0), var_12);
hoisted_lambda_var_24 = (+make_hoisted_lambda((+25.0), env_49));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_24)))|0)) & 31]((+get_array_element(hoisted_lambda_var_24, (+1.0))), var_17));

}
function func_25(env_25, var_16){
env_25 = +env_25;
var_16 = +var_16;

var hoisted_lambda_var_25 = 0.0, env_50 = 0.0, var_12 = 0.0;

var_12 = (+get_array_element(env_25, (+0.0)));
env_50 = (+alloc(+1));
set_array_element(env_50, (+0.0), var_12);
hoisted_lambda_var_25 = (+make_hoisted_lambda((+26.0), env_50));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_25)))|0)) & 31]((+get_array_element(hoisted_lambda_var_25, (+1.0))), var_16));

}
function func_26(env_26, var_15){
env_26 = +env_26;
var_15 = +var_15;

var var_12 = 0.0;

var_12 = (+get_array_element(env_26, (+0.0)));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), var_12)))|0)) & 31]((+get_array_element(var_12, (+1.0))), var_15));

}
function func_27(env_27, var_23){
env_27 = +env_27;
var_23 = +var_23;

var var_19 = 0.0;

var_19 = (+get_array_element(env_27, (+0.0)));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), var_19)))|0)) & 31]((+get_array_element(var_19, (+1.0))), var_23));

}
function func_28(env_28, ident_param_0){
env_28 = +env_28;
ident_param_0 = +ident_param_0;

return +ident_param_0;

}
function func_29(env_29, ident_param_1){
env_29 = +env_29;
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
return +(+ftable2[((~~+floor((+get_array_element((+0.0), cont_0)))|0)) & 31]((+get_array_element(cont_0, (+1.0))), var_0));

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

var hoisted_lambda_var_26 = 0.0, env_51 = 0.0, var_50 = 0.0;

var_50 = (+((+(ro_org_x))-(+(s_x))));
env_51 = (+alloc(+9));
set_array_element(env_51, (+0.0), ro_dir_x);
set_array_element(env_51, (+1.0), ro_org_z);
set_array_element(env_51, (+2.0), s_z);
set_array_element(env_51, (+3.0), s_y);
set_array_element(env_51, (+4.0), ro_org_y);
set_array_element(env_51, (+5.0), ro_dir_y);
set_array_element(env_51, (+6.0), cont_1);
set_array_element(env_51, (+7.0), s_r);
set_array_element(env_51, (+8.0), ro_dir_z);
hoisted_lambda_var_26 = (+make_hoisted_lambda((+0.0), env_51));
return +(+ftable2[((~~+floor((+get_array_element((+0.0), hoisted_lambda_var_26)))|0)) & 31]((+get_array_element(hoisted_lambda_var_26, (+1.0))), var_50));

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
hoisted_lambda_var_3 = (+make_hoisted_lambda((+28.0), env_var_6));
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
hoisted_lambda_var_4 = (+make_hoisted_lambda((+29.0), env_var_7));
return +(+raySphereIntersectionPoint(hoisted_lambda_var_4, ro_org_x, ro_org_y, ro_org_z, ro_dir_x, ro_dir_y, ro_dir_z, s_x, s_y, s_z, s_r));

}

var ftable6 = [vectorsDotProduct_copy];
var ftable7 = [vectorsDotProduct];
var ftable2 = [func_0,func_1,func_2,func_3,func_4,func_5,func_6,func_7,func_8,func_9,func_10,func_11,func_12,func_13,func_14,func_15,func_16,func_17,func_18,func_19,func_20,func_21,func_22,func_23,func_24,func_25,func_26,func_27,func_28,func_29,func_0,func_0];
var ftable10 = [raySphereIntersectionPoint_copy];
var ftable11 = [raySphereIntersectionPoint];

return {vectorsDotProduct: vectorsDotProduct_copy, raySphereIntersectionPoint: raySphereIntersectionPoint_copy};
}



var module = AsmModule({ Math: Math, Int32Array: Int32Array, Float32Array: Float32Array}, {consoleDotLog: console.log}, new ArrayBuffer(10*4096));

