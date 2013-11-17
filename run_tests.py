#!/usr/local/bin/python

import subprocess
import glob
import os

### COLORS ###

GREEN = '\033[92m'
RED = '\033[91m'
OFF = '\033[0m'

RACKET_PATH = "/Applications/Racket v5.3.6/bin/racket"


def scheme_eval(source):
	return call_prog([RACKET_PATH, "--eval", source])

# call a program and return its output
def call_prog(prog_and_args):
	proc = subprocess.Popen(prog_and_args, stdout=subprocess.PIPE)
	(output, err) = proc.communicate()
	return output.strip()


test_lib = ""
with open("test_lib.scm", "r") as test_lib_file:
	test_lib = test_lib_file.read()

for source_file_path in glob.glob("./tests/*.ljsp"):
	with open(source_file_path, "r") as source_file:
		print "Testing "+os.path.basename(source_file_path)
		source = source_file.read()
		print "   Compiling to CPS..."
		cps = call_prog(["scala", "ljsp_compiler.scala", "--cps", source])
		print "   Compiling to CC..."
		cc = call_prog(["scala", "ljsp_compiler.scala", "--cc", source])
		print "   Comipling to H..."
		h = call_prog(["scala", "ljsp_compiler.scala", "--h", source])
		# TODO this shouldn't succeed when both evaluations fail with the same error
		print "   Evaluating source..."
		res_source = scheme_eval(source)
		print "   Evaluating CPS..."
		res_cps = scheme_eval(cps)
		print "   Evaluating CC..."
		res_cc = scheme_eval(test_lib + cc)
		print "   Evaluating H..."
		res_h = scheme_eval(test_lib + h)
		if res_source == res_cps:
			print GREEN + "   CPS Success" + OFF
		else:
			print RED + "   CPS Fail!" + OFF

		if res_source == res_cc:
			print GREEN + "   CC Success" + OFF
		else:
			print RED + "   CC Fail!" + OFF

		if res_source == res_h:
			print GREEN + "   H Success" + OFF
		else:
			print RED + "   H Fail!" + OFF

		print ""


