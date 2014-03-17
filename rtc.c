
    #include <stdio.h>
    #include <stdlib.h>
    #include <math.h>
    #include "pool.c"
    #define min(x,y) ((x)<(y)?(x):(y))
    #define max(x,y) ((x)>(y)?(x):(y))
    void *func_0(void *env_0, void *var_36);
void *func_1(void *env_1, void *var_35);
void *func_2(void *env_2, void *var_32);
void *func_3(void *env_3, void *var_6);
void *func_4(void *env_4, void *var_9);
void *func_5(void *env_5, void *var_12);
void *func_6(void *env_6, void *ident_param_0);
void *func_7(void *env_7, void *ident_param_1);
void *vectorsDotProduct(void *cont_0, void *v1_x, void *v1_y, void *v1_z, void *v2_x, void *v2_y, void *v2_z);
void *raySphereIntersectionPoint(void *cont_1, void *ro_org_x, void *ro_org_y, void *ro_org_z, void *ro_dir_x, void *ro_dir_y, void *ro_dir_z, void *s_x, void *s_y, void *s_z, void *s_r);
void *vectorsDotProduct_copy(void *v1_x, void *v1_y, void *v1_z, void *v2_x, void *v2_y, void *v2_z);
void *raySphereIntersectionPoint_copy(void *ro_org_x, void *ro_org_y, void *ro_org_z, void *ro_dir_x, void *ro_dir_y, void *ro_dir_z, void *s_x, void *s_y, void *s_z, void *s_r);
double vectorsDotProduct_copy_by_value(double v1_x, double v1_y, double v1_z, double v2_x, double v2_y, double v2_z);
double raySphereIntersectionPoint_copy_by_value(double ro_org_x, double ro_org_y, double ro_org_z, double ro_dir_x, double ro_dir_y, double ro_dir_z, double s_x, double s_y, double s_z, double s_r);

void* func_0(void *env_0, void *var_36) {
void** env_0c;
void* ro_dir_x;
void* ro_dir_y;
void* cont_1;
void* s_r;
void* r_org_x;
void* ro_dir_z;
void* r_org_y;
void* r_org_z;
double* A;
void** env_var_0;
void** hoisted_lambda_var_0;
void* ret_val_1;

env_0c = (void**)env_0;
ro_dir_x = env_0c[0];
ro_dir_y = env_0c[1];
cont_1 = env_0c[2];
s_r = env_0c[3];
r_org_x = env_0c[4];
ro_dir_z = env_0c[5];
r_org_y = env_0c[6];
r_org_z = env_0c[7];
A = var_36;
env_var_0 = (void**)jalloc(sizeof(void*) * 6);
env_var_0[0] = A;
env_var_0[1] = cont_1;
env_var_0[2] = s_r;
env_var_0[3] = r_org_x;
env_var_0[4] = r_org_y;
env_var_0[5] = r_org_z;
hoisted_lambda_var_0 = (void**)jalloc(sizeof(void*) * 2);
hoisted_lambda_var_0[0] = &func_1;
hoisted_lambda_var_0[1] = env_var_0;
ret_val_1 = vectorsDotProduct(hoisted_lambda_var_0, r_org_x, r_org_y, r_org_z, ro_dir_x, ro_dir_y, ro_dir_z);
return ret_val_1;
}

void* func_1(void *env_1, void *var_35) {
void** env_1c;
void* A;
void* cont_1;
void* s_r;
void* r_org_x;
void* r_org_y;
void* r_org_z;
double* prim_op_p_0;
double prim_op_v_0;
double* var_34;
double* B;
void** env_var_1;
void** hoisted_lambda_var_1;
void* ret_val_2;

env_1c = (void**)env_1;
A = env_1c[0];
cont_1 = env_1c[1];
s_r = env_1c[2];
r_org_x = env_1c[3];
r_org_y = env_1c[4];
r_org_z = env_1c[5];
prim_op_p_0 = (double*)var_35;
prim_op_v_0 = *prim_op_p_0;
var_34 = (double*)jalloc(sizeof(double) * 1);
*var_34 = 2.0*prim_op_v_0;
B = var_34;
env_var_1 = (void**)jalloc(sizeof(void*) * 4);
env_var_1[0] = A;
env_var_1[1] = cont_1;
env_var_1[2] = s_r;
env_var_1[3] = B;
hoisted_lambda_var_1 = (void**)jalloc(sizeof(void*) * 2);
hoisted_lambda_var_1[0] = &func_2;
hoisted_lambda_var_1[1] = env_var_1;
ret_val_2 = vectorsDotProduct(hoisted_lambda_var_1, r_org_x, r_org_y, r_org_z, r_org_x, r_org_y, r_org_z);
return ret_val_2;
}

