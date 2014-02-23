@.str = private unnamed_addr constant [4 x i8] c"%f\0A\00"
declare i8* @malloc(i64)
declare i32 @printf(i8*, ...)

define i8* @func_0(i8* %env_0, i8* %var_1) {
  %1 = alloca i8*
  %2 = alloca i8*
  %env_0c = alloca i8**
  %casted_hl_var_0 = alloca i8**
  %func_pointer_0 = alloca i8* (i8*, i8*)*
  %cont_0 = alloca i8*
  store i8* %env_0, i8** %1
  store i8* %var_1, i8** %2
  %3 = load i8** %1
  %4 = bitcast i8* %3 to i8**
  store i8** %4, i8*** %env_0c
  %5 = load i8*** %env_0c
  %6 = getelementptr inbounds i8** %5, i64 0
  %7 = load i8** %6
  store i8* %7, i8** %cont_0
  %8 = load i8** %cont_0
  %9 = bitcast i8* %8 to i8**
  store i8** %9, i8*** %casted_hl_var_0
  %10 = load i8*** %casted_hl_var_0
  %11 = getelementptr inbounds i8** %10, i64 0
  %12 = load i8** %11
  %13 = bitcast i8* %12 to i8* (i8*, i8*)*
  store i8* (i8*, i8*)* %13, i8* (i8*, i8*)** %func_pointer_0
  %14 = load i8* (i8*, i8*)** %func_pointer_0
  %15 = load i8*** %casted_hl_var_0
  %16 = getelementptr inbounds i8** %15, i64 1
  %17 = load i8** %16
  %18 = load i8** %2
  %19 = call i8* %14(i8* %17, i8* %18)
  ret i8* %19
}

define i8* @func_1(i8* %env_1, i8* %var_4) {
  %1 = alloca i8*
  %2 = alloca i8*
  %env_1c = alloca i8**
  %n = alloca i8*
  %var_0 = alloca i8*
  %var_7 = alloca i8*
  %env_var_0 = alloca i8**
  %hoisted_lambda_var_0 = alloca i8**
  store i8* %env_1, i8** %1
  store i8* %var_4, i8** %2
  %3 = load i8** %1
  %4 = bitcast i8* %3 to i8**
  store i8** %4, i8*** %env_1c
  %5 = load i8*** %env_1c
  %6 = getelementptr inbounds i8** %5, i64 0
  %7 = load i8** %6
  store i8* %7, i8** %n
  %8 = load i8*** %env_1c
  %9 = getelementptr inbounds i8** %8, i64 1
  %10 = load i8** %9
  store i8* %10, i8** %var_0
  %11 = call i8* @malloc(i64 8)
  store i8* %11, i8** %var_7
  %12 = load i8** %n
  %13 = bitcast i8* %12 to double*
  %14 = load double* %13
  %15 = fsub double %14, 2.000000e+00
  %16 = load i8** %var_7
  %17 = bitcast i8* %16 to double*
  store double %15, double* %17
  %18 = call i8* @malloc(i64 16)
  %19 = bitcast i8* %18 to i8**
  store i8** %19, i8*** %env_var_0
  %20 = load i8** %2
  %21 = load i8*** %env_var_0
  %22 = getelementptr inbounds i8** %21, i64 0
  store i8* %20, i8** %22
  %23 = load i8** %var_0
  %24 = load i8*** %env_var_0
  %25 = getelementptr inbounds i8** %24, i64 1
  store i8* %23, i8** %25
  %26 = call i8* @malloc(i64 16)
  %27 = bitcast i8* %26 to i8**
  store i8** %27, i8*** %hoisted_lambda_var_0
  %28 = load i8*** %hoisted_lambda_var_0
  %29 = getelementptr inbounds i8** %28, i64 0
  store i8* bitcast (i8* (i8*, i8*)* @func_2 to i8*), i8** %29
  %30 = load i8*** %env_var_0
  %31 = bitcast i8** %30 to i8*
  %32 = load i8*** %hoisted_lambda_var_0
  %33 = getelementptr inbounds i8** %32, i64 1
  store i8* %31, i8** %33
  %34 = load i8*** %hoisted_lambda_var_0
  %35 = bitcast i8** %34 to i8*
  %36 = load i8** %var_7
  %37 = call i8* @fib(i8* %35, i8* %36)
  ret i8* %37
}

