#!/bin/bash

LD_LIBRARY_PATH=.:$LD_LIBRARY_PATH
for x in $@ ; do echo $x:; ./$x || exit -1 ; done