void* func_2(void *env_2, void *var_32) {
void** env_2c;
void* A;
void* cont_1;
void* s_r;
void* B;
double* prim_op_p_1;
double prim_op_v_1;
double* prim_op_p_2;
double prim_op_v_2;
double* var_33;
double* prim_op_p_3;
double prim_op_v_3;
double* prim_op_p_4;
double prim_op_v_4;
double* var_31;
double* C;
double* prim_op_p_5;
double prim_op_v_5;
double* prim_op_p_6;
double prim_op_v_6;
double* var_27;
double* prim_op_p_7;
double prim_op_v_7;
double* var_30;
double* prim_op_p_8;
double prim_op_v_8;
double* prim_op_p_9;
double prim_op_v_9;
double* var_29;
double* prim_op_p_10;
double prim_op_v_10;
double* prim_op_p_11;
double prim_op_v_11;
double* var_26;
double* disc;
double* prim_op_p_12;
double prim_op_v_12;
int* var_7;
void** env_var_2;
void** var_5;
int* cast_if_var_pointer_0;
int if_var_0;
void** casted_hl_var_0;
void* uncast_func_pointer_0;
void *(*func_pointer_0)(void*,void*);;
void* env_param_0;
double* const_0;
double* prim_op_p_13;
double prim_op_v_13;
double* var_23;
double* prim_op_p_14;
double prim_op_v_14;
double* var_24;
double* prim_op_p_15;
double prim_op_v_15;
double* prim_op_p_16;
double prim_op_v_16;
double* var_22;
double* prim_op_p_17;
double prim_op_v_17;
double* var_25;
double* prim_op_p_18;
double prim_op_v_18;
double* prim_op_p_19;
double prim_op_v_19;
double* var_21;
double* t0;
double* prim_op_p_20;
double prim_op_v_20;
double* var_18;
double* prim_op_p_21;
double prim_op_v_21;
double* var_19;
double* prim_op_p_22;
double prim_op_v_22;
double* prim_op_p_23;
double prim_op_v_23;
double* var_17;
double* prim_op_p_24;
double prim_op_v_24;
double* var_20;
double* prim_op_p_25;
double prim_op_v_25;
double* prim_op_p_26;
double prim_op_v_26;
double* var_16;
double* t1;
double* prim_op_p_27;
double prim_op_v_27;
double* prim_op_p_28;
double prim_op_v_28;
double* var_15;
double* t_max;
double* prim_op_p_29;
double prim_op_v_29;
double* prim_op_p_30;
double prim_op_v_30;
double* var_14;
double* t_min;
double* prim_op_p_31;
double prim_op_v_31;
int* var_10;
void** env_var_3;
void** var_8;
int* cast_if_var_pointer_1;
int if_var_1;
void** casted_hl_var_1;
void* uncast_func_pointer_1;
void *(*func_pointer_1)(void*,void*);;
void* env_param_1;
double* const_1;
double* prim_op_p_32;
double prim_op_v_32;
int* var_13;
void** env_var_4;
void** var_11;
int* cast_if_var_pointer_2;
int if_var_2;
void** casted_hl_var_2;
void* uncast_func_pointer_2;
void *(*func_pointer_2)(void*,void*);;
void* env_param_2;
void** casted_hl_var_3;
void* uncast_func_pointer_3;
void *(*func_pointer_3)(void*,void*);;
void* env_param_3;
void* ret_val_3;

env_2c = (void**)env_2;
A = env_2c[0];
cont_1 = env_2c[1];
s_r = env_2c[2];
B = env_2c[3];
prim_op_p_1 = (double*)s_r;
prim_op_v_1 = *prim_op_p_1;
prim_op_p_2 = (double*)s_r;
prim_op_v_2 = *prim_op_p_2;
var_33 = (double*)jalloc(sizeof(double) * 1);
*var_33 = prim_op_v_1*prim_op_v_2;
prim_op_p_3 = (double*)var_32;
prim_op_v_3 = *prim_op_p_3;
prim_op_p_4 = (double*)var_33;
prim_op_v_4 = *prim_op_p_4;
var_31 = (double*)jalloc(sizeof(double) * 1);
*var_31 = prim_op_v_3-prim_op_v_4;
C = var_31;
prim_op_p_5 = (double*)B;
prim_op_v_5 = *prim_op_p_5;
prim_op_p_6 = (double*)B;
prim_op_v_6 = *prim_op_p_6;
var_27 = (double*)jalloc(sizeof(double) * 1);
*var_27 = prim_op_v_5*prim_op_v_6;
prim_op_p_7 = (double*)A;
prim_op_v_7 = *prim_op_p_7;
var_30 = (double*)jalloc(sizeof(double) * 1);
*var_30 = 4.0*prim_op_v_7;
prim_op_p_8 = (double*)var_30;
prim_op_v_8 = *prim_op_p_8;
prim_op_p_9 = (double*)C;
prim_op_v_9 = *prim_op_p_9;
var_29 = (double*)jalloc(sizeof(double) * 1);
*var_29 = prim_op_v_8*prim_op_v_9;
prim_op_p_10 = (double*)var_27;
prim_op_v_10 = *prim_op_p_10;
prim_op_p_11 = (double*)var_29;
prim_op_v_11 = *prim_op_p_11;
var_26 = (double*)jalloc(sizeof(double) * 1);
*var_26 = prim_op_v_10-prim_op_v_11;
disc = var_26;
prim_op_p_12 = (double*)disc;
prim_op_v_12 = *prim_op_p_12;
var_7 = (int*)jalloc(sizeof(int) * 1);
*var_7 = prim_op_v_12<0.0;
env_var_2 = (void**)jalloc(sizeof(void*) * 1);
env_var_2[0] = cont_1;
var_5 = (void**)jalloc(sizeof(void*) * 2);
var_5[0] = &func_3;
var_5[1] = env_var_2;
cast_if_var_pointer_0 = (int*)var_7;
if_var_0 = *cast_if_var_pointer_0;
if (if_var_0) {
casted_hl_var_0 = (void**)var_5;
uncast_func_pointer_0 = casted_hl_var_0[0];
func_pointer_0 = (void* (*)(void*,void*))uncast_func_pointer_0;
env_param_0 = casted_hl_var_0[1];
const_0 = (double*)jalloc(sizeof(double) * 1);
*const_0 = 1000000.0;
ret_val_3 = func_pointer_0(env_param_0, const_0);
} else {
prim_op_p_13 = (double*)B;
prim_op_v_13 = *prim_op_p_13;
var_23 = (double*)jalloc(sizeof(double) * 1);
*var_23 = 0.0-prim_op_v_13;
prim_op_p_14 = (double*)disc;
prim_op_v_14 = *prim_op_p_14;
var_24 = (double*)jalloc(sizeof(double) * 1);
*var_24 = sqrt(prim_op_v_14);
prim_op_p_15 = (double*)var_23;
prim_op_v_15 = *prim_op_p_15;
prim_op_p_16 = (double*)var_24;
prim_op_v_16 = *prim_op_p_16;
var_22 = (double*)jalloc(sizeof(double) * 1);
*var_22 = prim_op_v_15-prim_op_v_16;
prim_op_p_17 = (double*)A;
prim_op_v_17 = *prim_op_p_17;
var_25 = (double*)jalloc(sizeof(double) * 1);
*var_25 = 2.0*prim_op_v_17;
prim_op_p_18 = (double*)var_22;
prim_op_v_18 = *prim_op_p_18;
prim_op_p_19 = (double*)var_25;
prim_op_v_19 = *prim_op_p_19;
var_21 = (double*)jalloc(sizeof(double) * 1);
*var_21 = prim_op_v_18/prim_op_v_19;
t0 = var_21;
prim_op_p_20 = (double*)B;
prim_op_v_20 = *prim_op_p_20;
var_18 = (double*)jalloc(sizeof(double) * 1);
*var_18 = 0.0-prim_op_v_20;
prim_op_p_21 = (double*)disc;
prim_op_v_21 = *prim_op_p_21;
var_19 = (double*)jalloc(sizeof(double) * 1);
*var_19 = sqrt(prim_op_v_21);
prim_op_p_22 = (double*)var_18;
prim_op_v_22 = *prim_op_p_22;
prim_op_p_23 = (double*)var_19;
prim_op_v_23 = *prim_op_p_23;
var_17 = (double*)jalloc(sizeof(double) * 1);
*var_17 = prim_op_v_22+prim_op_v_23;
prim_op_p_24 = (double*)A;
prim_op_v_24 = *prim_op_p_24;
var_20 = (double*)jalloc(sizeof(double) * 1);
*var_20 = 2.0*prim_op_v_24;
prim_op_p_25 = (double*)var_17;
prim_op_v_25 = *prim_op_p_25;
prim_op_p_26 = (double*)var_20;
prim_op_v_26 = *prim_op_p_26;
var_16 = (double*)jalloc(sizeof(double) * 1);
*var_16 = prim_op_v_25/prim_op_v_26;
t1 = var_16;
prim_op_p_27 = (double*)t0;
prim_op_v_27 = *prim_op_p_27;
prim_op_p_28 = (double*)t1;
prim_op_v_28 = *prim_op_p_28;
var_15 = (double*)jalloc(sizeof(double) * 1);
*var_15 = max(prim_op_v_27, prim_op_v_28);
t_max = var_15;
prim_op_p_29 = (double*)t0;
prim_op_v_29 = *prim_op_p_29;
prim_op_p_30 = (double*)t1;
prim_op_v_30 = *prim_op_p_30;
var_14 = (double*)jalloc(sizeof(double) * 1);
*var_14 = min(prim_op_v_29, prim_op_v_30);
t_min = var_14;
prim_op_p_31 = (double*)t_max;
prim_op_v_31 = *prim_op_p_31;
var_10 = (int*)jalloc(sizeof(int) * 1);
*var_10 = prim_op_v_31<0.0;
env_var_3 = (void**)jalloc(sizeof(void*) * 1);
env_var_3[0] = var_5;
var_8 = (void**)jalloc(sizeof(void*) * 2);
var_8[0] = &func_4;
var_8[1] = env_var_3;
cast_if_var_pointer_1 = (int*)var_10;
if_var_1 = *cast_if_var_pointer_1;
if (if_var_1) {
casted_hl_var_1 = (void**)var_8;
uncast_func_pointer_1 = casted_hl_var_1[0];
func_pointer_1 = (void* (*)(void*,void*))uncast_func_pointer_1;
env_param_1 = casted_hl_var_1[1];
const_1 = (double*)jalloc(sizeof(double) * 1);
*const_1 = 1000000.0;
ret_val_3 = func_pointer_1(env_param_1, const_1);
} else {
prim_op_p_32 = (double*)t_min;
prim_op_v_32 = *prim_op_p_32;
var_13 = (int*)jalloc(sizeof(int) * 1);
*var_13 = prim_op_v_32<0.0;
env_var_4 = (void**)jalloc(sizeof(void*) * 1);
env_var_4[0] = var_8;
var_11 = (void**)jalloc(sizeof(void*) * 2);
var_11[0] = &func_5;
var_11[1] = env_var_4;
cast_if_var_pointer_2 = (int*)var_13;
if_var_2 = *cast_if_var_pointer_2;
if (if_var_2) {
casted_hl_var_2 = (void**)var_11;
uncast_func_pointer_2 = casted_hl_var_2[0];
func_pointer_2 = (void* (*)(void*,void*))uncast_func_pointer_2;
env_param_2 = casted_hl_var_2[1];
ret_val_3 = func_pointer_2(env_param_2, t_max);
} else {
casted_hl_var_3 = (void**)var_11;
uncast_func_pointer_3 = casted_hl_var_3[0];
func_pointer_3 = (void* (*)(void*,void*))uncast_func_pointer_3;
env_param_3 = casted_hl_var_3[1];
ret_val_3 = func_pointer_3(env_param_3, t_min);
};
};
};
return ret_val_3;
}