define i8* @func_2(i8* %env_2, i8* %var_6) {
  %1 = alloca i8*
  %2 = alloca i8*
  %env_2c = alloca i8**
  %casted_hl_var_1 = alloca i8**
  %func_pointer_1 = alloca i8* (i8*, i8*)*
  %var_4 = alloca i8*
  %var_0 = alloca i8*
  %var_3 = alloca i8*
  store i8* %env_2, i8** %1
  store i8* %var_6, i8** %2
  %3 = load i8** %1
  %4 = bitcast i8* %3 to i8**
  store i8** %4, i8*** %env_2c
  %5 = load i8*** %env_2c
  %6 = getelementptr inbounds i8** %5, i64 0
  %7 = load i8** %6
  store i8* %7, i8** %var_4
  %8 = load i8*** %env_2c
  %9 = getelementptr inbounds i8** %8, i64 1
  %10 = load i8** %9
  store i8* %10, i8** %var_0
  %11 = call i8* @malloc(i64 8)
  store i8* %11, i8** %var_3
  %12 = load i8** %var_4
  %13 = bitcast i8* %12 to double*
  %14 = load double* %13
  %15 = load i8** %2
  %16 = bitcast i8* %15 to double*
  %17 = load double* %16
  %18 = fadd double %14, %17
  %19 = load i8** %var_3
  %20 = bitcast i8* %19 to double*
  store double %18, double* %20
  %21 = load i8** %var_0
  %22 = bitcast i8* %21 to i8**
  store i8** %22, i8*** %casted_hl_var_1
  %23 = load i8*** %casted_hl_var_1
  %24 = getelementptr inbounds i8** %23, i64 0
  %25 = load i8** %24
  %26 = bitcast i8* %25 to i8* (i8*, i8*)*
  store i8* (i8*, i8*)* %26, i8* (i8*, i8*)** %func_pointer_1
  %27 = load i8* (i8*, i8*)** %func_pointer_1
  %28 = load i8*** %casted_hl_var_1
  %29 = getelementptr inbounds i8** %28, i64 1
  %30 = load i8** %29
  %31 = load i8** %var_3
  %32 = call i8* %27(i8* %30, i8* %31)
  ret i8* %32
}

define i8* @func_3(i8* %env_3, i8* %ident_param_0) {
  %1 = alloca i8*
  %2 = alloca i8*
  %env_3c = alloca i8**
  store i8* %env_3, i8** %1
  store i8* %ident_param_0, i8** %2
  %3 = load i8** %1
  %4 = bitcast i8* %3 to i8**
  store i8** %4, i8*** %env_3c
  %5 = load i8** %2
  ret i8* %5
}

