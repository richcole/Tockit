CCC   = g++
CC    = gcc
JAVAC = javac

# DEBUG = -O3 -pg -ggdb
# DEBUG = -march=i686 -O3 -pg -ggdb # -ggdb
# DEBUG = -ggdb 

DEBUG = -ggdb -Wall
OPTIM = -O3 -march=i686

# Sarl Root

SARL_ROOT  := $(shell pwd)

# Java.

JAVA_INC   = \
  /usr/java/j2sdk1.4.0/include \
  /usr/java/j2sdk1.4.0/include/linux

# Python

PYTHON_INC = \
  /usr/include/python2.2/

PYTHON     = \
  /usr/bin/python

# Swig

SWIG_INC   = \
  /usr/local/include

SWIG       = \
  /usr/local/bin/swig 

# CPPX

CPPX       = \
  /usr/local/cppx/bin/cppx