void* func_3(void *env_3, void *var_6) {
void** env_3c;
void* cont_1;
void** casted_hl_var_4;
void* uncast_func_pointer_4;
void *(*func_pointer_4)(void*,void*);;
void* env_param_4;
void* ret_val_4;

env_3c = (void**)env_3;
cont_1 = env_3c[0];
casted_hl_var_4 = (void**)cont_1;
uncast_func_pointer_4 = casted_hl_var_4[0];
func_pointer_4 = (void* (*)(void*,void*))uncast_func_pointer_4;
env_param_4 = casted_hl_var_4[1];
ret_val_4 = func_pointer_4(env_param_4, var_6);
return ret_val_4;
}

void* func_4(void *env_4, void *var_9) {
void** env_4c;
void* var_5;
void** casted_hl_var_5;
void* uncast_func_pointer_5;
void *(*func_pointer_5)(void*,void*);;
void* env_param_5;
void* ret_val_5;

env_4c = (void**)env_4;
var_5 = env_4c[0];
casted_hl_var_5 = (void**)var_5;
uncast_func_pointer_5 = casted_hl_var_5[0];
func_pointer_5 = (void* (*)(void*,void*))uncast_func_pointer_5;
env_param_5 = casted_hl_var_5[1];
ret_val_5 = func_pointer_5(env_param_5, var_9);
return ret_val_5;
}

