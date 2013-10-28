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


for source_file_path in glob.glob("./tests/*.ljsp"):
	with open(source_file_path, "r") as source_file:
		print "Testing "+os.path.basename(source_file_path)
		source = source_file.read()
		print " Compiling to CPS..."
		cps = call_prog(["scala", "ljsp_compiler.scala", "--cps", source])
		# TODO this succeeds if both evaluations fail with the same error
		if scheme_eval(source) == scheme_eval(cps):
			print GREEN + " Success" + OFF
		else:
			print RED + " Fail!" + OFF
		print ""