define i8* @fib(i8* %cont_0, i8* %n) {
  %1 = alloca i8*
  %2 = alloca i8*
  %3 = alloca i8*
  %var_2 = alloca i32*
  %env_var_1 = alloca i8**
  %var_0 = alloca i8**
  %func_pointer_2 = alloca i8* (i8**, i8*)*
  %const_0 = alloca i8*
  %var_5 = alloca i8*
  %env_var_2 = alloca i8**
  %hoisted_lambda_var_1 = alloca i8**
  store i8* %cont_0, i8** %2
  store i8* %n, i8** %3
  %4 = call i8* @malloc(i64 4)
  %5 = bitcast i8* %4 to i32*
  store i32* %5, i32** %var_2
  %6 = load i8** %3
  %7 = bitcast i8* %6 to double*
  %8 = load double* %7
  %9 = fcmp olt double %8, 2.000000e+00
  %10 = zext i1 %9 to i32
  %11 = load i32** %var_2
  store i32 %10, i32* %11
  %12 = call i8* @malloc(i64 8)
  %13 = bitcast i8* %12 to i8**
  store i8** %13, i8*** %env_var_1
  %14 = load i8** %2
  %15 = load i8*** %env_var_1
  %16 = getelementptr inbounds i8** %15, i64 0
  store i8* %14, i8** %16
  %17 = call i8* @malloc(i64 16)
  %18 = bitcast i8* %17 to i8**
  store i8** %18, i8*** %var_0
  %19 = load i8*** %var_0
  %20 = getelementptr inbounds i8** %19, i64 0
  store i8* bitcast (i8* (i8*, i8*)* @func_0 to i8*), i8** %20
  %21 = load i8*** %env_var_1
  %22 = bitcast i8** %21 to i8*
  %23 = load i8*** %var_0
  %24 = getelementptr inbounds i8** %23, i64 1
  store i8* %22, i8** %24
  %25 = load i32** %var_2
  %26 = load i32* %25
  %27 = icmp ne i32 %26, 0
  br i1 %27, label %28, label %43

; <label>:28                                      ; preds = %0
  %29 = load i8*** %var_0
  %30 = getelementptr inbounds i8** %29, i64 0
  %31 = load i8** %30
  %32 = bitcast i8* %31 to i8* (i8**, i8*)*
  store i8* (i8**, i8*)* %32, i8* (i8**, i8*)** %func_pointer_2
  %33 = call i8* @malloc(i64 8)
  store i8* %33, i8** %const_0
  %34 = load i8** %const_0
  %35 = bitcast i8* %34 to double*
  store double 1.000000e+00, double* %35
  %36 = load i8* (i8**, i8*)** %func_pointer_2
  %37 = load i8*** %var_0
  %38 = getelementptr inbounds i8** %37, i64 1
  %39 = load i8** %38
  %40 = bitcast i8* %39 to i8**
  %41 = load i8** %const_0
  %42 = call i8* %36(i8** %40, i8* %41)
  store i8* %42, i8** %1
  br label %72

; <label>:43                                      ; preds = %0
  %44 = call i8* @malloc(i64 8)
  store i8* %44, i8** %var_5
  %45 = load i8** %3
  %46 = bitcast i8* %45 to double*
  %47 = load double* %46
  %48 = fsub double %47, 1.000000e+00
  %49 = load i8** %var_5
  %50 = bitcast i8* %49 to double*
  store double %48, double* %50
  %51 = call i8* @malloc(i64 16)
  %52 = bitcast i8* %51 to i8**
  store i8** %52, i8*** %env_var_2
  %53 = load i8** %3
  %54 = load i8*** %env_var_2
  %55 = getelementptr inbounds i8** %54, i64 0
  store i8* %53, i8** %55
  %56 = load i8*** %var_0
  %57 = bitcast i8** %56 to i8*
  %58 = load i8*** %env_var_2
  %59 = getelementptr inbounds i8** %58, i64 1
  store i8* %57, i8** %59
  %60 = call i8* @malloc(i64 16)
  %61 = bitcast i8* %60 to i8**
  store i8** %61, i8*** %hoisted_lambda_var_1
  %62 = load i8*** %hoisted_lambda_var_1
  %63 = getelementptr inbounds i8** %62, i64 0
  store i8* bitcast (i8* (i8*, i8*)* @func_1 to i8*), i8** %63
  %64 = load i8*** %env_var_2
  %65 = bitcast i8** %64 to i8*
  %66 = load i8*** %hoisted_lambda_var_1
  %67 = getelementptr inbounds i8** %66, i64 1
  store i8* %65, i8** %67
  %68 = load i8*** %hoisted_lambda_var_1
  %69 = bitcast i8** %68 to i8*
  %70 = load i8** %var_5
  %71 = call i8* @fib(i8* %69, i8* %70)
  store i8* %71, i8** %1
  br label %72

; <label>:72                                      ; preds = %43, %28
  %73 = load i8** %1
  ret i8* %73
}

define i8* @fib_copy(i8* %n) {
  %1 = alloca i8*
  %env_var_3 = alloca i8**
  %hoisted_lambda_var_2 = alloca i8**
  store i8* %n, i8** %1
  %2 = call i8* @malloc(i64 0)
  %3 = bitcast i8* %2 to i8**
  store i8** %3, i8*** %env_var_3
  %4 = call i8* @malloc(i64 16)
  %5 = bitcast i8* %4 to i8**
  store i8** %5, i8*** %hoisted_lambda_var_2
  %6 = load i8*** %hoisted_lambda_var_2
  %7 = getelementptr inbounds i8** %6, i64 0
  store i8* bitcast (i8* (i8*, i8*)* @func_3 to i8*), i8** %7
  %8 = load i8*** %env_var_3
  %9 = bitcast i8** %8 to i8*
  %10 = load i8*** %hoisted_lambda_var_2
  %11 = getelementptr inbounds i8** %10, i64 1
  store i8* %9, i8** %11
  %12 = load i8*** %hoisted_lambda_var_2
  %13 = bitcast i8** %12 to i8*
  %14 = load i8** %1
  %15 = call i8* @fib(i8* %13, i8* %14)
  ret i8* %15
}

define i32 @main(i32 %argc, i8** %argv) {
  %1 = alloca i32
  %2 = alloca i8**
  %n = alloca double*
  store i32 %argc, i32* %1
  store i8** %argv, i8*** %2
  %3 = call i8* @malloc(i64 8)
  %4 = bitcast i8* %3 to double*
  store double* %4, double** %n
  %5 = load double** %n
  store double 6.000000e+00, double* %5
  %6 = load double** %n
  %7 = bitcast double* %6 to i8*
  %8 = call i8* @fib_copy(i8* %7)
  %9 = bitcast i8* %8 to double*
  %10 = load double* %9
  %11 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([4 x i8]* @.str, i32 0, i32 0), double %10)
  ret i32 0
}