void* func_5(void *env_5, void *var_12) {
void** env_5c;
void* var_8;
void** casted_hl_var_6;
void* uncast_func_pointer_6;
void *(*func_pointer_6)(void*,void*);;
void* env_param_6;
void* ret_val_6;

env_5c = (void**)env_5;
var_8 = env_5c[0];
casted_hl_var_6 = (void**)var_8;
uncast_func_pointer_6 = casted_hl_var_6[0];
func_pointer_6 = (void* (*)(void*,void*))uncast_func_pointer_6;
env_param_6 = casted_hl_var_6[1];
ret_val_6 = func_pointer_6(env_param_6, var_12);
return ret_val_6;
}

void* func_6(void *env_6, void *ident_param_0) {
void** env_6c;
void* ret_val_7;

env_6c = (void**)env_6;
ret_val_7 = ident_param_0;
return ret_val_7;
}

void* func_7(void *env_7, void *ident_param_1) {
void** env_7c;
void* ret_val_8;

env_7c = (void**)env_7;
ret_val_8 = ident_param_1;
return ret_val_8;
}

void* vectorsDotProduct(void *cont_0, void *v1_x, void *v1_y, void *v1_z, void *v2_x, void *v2_y, void *v2_z) {
double* prim_op_p_33;
double prim_op_v_33;
double* prim_op_p_34;
double prim_op_v_34;
double* var_2;
double* prim_op_p_35;
double prim_op_v_35;
double* prim_op_p_36;
double prim_op_v_36;
double* var_3;
double* prim_op_p_37;
double prim_op_v_37;
double* prim_op_p_38;
double prim_op_v_38;
double* var_1;
double* prim_op_p_39;
double prim_op_v_39;
double* prim_op_p_40;
double prim_op_v_40;
double* var_4;
double* prim_op_p_41;
double prim_op_v_41;
double* prim_op_p_42;
double prim_op_v_42;
double* var_0;
void** casted_hl_var_7;
void* uncast_func_pointer_7;
void *(*func_pointer_7)(void*,void*);;
void* env_param_7;
void* ret_val_9;

prim_op_p_33 = (double*)v1_x;
prim_op_v_33 = *prim_op_p_33;
prim_op_p_34 = (double*)v2_x;
prim_op_v_34 = *prim_op_p_34;
var_2 = (double*)jalloc(sizeof(double) * 1);
*var_2 = prim_op_v_33*prim_op_v_34;
prim_op_p_35 = (double*)v1_y;
prim_op_v_35 = *prim_op_p_35;
prim_op_p_36 = (double*)v2_y;
prim_op_v_36 = *prim_op_p_36;
var_3 = (double*)jalloc(sizeof(double) * 1);
*var_3 = prim_op_v_35*prim_op_v_36;
prim_op_p_37 = (double*)var_2;
prim_op_v_37 = *prim_op_p_37;
prim_op_p_38 = (double*)var_3;
prim_op_v_38 = *prim_op_p_38;
var_1 = (double*)jalloc(sizeof(double) * 1);
*var_1 = prim_op_v_37+prim_op_v_38;
prim_op_p_39 = (double*)v1_z;
prim_op_v_39 = *prim_op_p_39;
prim_op_p_40 = (double*)v2_z;
prim_op_v_40 = *prim_op_p_40;
var_4 = (double*)jalloc(sizeof(double) * 1);
*var_4 = prim_op_v_39*prim_op_v_40;
prim_op_p_41 = (double*)var_1;
prim_op_v_41 = *prim_op_p_41;
prim_op_p_42 = (double*)var_4;
prim_op_v_42 = *prim_op_p_42;
var_0 = (double*)jalloc(sizeof(double) * 1);
*var_0 = prim_op_v_41+prim_op_v_42;
casted_hl_var_7 = (void**)cont_0;
uncast_func_pointer_7 = casted_hl_var_7[0];
func_pointer_7 = (void* (*)(void*,void*))uncast_func_pointer_7;
env_param_7 = casted_hl_var_7[1];
ret_val_9 = func_pointer_7(env_param_7, var_0);
return ret_val_9;
}

