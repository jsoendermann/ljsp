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

FRONT_END_STAGES = ["parsed", "letExp", "reducePrimOps", "cps", "cc", "hoist"]
BACK_END_STAGES = ["c", "llvmIr", "numLlvmIr"]


# Tests two floating point numbers for equality
def floats_equal(a, b):
    if abs(a-b) < 0.01:
        return True
    return False

# This uses Racket to evaluate the code and returns the result
def evaluate_scheme_code(source):
	return float(subprocess.check_output([RACKET_PATH, "--eval", source]))

# This runs the ljsp compiler on the code and returns the result
def run_ljsp_compiler(arguments, source):
    return subprocess.check_output(["scala", "-cp", "ljsp/", "ljsp.Ljsp"] + arguments + [source])

# This runs the ljsp compiler on the code and writes the result to file_name
def compile_ljsp_code_to_file(file_name, target, source):
    subprocess.call(["scala", "-cp", "ljsp/", "ljsp.Ljsp", "--" + target, source, "-o", file_name])

def test_back_end(back_end, source, res):
    source_file_name = path.join(temp_dir_name, back_end + "_output.c")
    output_file_name = path.join(temp_dir_name, back_end + "_output")

    compile_ljsp_code_to_file(source_file_name, back_end, source)

    if back_end == "c":
        subprocess.call(["cc", source_file_name, "-o", output_file_name])
        output = float(subprocess.check_output([output_file_name]))

    if back_end == "llvmIr" or back_end == "numLlvmIr":
        output = float(subprocess.check_output([LLI_PATH, source_file_name]))

    if floats_equal(res, output):
        print GREEN + " " + back_end + " success" + OFF
    else:
        print RED + " " + back_end + " failure, " + str(res) + " != " + str(output) + OFF
        exit(1)

with open("test/test_lib.scm", "r") as test_lib_file:
	test_lib = test_lib_file.read()

for source_file_path in glob.glob("./test/test_cases/*.scm"):
    print "Testing "+os.path.basename(source_file_path)

    # evaluate orginal ljsp code
    with open(source_file_path, "r") as source_file:
        source = source_file.read()
    res = evaluate_scheme_code(source)


    # test front end stages 
    for target in FRONT_END_STAGES:
        target_res = evaluate_scheme_code(test_lib + run_ljsp_compiler(["--" + target], source))

        if floats_equal(res, target_res):
            print GREEN + " " + target + " success" + OFF
        else:
            print RED + " " + target + " failure, " + str(res) + " != " + str(target_res) + OFF
            exit(1)

    
    # make temp dir
    temp_dir_name = "tmp_files_" + ''.join(random.choice(string.ascii_lowercase) for _ in range(5))
    mkdir(temp_dir_name)

    # test back end stages
    for back_end in BACK_END_STAGES:
        test_back_end(back_end, source, res)
    
    # remove temp dir
    shutil.rmtree(temp_dir_name)
