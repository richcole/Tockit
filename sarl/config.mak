CCC   = g++
JAVAC = javac
DEBUG = -ggdb 

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
  /usr/bin/python2.2

# Swig

SWIG_INC   = \
  /usr/local/include

SWIG       = \
  /usr/local/bin/swig