void* raySphereIntersectionPoint(void *cont_1, void *ro_org_x, void *ro_org_y, void *ro_org_z, void *ro_dir_x, void *ro_dir_y, void *ro_dir_z, void *s_x, void *s_y, void *s_z, void *s_r) {
double* prim_op_p_43;
double prim_op_v_43;
double* prim_op_p_44;
double prim_op_v_44;
double* var_39;
double* r_org_x;
double* prim_op_p_45;
double prim_op_v_45;
double* prim_op_p_46;
double prim_op_v_46;
double* var_38;
double* r_org_y;
double* prim_op_p_47;
double prim_op_v_47;
double* prim_op_p_48;
double prim_op_v_48;
double* var_37;
double* r_org_z;
void** env_var_5;
void** hoisted_lambda_var_2;
void* ret_val_10;

prim_op_p_43 = (double*)ro_org_x;
prim_op_v_43 = *prim_op_p_43;
prim_op_p_44 = (double*)s_x;
prim_op_v_44 = *prim_op_p_44;
var_39 = (double*)jalloc(sizeof(double) * 1);
*var_39 = prim_op_v_43-prim_op_v_44;
r_org_x = var_39;
prim_op_p_45 = (double*)ro_org_y;
prim_op_v_45 = *prim_op_p_45;
prim_op_p_46 = (double*)s_y;
prim_op_v_46 = *prim_op_p_46;
var_38 = (double*)jalloc(sizeof(double) * 1);
*var_38 = prim_op_v_45-prim_op_v_46;
r_org_y = var_38;
prim_op_p_47 = (double*)ro_org_z;
prim_op_v_47 = *prim_op_p_47;
prim_op_p_48 = (double*)s_z;
prim_op_v_48 = *prim_op_p_48;
var_37 = (double*)jalloc(sizeof(double) * 1);
*var_37 = prim_op_v_47-prim_op_v_48;
r_org_z = var_37;
env_var_5 = (void**)jalloc(sizeof(void*) * 8);
env_var_5[0] = ro_dir_x;
env_var_5[1] = ro_dir_y;
env_var_5[2] = cont_1;
env_var_5[3] = s_r;
env_var_5[4] = r_org_x;
env_var_5[5] = ro_dir_z;
env_var_5[6] = r_org_y;
env_var_5[7] = r_org_z;
hoisted_lambda_var_2 = (void**)jalloc(sizeof(void*) * 2);
hoisted_lambda_var_2[0] = &func_0;
hoisted_lambda_var_2[1] = env_var_5;
ret_val_10 = vectorsDotProduct(hoisted_lambda_var_2, ro_dir_x, ro_dir_y, ro_dir_z, ro_dir_x, ro_dir_y, ro_dir_z);
return ret_val_10;
}

