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

#
# Sources and headers not to be installed
#
libsarlplus_la_SOURCES = \
	AdditiveLayout.h \
	ConceptIterator.h \
	Config.h \
	Context.cpp \
	Context.h \
	ContextIterator.cpp \
	ContextIterator.h \
	ContextReader.h \
	depend.mak \
	Dictionary.h \
	Index.h \
	InputFileStream.h \
	InputStream.h \
	Lattice.cpp \
	LatticeIterator.cpp \
	Map.h \
	OutputStream.h \
	Pair.h \
	RefCount.cpp \
	RefCount.h \
	Relation.cpp \
	RelationIterator.cpp \
	Set.cpp \
	SetIterator.cpp \
	String.h

libsarlplus_la_LDFLAGS = @VERSION_INFO_PLUSPLUS@

