#include <stdio.h>
#include <stdlib.h>

void *func_0(void *env_0, void *var_1);
void *func_1(void *env_1, void *var_4);
void *func_2(void *env_2, void *var_6);
void *func_3(void *env_3, void *ident_param_0);
void *fib(void *cont_0, void *n);
void *fib_copy(void *n);

void *func_0(void *env_0, void *var_1) {
    // cast env
    void **env_0c = (void**)env_0;

    // local vars
    void *cont_0;
    void **casted_hl_var_0; 
    void *(*func_pointer_0)(void*,void*);

    // assign env vars
    cont_0 = env_0c[0];

    // body
    casted_hl_var_0 = (void**)cont_0;
    func_pointer_0 = (void* (*)(void*,void*))casted_hl_var_0[0];
    return func_pointer_0(casted_hl_var_0[1], var_1);
}


void *func_1(void *env_1, void *var_4) {
    // cast env
    void **env_1c = (void**)env_1;

    // assign env vars
    void *n = env_1c[0];
    void *var_0 = env_1c[1];
    
    // local vars
    void *var_7;
    void **env_var_0;
    void **hoisted_lambda_var_0;

    // body
    var_7 = (void*)malloc(sizeof(double));
    *(double*)var_7 = *(double*)n - 2.0;

    env_var_0 = (void**)malloc(sizeof(void*)*2);
    env_var_0[0] = var_4;
    env_var_0[1] = var_0;

    hoisted_lambda_var_0 = (void**)malloc(sizeof(void*)*2);
    hoisted_lambda_var_0[0] = &func_2;
    hoisted_lambda_var_0[1] = env_var_0;

    return fib(hoisted_lambda_var_0, var_7);
}



void *func_2(void *env_2, void *var_6) {
    // cast env
    void **env_2c = (void**)env_2;

    // assign env vars
    void *var_4 = env_2c[0];
    void *var_0 = env_2c[1];

    // local vars
    void *var_3;
    void **casted_hl_var_1; 
    void *(*func_pointer_1)(void*,void*);
    
    // body
    var_3 = (void*)malloc(sizeof(double));
    *(double*)var_3 = *(double*)var_4 + *(double*)var_6;

    casted_hl_var_1 = (void**)var_0;
    func_pointer_1 = (void* (*)(void*,void*))casted_hl_var_1[0];
    return func_pointer_1(casted_hl_var_1[1], var_3);
}



void *func_3(void *env_3, void *ident_param_0) {
    void **env_3c = (void**)env_3;

    return ident_param_0;
}



void *fib(void *cont_0, void *n) {
    // local vars
    void *var_2;
    void **env_var_1;
    void **var_0;
    void **casted_hl_var_2;
    void *(*func_pointer_2)(void**,void*);
    void *var_5;
    void **env_var_2;
    void **hoisted_lambda_var_1;

    // body
    var_2 = (void*)malloc(sizeof(int));
    *(int*)var_2 = *(double*)n < 2.0;
    
    env_var_1 = (void**)malloc(sizeof(void*)*1);
    env_var_1[0] = cont_0;

    var_0 = (void**)malloc(sizeof(void*)*2);
    var_0[0] = &func_0;
    var_0[1] = env_var_1;

    if (*(int*)var_2) {
        casted_hl_var_2 = (void**)var_0;
        func_pointer_2 = (void* (*)(void**,void*))casted_hl_var_2[0];
        void *const_0 = (void*)malloc(sizeof(double));
        *(double*)const_0 = 1.0;
        return func_pointer_2(casted_hl_var_2[1], const_0);
    } else {
        var_5 = (void*)malloc(sizeof(double));
        *(double*)var_5 = *(double*)n - 1.0;

        env_var_2 = (void**)malloc(sizeof(void*)*2);
        env_var_2[0] = n;
        env_var_2[1] = var_0;

        hoisted_lambda_var_1 = (void**)malloc(sizeof(void*)*2);
        hoisted_lambda_var_1[0] = &func_1;
        hoisted_lambda_var_1[1] = env_var_2;

        return fib(hoisted_lambda_var_1, var_5);
    }
}


void *fib_copy(void *n) {
    // local vars
    void **env_var_3;
    void **hoisted_lambda_var_2;

    // body
    env_var_3 = (void**)malloc(sizeof(void*)*0);

    hoisted_lambda_var_2 = (void**)malloc(sizeof(void*)*2);
    hoisted_lambda_var_2[0] = &func_3;
    hoisted_lambda_var_2[1] = env_var_3;

    return fib(hoisted_lambda_var_2, n);
}


int main(int argc, char **argv) {
    double *n = (double*)malloc(sizeof(double));
    *n = 6.0;

    printf("%f\n", *(double*)fib_copy(n));

}
