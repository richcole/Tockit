SUBDIRS = 
EXTRA_DIST = 		

#
# A list of all the files in the current directory which can be regenerated
#
MAINTAINERCLEANFILES = Makefile.in

INCLUDES =                                              \
	-I$(srcdir) -I$(top_srcdir) 			\
	-I$(top_builddir)				\
        -I$(includedir)                                 \
	-I- -I../..                                     \
	@CXXFLAGS@                                        


lib_LTLIBRARIES      = libsarlplus.la 

#
# Where to install header files
#
sarlplusincludedir=$(includedir)/sarl/cpp

#
# Headers that are installed
#
sarlplusinclude_HEADERS = \
	Set.h \
	SetIterator.h \
	Relation.h \
	RelationIterator.h \
	Lattice.h \
	LatticeIterator.h 
	AdditiveLayout.h \
	ConceptIterator.h \
	Config.h \
	Context.h \
	Dictionary.h \
	Index.h \
	InputFileStream.h \
	InputStream.h \
	Map.h \
	OutputStream.h \
	Pair.h \
	ContextReader.h \
	ContextIterator.h \
	RefCount.h \
	String.h
#
# Sources and headers not to be installed
#
libsarlplus_la_SOURCES = \
	Context.cpp \
	ContextIterator.cpp \
	Lattice.cpp \
	LatticeIterator.cpp \
	RefCount.cpp \
	Relation.cpp \
	RelationIterator.cpp \
	Set.cpp \
	SetIterator.cpp 


libsarlplus_la_LDFLAGS = @VERSION_INFO_PLUSPLUS@

