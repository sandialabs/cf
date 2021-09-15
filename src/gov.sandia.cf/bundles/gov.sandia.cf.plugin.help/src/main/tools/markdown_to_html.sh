#!/bin/bash

sudo apt install -y pandoc

pandoc -f markdown_strict -t html -i functional_spec.txt -o functional_spec.html
