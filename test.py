#!/usr/local/bin/python

import subprocess
import glob
import os

RACKET_PATH = "/Applications/Racket v5.3.6/bin/racket"

def scheme_eval(source):
	racket = subprocess.Popen([RACKET_PATH, "--eval", source], stdout=subprocess.PIPE)
	(output, err) = racket.communicate()
	return output.strip()

for source_file_path in glob.glob("./tests/*ljsp"):
	with open(source_file_path, "r") as source_file:
		source = source_file.read()
		print scheme_eval(source)

