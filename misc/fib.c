#include <stdio.h>
#include <stdlib.h>

void *func_0(void **env_0, void *var_1);
void *func_1(void **env_1, void *var_4);
void *func_2(void **env_2, void *var_6);
void *func_3(void **env_3, void *ident_param_0);
void *fib(void *cont_0, void *n);
void *fib_copy(void *n);

void *func_0(void **env_0, void *var_1) {
    //printf("func_0\n");
    void *cont_0 = env_0[0];

    void *(*func_pointer_0)(void**,void*) = (void* (*)(void**,void*)) ((void**)cont_0)[0];
    return func_pointer_0(((void**)cont_0)[1], var_1);
}


void *func_1(void **env_1, void *var_4) {
    //printf("func_1\n");
    void *n = env_1[0];
    void *var_0 = env_1[1];

    void *var_7 = (void*)malloc(sizeof(double));
    *(double*)var_7 = *(double*)n - 2.0;

    void **env_var_0 = (void**)malloc(sizeof(void*)*2);
    env_var_0[0] = var_4;
    env_var_0[1] = var_0;

    void **hoisted_lambda_var_0 = (void**)malloc(sizeof(void*)*2);
    hoisted_lambda_var_0[0] = &func_2;
    hoisted_lambda_var_0[1] = env_var_0;

    return fib(hoisted_lambda_var_0, var_7);
}



void *func_2(void **env_2, void *var_6) {
    //printf("func_2\n");
    void *var_4 = env_2[0];
    void *var_0 = env_2[1];
    
    void *var_3 = (void*)malloc(sizeof(double));
    *(double*)var_3 = *(double*)var_4 + *(double*)var_6;

    void *(*func_pointer_1)(void**,void*) = (void* (*)(void**,void*))((void**)var_0)[0];
    return func_pointer_1(((void**)var_0)[1], var_3);
}



void *func_3(void **env_3, void *ident_param_0) {
    //printf("func_3\n");
    return ident_param_0;
}



void *fib(void *cont_0, void *n) {
    //printf("fib\n");
    int *var_2 = (int*)malloc(sizeof(int));
    *var_2 = *(double*)n < 2.0;
    
    void **env_var_1 = (void**)malloc(sizeof(void*)*1);
    env_var_1[0] = cont_0;

    void **var_0 = (void**)malloc(sizeof(void*)*2);
    var_0[0] = &func_0;
    var_0[1] = env_var_1;

    if (*var_2) {
        void *(*func_pointer_2)(void**,void*) = (void* (*)(void**,void*))var_0[0];
        void *const_0 = (void*)malloc(sizeof(double));
        *(double*)const_0 = 1.0;
        return func_pointer_2(var_0[1], const_0);
    } else {
        void *var_5 = (void*)malloc(sizeof(double));
        *(double*)var_5 = *(double*)n - 1.0;

        void **env_var_2 = (void**)malloc(sizeof(void*)*2);
        env_var_2[0] = n;
        env_var_2[1] = var_0;

        void **hoisted_lambda_var_1 = (void**)malloc(sizeof(void*)*2);
        hoisted_lambda_var_1[0] = &func_1;
        hoisted_lambda_var_1[1] = env_var_2;

        return fib(hoisted_lambda_var_1, var_5);
    }
}


void *fib_copy(void *n) {
    //printf("fib_copy\n");

    void **env_var_3 = (void**)malloc(sizeof(void*)*0);

    void **hoisted_lambda_var_2 = (void**)malloc(sizeof(void*)*2);
    hoisted_lambda_var_2[0] = &func_3;
    hoisted_lambda_var_2[1] = env_var_3;

    return fib(hoisted_lambda_var_2, n);
}


int main(int argc, char **argv) {
    double *n = (double*)malloc(sizeof(double));
    *n = 6.0;

    printf("%f\n", *(double*)fib_copy(n));

}
