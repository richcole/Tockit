AUTOMAKE_OPTIONS = dist-bzip2 # subdir-objects dejagnu

# This is an example of using variable from the configure.in file
#
# if HAVE_GTK2
# SARLUIDIR = SarlUI appsui
# endif

SUBDIRS = 

bin_SCRIPTS = sarl-config sarl++-config

EXTRA_DIST = 		\
  sarl.spec.in 		\
  sarl.spec             


#
# A list of all the files in the current directory which can be regenerated
#
MAINTAINERCLEANFILES = Makefile.in

INCLUDES =                                              \
	-I. 						\
	-I$(srcdir) -I$(top_srcdir) 			\
	-I$(top_builddir)				\
        -I$(includedir)                                 \
	-I$(top_srcdir)/plugins/context/                \
	@CFLAGS@                                        


lib_LTLIBRARIES      = libsarl.la libsarl++.la

#
# Where to install header files
#
sarlincludedir=$(includedir)/sarl

#
# Headers that are installed
#
sarlinclude_HEADERS =          \

#
# Sources and headers not to be installed
#
libsarl_la_SOURCES =           \
	Sarl.cpp               \
	Sarl_private.hh        \
	Native.cpp               \
	ContextContext.cpp       \
	SarlException.cpp      \
	SignalStreams.cpp        \
	SarlHandle.cpp         \
	timber.cpp               \
	Enamel.cpp               \
	Enamel_priv.hh 		 \
	SM.cpp                   \
	SM_private.hh            \
	MatchedEAGenerators.cpp  \
	General.cpp              \
	FilteredContext.cpp      \
	Regex.cpp                \
	SarlContextPlugin.cpp  \
	SarlOpenSSL.cpp        \
	Context.cpp              \
	Context_private.hh       \
	SarlPopt.cpp           \
	ContextPopt.cpp          \
	Runner.cpp               \
	Sarlls.cpp             \
	Attribute.cpp 		 \
	Attribute_private.hh     \
	FilteredContext_private.hh \
	Resolver.cpp 		 \
	Resolver_private.hh 	 \
	Math.cpp		 \
	Sarlls_AggregateData.cpp  \
	Sarlls_AggregateData.hh   \
	ChainedViewContext.cpp   \
	SarlOpenSSL.hh         \
	MainPageDocs.hh          \
	$(XMLCPP)                \
	xfsutil.cpp              \
	xfsutil.hh               \
	EAN.cpp                  \
	SarlBackup.cpp         \
	StatfsUtilities.cpp      \
	StatfsUtilities.hh       \
	SarlCopy.cpp		 \
	SarlCopy_private.hh    \
	SarlRemove.cpp         \
	SarlRemove_private.hh  \
	LinkContextScheme.cpp    \
	LinkContextScheme.hh     \
	LinkContextSchemeClients.cpp \
	LinkContextSchemeClients.hh  \
	SarlMove.cpp               \
	ClipAPI.cpp                  \
	CursorAPI.cpp                \
	Configuration.cpp            \
	Configuration_private.hh     \
	SarlVersioning.cpp         \
	ContextIterator.cpp          \
	ContextSetCompare.cpp        \
	ContextSetCompare_private.hh \
	Shell.cpp                    \
	Mime.cpp                     \
	SarlKDE3.cpp SarlKDE3.hh \
	ChildStreamServer.cpp        \
	AsyncIO.cpp                  \
	Chmod.cpp                    \
	PluginOutOfProcNotificationEngine.hh  \
	PluginOutOfProcNotificationEngine.cpp \
	common-sarl-out-of-proc-notification-deamon.hh  \
	common-sarl-out-of-proc-notification-deamon.cpp \
	Daemon.cpp               \
	Debug.cpp                \
	FullTextIndexer.cpp      \
	FullTextIndexer_private.hh \
	FullTextQuery.cpp        \
	SarlAsTextPlugin.hh    \
	SarlAsTextPlugin.cpp   \
	SarlCreationPlugin.hh  \
	SarlCreationPlugin.cpp \
	FCA.cpp

libsarl_la_LDFLAGS =              	  \
	@GLIB_LIBS@                 	  \
	@CUSTOMALLOC_LA@                  \
	-version-info @LIBSARL_LT_CURRENT@:@LIBSARL_LT_REVISION@:@LIBSARL_LT_AGE@

