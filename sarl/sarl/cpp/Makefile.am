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
	LatticeIterator.h \
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
        MapIterator.h \
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
	MapIterator.cpp \
	Map.cpp \
	SetIterator.cpp 


libsarlplus_la_LDFLAGS = @VERSION_INFO_PLUSPLUS@

#
# Testing
#
check_PROGRAMS = \
  test_additive_layout \
  test_assignment \
  test_context \
  test_context_reader \
  test_dictionary \
  test_input_stream \
  test_intersection \
  test_lattice \
  test_map \
  test_relation \
  test_relation_join \
  test_stl \
  test_string \
  test_attribute_factor

TESTS = \
  test_additive_layout \
  test_assignment \
  test_context \
  test_context_reader \
  test_dictionary \
  test_input_stream \
  test_intersection \
  test_lattice \
  test_map \
  test_relation \
  test_relation_join \
  test_stl \
  test_string \
  test_attribute_factor

##

test_attribute_factor_SOURCES = test_attribute_factor.cpp
test_attribute_factor_LDADD = ../libsarl.la libsarlplus.la

test_string_SOURCES = test_string.cpp
test_string_LDADD = ../libsarl.la libsarlplus.la

test_stl_SOURCES = test_stl.cpp
test_stl_LDADD = ../libsarl.la libsarlplus.la

test_relation_join_SOURCES = test_relation_join.cpp
test_relation_join_LDADD = ../libsarl.la libsarlplus.la

test_relation_SOURCES = test_relation.cpp
test_relation_LDADD = ../libsarl.la libsarlplus.la

test_map_SOURCES = test_map.cpp
test_map_LDADD = ../libsarl.la libsarlplus.la

test_lattice_SOURCES = test_lattice.cpp
test_lattice_LDADD = ../libsarl.la libsarlplus.la

test_intersection_SOURCES = test_intersection.cpp
test_intersection_LDADD = ../libsarl.la libsarlplus.la

test_input_stream_SOURCES = test_input_stream.cpp
test_input_stream_LDADD = ../libsarl.la libsarlplus.la

test_dictionary_SOURCES = test_dictionary.cpp
test_dictionary_LDADD = ../libsarl.la libsarlplus.la

test_context_reader_SOURCES = test_context_reader.cpp
test_context_reader_LDADD = ../libsarl.la libsarlplus.la

test_context_SOURCES = test_context.cpp
test_context_LDADD = ../libsarl.la libsarlplus.la

test_assignment_SOURCES = test_assignment.cpp
test_assignment_LDADD = ../libsarl.la libsarlplus.la

test_additive_layout_SOURCES = test_additive_layout.cpp
test_additive_layout_LDADD = ../libsarl.la libsarlplus.la


