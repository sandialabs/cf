#!/usr/bin/python

import argparse
import sys
import os


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Print the ARG backend and report types into the console',
                                     formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument('-a', '--argfile',
                        type=str,
                        required=True,
                        help="query_ARG_types.py -a <argfile>")

    ARG_EXECUTABLE = parser.parse_args().argfile

    if ARG_EXECUTABLE != '':
        ARG_HOME = os.path.dirname(os.path.dirname(os.path.dirname(ARG_EXECUTABLE)))
        ARG_MODULE = [os.path.basename(os.path.dirname(os.path.dirname(ARG_EXECUTABLE))),
                      os.path.basename(os.path.dirname(ARG_EXECUTABLE))]
        ARG_SCRIPT = os.path.splitext(os.path.basename(ARG_EXECUTABLE))[0]
        ARG_IMPORT = '.'.join(ARG_MODULE)

        imported = getattr(__import__(ARG_IMPORT, fromlist=[ARG_SCRIPT]), ARG_SCRIPT)
        BACKEND_TYPES = list(imported.Types.get("BackendTypes").keys())
        REPORT_TYPES = list(imported.Types.get("ReportTypes").keys())
        DICO_TYPES = {"BackendTypes": BACKEND_TYPES, "ReportTypes": REPORT_TYPES}

        print("ARG_TYPES=" + str(DICO_TYPES))
    else:
        print('query_ARG_types.py -a <argfile>')