void* vectorsDotProduct_copy(void *v1_x, void *v1_y, void *v1_z, void *v2_x, void *v2_y, void *v2_z) {
void** env_var_6;
void** hoisted_lambda_var_3;
void* ret_val_11;

env_var_6 = (void**)jalloc(sizeof(void*) * 0);
hoisted_lambda_var_3 = (void**)jalloc(sizeof(void*) * 2);
hoisted_lambda_var_3[0] = &func_6;
hoisted_lambda_var_3[1] = env_var_6;
ret_val_11 = vectorsDotProduct(hoisted_lambda_var_3, v1_x, v1_y, v1_z, v2_x, v2_y, v2_z);
return ret_val_11;
}

void* raySphereIntersectionPoint_copy(void *ro_org_x, void *ro_org_y, void *ro_org_z, void *ro_dir_x, void *ro_dir_y, void *ro_dir_z, void *s_x, void *s_y, void *s_z, void *s_r) {
void** env_var_7;
void** hoisted_lambda_var_4;
void* ret_val_12;

env_var_7 = (void**)jalloc(sizeof(void*) * 0);
hoisted_lambda_var_4 = (void**)jalloc(sizeof(void*) * 2);
hoisted_lambda_var_4[0] = &func_7;
hoisted_lambda_var_4[1] = env_var_7;
ret_val_12 = raySphereIntersectionPoint(hoisted_lambda_var_4, ro_org_x, ro_org_y, ro_org_z, ro_dir_x, ro_dir_y, ro_dir_z, s_x, s_y, s_z, s_r);
return ret_val_12;
}

