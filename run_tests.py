#!/usr/local/bin/python

import subprocess
import glob
import os

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
	return subprocess.check_output([RACKET_PATH, "--eval", source])

def run_compiler(arguments, source):
    return subprocess.check_output(["scala", "-cp", "ljsp/", "ljsp.Ljsp"] + arguments + [source])

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
            print RED + " " + target + " failure" + OFF
            exit(1)

    # TODO test C and LLVM IR
