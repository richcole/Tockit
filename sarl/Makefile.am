AUTOMAKE_OPTIONS = dist-bzip2 subdir-objects #dejagnu

SUBDIRS = sarl testsuite

bin_SCRIPTS = sarl-config sarl++-config

EXTRA_DIST = 		\
  sarl.spec.in 		\
  sarl.spec             \
  sarl.pc               \
  sarl.pc.in            \
  sarl++.pc             \
  sarl++.pc.in          \
  sarl-config           \
  sarl-config.in        \
  sarl++-config         \
  sarl++-config.in      \
  sarl.input

pkgconfigdir = $(libdir)/pkgconfig
pkgconfig_DATA = sarl.pc sarl++.pc

#
# A list of all the files in the current directory which can be regenerated
#
MAINTAINERCLEANFILES = Makefile.in

RUNTESTDEFAULTFLAGS = \
	--tool sarl EXBASE=`pwd`/ SDATA=`pwd`/testsuite/sampledata --srcdir $$srcdir/testsuite


