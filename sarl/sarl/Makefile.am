#
# This is an example of changing what is to be built by 
# using a variable from the configure.in file
#
# if HAVE_GTK2
# SARLUIDIR = SarlUI appsui
# endif

SUBDIRS = python cpp

EXTRA_DIST = 		

#
# A list of all the files in the current directory which can be regenerated
#
MAINTAINERCLEANFILES = Makefile.in

INCLUDES =                                              \
	-I$(srcdir) -I$(top_srcdir) 			\
	-I$(top_builddir)				\
        -I$(includedir)                                 \
	-I- -I..                                        \
	@CXXFLAGS@                                        


lib_LTLIBRARIES      = libsarl.la 

#
# Where to install header files
#
sarlincludedir=$(includedir)/sarl

#
# Headers that are installed
#
sarlinclude_HEADERS = \
	index.h \
	pair.h \
	set.h \
	set_iterator.h \
	relation.h \
	relation_iterator.h
	aggregate_set_iterator.h \
        assert.h \
        concept_iterator.h \
        concept_lattice_iterator.h \
        config.h \
        context_complement_relation_iterator.h \
        context.h \
        context_intent_map.h \
        context_iterator.h \
        context_reduce.h \
        dictionary.h \
        dictionary_indexes_set_iterator.h \
        filter_lattice_iterator.h \
        global.h \
        input_stream.h \
        join_relation_iterator.h \
        lattice.h \
        lattice_iterator.h \
        lectic.h \
        map.h \
        map_iterator.h \
        output_stream.h \
        ownership.h \
        perm.h \
        plain_dictionary.h \
        plain_relation_iterator.h \
        read_cxt_context.h \
        ref_count.h \
        string.h \
        string_iterator.h \
        test.h \
        transitive_relation.h \
        triadic_relation.h \
        triple.h \
        write_cxt_context.h

#
# Sources and headers not to be installed
#
libsarl_la_SOURCES =           \
	$(SARLUIDIR)           \
	args.cpp               \
	assert.cpp             \
	cached_lattice_iterator.cpp \
	concept_iterator.cpp \
	context_complement_relation_iterator.cpp \
	context.cpp \
	context_iterator_arrow_relations.cpp \
	context_iterator.cpp \
	context_sum.cpp \
	dictionary.cpp \
	dictionary_indexes_set_iterator.cpp \
	domain_set_iterator.cpp \
	filter_lattice_iterator.cpp \
	input_stream.cpp \
	intent_set_iterator.cpp \
	intersection_set_iterator.cpp \
	interval_set_iterator.cpp \
	inverse_relation_iterator.cpp \
	join_relation_iterator.cpp \
	lattice.cpp \
	lattice_iterator.cpp \
	lectic.cpp \
	map.cpp \
	map_iterator.cpp \
	output_stream.cpp \
	plain_dictionary.cpp \
	plain_lattice_iterator.cpp \
	plain_relation_iterator.cpp \
	plain_set_iterator.cpp \
	read_cxt_context.cpp \
	ref_count.cpp \
	relation.cpp \
	relation_iterator.cpp \
	sarl_additive_layout.cpp \
	set.cpp \
	set_iterator.cpp \
	set_minus_set_iterator.cpp \
	string.cpp \
	transitive_relation.cpp \
	union_set_iterator.cpp \
	write_cxt_context.cpp \
        concept_iterator_impl.h \
        context_impl.h \
        context_iterator_impl.h \
        dictionary_impl.h \
        domain_set_iterator_impl.h \
        intersection_set_iterator_impl.h \
        iterator_impl.h \
        lattice_impl.h \
        lattice_iterator_impl.h \
        map_impl.h \
        map_iterator_impl.h \
        output_stream_impl.h \
        plain_dictionary_impl.h \
        relation_impl.h \
        relation_iterator_impl.h \
        set_impl.h \
        set_iterator_impl.h \
        set_minus_set_iterator_impl.h \
        string_impl.h \
        transitive_relation_impl.h \
        union_set_iterator_impl.h 

libsarl_la_LDFLAGS = @VERSION_INFO@

