ljsp
====

To Compile:

    scalac -deprecation ./*.scala  

To print usage:

    scala -cp ljsp/ ljsp.Ljsp   

To generate ray tracer asm.js module:

    scala -cp ljsp/ ljsp.Ljsp --asmjs -i ray_tracer/ljsp_code.scm -o ray_tracer/gen_code.js

To generate ray tracer emscripten asm.js module:

    scala -cp ljsp/ ljsp.Ljsp --emC -i ray_tracer/ljsp_code.scm -o rtc.c
    emcc -O2 -s EXPORTED_FUNCTIONS="['_raySphereIntersectionPoint_copy_call_by_value', '_vectorsDotProduct_copy_call_by_value']" -s NO_EXIT_RUNTIME=1 rtc.c -o ray_tracer/emcc_output.js

To run tests:

    ./run_tests.py