SUBDIRS = 
EXTRA_DIST = 		

INCLUDES =                                              \
	-I$(srcdir) -I$(top_srcdir) 			\
	-I$(top_builddir)				\
        -I$(includedir)                                 

#
# A list of all the files in the current directory which can be regenerated
#
MAINTAINERCLEANFILES = Makefile.in

all: _sarl.so

# Make sure these locate your Python installation
PYTHON_INCLUDE= -DHAVE_CONFIG_H @PYINCLUDE@
PYTHON_LIB    = @PYLIB@

# Extra Python specific dynamic linking options
PYTHON_DLNK   = @PYTHONDYNAMICLINKING@

# -----------------------------------------------------------------
# Build a C++ dynamically loadable module
# -----------------------------------------------------------------

_sarl.so: $(SRCS)
	$(SWIG) $(INCLUDES) -c++ -python -shadow -o pysarl.cpp ../sarl.input 
	$(CXX) -o pysarl.o -c $(CCSHARED) $(CFLAGS) $(INCLUDES) \
          $(PYTHON_INCLUDE) pysarl.cpp
	$(CXX) -shared pysarl.o -o _sarl.so






