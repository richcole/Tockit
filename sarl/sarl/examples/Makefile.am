
EXTRA_DIST = 		

#
# A list of all the files in the current directory which can be regenerated
#
MAINTAINERCLEANFILES = Makefile.in

INCLUDES =                                      \
	-I$(top_srcdir) 			\
	@CXXFLAGS@                                        


#
# Tests
#
noinst_PROGRAMS = \
  proto_concepts   

proto_concepts_SOURCES = proto_concepts.cpp
proto_concepts_LDADD   = ../libsarl.la ../cpp/libsarlplus.la


