#!/usr/bin/python

import argparse
import sys
import os
import os.path
import codecs

def read(rel_path):
    here = os.path.abspath(os.path.dirname(__file__))
    with codecs.open(os.path.join(here, rel_path), 'r') as fp:
        return fp.read()
        
def get_version(rel_path):
    for line in read(rel_path).splitlines():
        if line.startswith('__version__'):
            delim = '"' if '"' in line else "'"
            return line.split(delim)[1]
    else:
        raise RuntimeError("Unable to find version string.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Print the ARG version into the console',
                                     formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument('-a', '--argfile',
                        type=str,
                        required=True,
                        help="query_ARG_version.py -a <argfile>")

    ARG_EXECUTABLE = parser.parse_args().argfile

    if ARG_EXECUTABLE != '':
        ARG_PATH = os.path.dirname(os.path.dirname(ARG_EXECUTABLE))

        version=get_version(ARG_PATH + "/__version__.py")
        
        print("ARG_VERSION=" + version)
    else:
        print('query_ARG_version.py -a <argfile>')
