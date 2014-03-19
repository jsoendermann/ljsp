#!/usr/local/bin/python

import subprocess
import glob
import os
from os import mkdir
from os import path
import string
import random
import shutil

### COLORS ###

GREEN = '\033[92m'
RED = '\033[91m'
OFF = '\033[0m'

# TODO get these paths from env vars
RACKET_PATH = "/Applications/Racket v5.3.6/bin/racket"
LLI_PATH = "/Users/json/clang+llvm-3.3-x86_64-apple-darwin12/bin/lli"

LJSP_TARGETS = ["parsed", "letExp", "reducePrimOps", "cps", "cc", "hoist"]

# compile
#print "Compiling compiler..."
#subprocess.call(["scalac"] + glob.glob("./*.scala"))
#print "Done compiling"

def scheme_eval(source):
	return float(subprocess.check_output([RACKET_PATH, "--eval", source]))

def run_compiler(arguments, source):
    return subprocess.check_output(["scala", "-cp", "ljsp/", "ljsp.Ljsp"] + arguments + [source])

def compile_to_file(file_name, target, source):
    subprocess.call(["scala", "-cp", "ljsp/", "ljsp.Ljsp", "--" + target, source, "-o", file_name])

with open("test/test_lib.scm", "r") as test_lib_file:
	test_lib = test_lib_file.read()

def floats_equal(a, b):
    if abs(a-b) < 0.01:
        return True
    return False

for source_file_path in glob.glob("./test/test_cases/*.scm"):
    print "Testing "+os.path.basename(source_file_path)

    with open(source_file_path, "r") as source_file:
        source = source_file.read()
    res = scheme_eval(source)


    
    for target in LJSP_TARGETS:
        target_res = scheme_eval(test_lib + run_compiler(["--" + target], source))

        if floats_equal(res, target_res):
            print GREEN + " " + target + " success" + OFF
        else:
            print RED + " " + target + " failure, " + str(res) + " != " + str(target_res) + OFF
            exit(1)

    
    # make temp dir
    temp_dir_name = "tmp_files_" + ''.join(random.choice(string.ascii_lowercase) for _ in range(5))
    mkdir(temp_dir_name)

    # compile to C
    c_source_file_name = path.join(temp_dir_name, "c_output.c")
    c_exe_file_name = path.join(temp_dir_name, "c_output")
    compile_to_file(c_source_file_name, "c", source)

    subprocess.call(["cc", c_source_file_name, "-o", c_exe_file_name])

    c_output = float(subprocess.check_output([c_exe_file_name]))

    if floats_equal(res, c_output):
        print GREEN + " C success" + OFF
    else:
        print RED + " C failure, " + str(res) + " != " + str(c_output) + OFF
        exit(1)

    # compile to LLVM IR
    llvm_ir_source_file_name = path.join(temp_dir_name, "llvm_ir_output.s")
    compile_to_file(llvm_ir_source_file_name, "llvmIr", source)

    llvm_ir_output = float(subprocess.check_output([LLI_PATH, llvm_ir_source_file_name]))

    if floats_equal(res, llvm_ir_output):
        print GREEN + " LLVM IR success" + OFF
    else:
        print RED + " LLVM IR failure, " + str(res) + " != " + str(llvm_ir_output) + OFF
        exit(1)

    # compile to numbered LLVM IR
    num_llvm_ir_source_file_name = path.join(temp_dir_name, "num_llvm_ir_output.s")
    compile_to_file(num_llvm_ir_source_file_name, "numLlvmIr", source)

    num_llvm_ir_output = float(subprocess.check_output([LLI_PATH, num_llvm_ir_source_file_name]))

    if floats_equal(res, num_llvm_ir_output):
        print GREEN + " numbered LLVM IR success" + OFF
    else:
        print RED + " numbered LLVM IR failure, " + str(res) + " != " + str(num_llvm_ir_output) + OFF
        exit(1)

    shutil.rmtree(temp_dir_name)