double vectorsDotProduct_copy_by_value(double v1_x, double v1_y, double v1_z, double v2_x, double v2_y, double v2_z) {
double* v1_x_p;
double* v1_y_p;
double* v1_z_p;
double* v2_x_p;
double* v2_y_p;
double* v2_z_p;
void* ret_val_13;
double* ret_val_cast_0;
double ret_val_double_0;

free_all();
v1_x_p = (double*)jalloc(sizeof(double) * 1);
*v1_x_p = v1_x;
v1_y_p = (double*)jalloc(sizeof(double) * 1);
*v1_y_p = v1_y;
v1_z_p = (double*)jalloc(sizeof(double) * 1);
*v1_z_p = v1_z;
v2_x_p = (double*)jalloc(sizeof(double) * 1);
*v2_x_p = v2_x;
v2_y_p = (double*)jalloc(sizeof(double) * 1);
*v2_y_p = v2_y;
v2_z_p = (double*)jalloc(sizeof(double) * 1);
*v2_z_p = v2_z;
ret_val_13 = vectorsDotProduct_copy(v1_x_p, v1_y_p, v1_z_p, v2_x_p, v2_y_p, v2_z_p);
ret_val_cast_0 = (double*)ret_val_13;
ret_val_double_0 = *ret_val_cast_0;
return ret_val_double_0;
}

double raySphereIntersectionPoint_copy_by_value(double ro_org_x, double ro_org_y, double ro_org_z, double ro_dir_x, double ro_dir_y, double ro_dir_z, double s_x, double s_y, double s_z, double s_r) {
double* ro_org_x_p;
double* ro_org_y_p;
double* ro_org_z_p;
double* ro_dir_x_p;
double* ro_dir_y_p;
double* ro_dir_z_p;
double* s_x_p;
double* s_y_p;
double* s_z_p;
double* s_r_p;
void* ret_val_14;
double* ret_val_cast_1;
double ret_val_double_1;

free_all();
ro_org_x_p = (double*)jalloc(sizeof(double) * 1);
*ro_org_x_p = ro_org_x;
ro_org_y_p = (double*)jalloc(sizeof(double) * 1);
*ro_org_y_p = ro_org_y;
ro_org_z_p = (double*)jalloc(sizeof(double) * 1);
*ro_org_z_p = ro_org_z;
ro_dir_x_p = (double*)jalloc(sizeof(double) * 1);
*ro_dir_x_p = ro_dir_x;
ro_dir_y_p = (double*)jalloc(sizeof(double) * 1);
*ro_dir_y_p = ro_dir_y;
ro_dir_z_p = (double*)jalloc(sizeof(double) * 1);
*ro_dir_z_p = ro_dir_z;
s_x_p = (double*)jalloc(sizeof(double) * 1);
*s_x_p = s_x;
s_y_p = (double*)jalloc(sizeof(double) * 1);
*s_y_p = s_y;
s_z_p = (double*)jalloc(sizeof(double) * 1);
*s_z_p = s_z;
s_r_p = (double*)jalloc(sizeof(double) * 1);
*s_r_p = s_r;
ret_val_14 = raySphereIntersectionPoint_copy(ro_org_x_p, ro_org_y_p, ro_org_z_p, ro_dir_x_p, ro_dir_y_p, ro_dir_z_p, s_x_p, s_y_p, s_z_p, s_r_p);
ret_val_cast_1 = (double*)ret_val_14;
ret_val_double_1 = *ret_val_cast_1;
return ret_val_double_1;
}
        int main(int argc, char **argv) {
          printf("Emscripten asm.js module loaded\n");
        }
        
