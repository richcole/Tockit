#!/bin/sh
#
# small script to generate website from XML sources using Saxon
#
# to get it running change baseDir and saxonDir
#

# baseDir must point to the directory with the sources, i.e.
# the directory containing the content and the framework
# subdirectories.
#
# Relative paths are a little dangerous since they depend on
# the place were you start the script. They should work but
# don't complain about problems unless you can solve them :-)
#
baseDir="../..";

# outputDir is the place where the HTML is generated
#
outputDir=$baseDir/output;

# saxonDir must point to a Saxon installation or Saxon
# must be found in the java classpath
#
saxonDir=$SAXONDIR;

# styleSheet sets the style sheet that is used
# to process the files. It is expected in 
# $baseDir/source/stylesheets, ".xsl" is appended.
#
styleSheet="simple"

# files enlist the files that should be processed.
#
# Currently the sources and output directory have to have the
# same structure and all directories in the output have to
# exist. The ending ".xml" is always added.
#
# Later the website.xml file should be used to get this
# information, including different source names (sname)
# and target names (tname).
#
files="index overview/goals overview/status overview/people overview/technology documentation/applications documentation/cvs";

# ============================================================
#         End of configuration part
# ============================================================

# change directories to absolute paths if relative path is used
# both might be relative, so we switch to the starting directory
# in between. Probably there is a command to do this more elegant
# but I don't know it.
#
curDir=`pwd`;
cd $baseDir;
baseDir=`pwd`;
cd $curDir;
cd $outputDir;
outputDir=`pwd`;

# create all directories needed.
#
mkdir -p $outputDir;
mkdir -p $outputDir/overview;
mkdir -p $outputDir/documentation;

# work in the Saxon dir -- this way everthing is found if classpath
# contains "."
#
if ( test -d $saxonDir; ) then
	cd $saxonDir;
fi;

# give config info
echo Working in `pwd`;
echo Base input directory is $baseDir;
echo Output directory is $outputDir;

# process files
#
for name in $files; 
	do 
		echo "Processing $name";
		java com.icl.saxon.StyleSheet \
			$baseDir/content/$name.xml \
			$baseDir/framework/stylesheets/$styleSheet.xsl \
				> $outputDir/$name.html; 
	done
