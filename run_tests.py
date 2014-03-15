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

RACKET_PATH = "/Applications/Racket v5.3.6/bin/racket"

LJSP_TARGETS = ["parsed", "letExp", "negsRemoved", "cps", "cc", "hoist"]

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


for source_file_path in glob.glob("./test/test_cases/*.scm"):
    print "Testing "+os.path.basename(source_file_path)

    with open(source_file_path, "r") as source_file:
        source = source_file.read()
    res = scheme_eval(source)


    
    for target in LJSP_TARGETS:
        target_res = scheme_eval(test_lib + run_compiler(["--" + target], source))

        if res == target_res:
            print GREEN + " " + target + " success" + OFF
        else:
            print RED + " " + target + " failure, " + res + " != " + target_res + OFF
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

    if res == c_output:
        print GREEN + " C success" + OFF
    else:
        print RED + " C failure, " + res + " != " + c_output + OFF
        exit(1)
    
    shutil.rmtree(temp_dir_name)
