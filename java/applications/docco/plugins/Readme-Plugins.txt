
This file contains notes for plugin documentation. Once proper 
documentation is finished - this file should be removed.


All plugins are expected to implement org.tockit.plugins.Plugin interface.
Plugins should be placed into 'plugins' subdirectory of 
a working directory (At least that is what where we 
expect to find Docco plugins). 

A plugin should have following directory structure:
- source directory - containing source files.
- libs directory - containing all needed libs.
- plugin.txt file - optional. See description above for more details. 


Plugin interface:
- includes one method load(). This method is expected to perform 
all startup actions needed and is also responsible for registering
plugin implementation with a corresponding registry. For instance,
DocumentHandler plugin should register itself with DocumentHandlerRegistry.


PluginLoader:
- will scan plugins directory (specified in constructor parameters) for 
any subdirectories and will assume that each found subdirectory corresponds
to a plugin.
- once plugin directories are identified, PluginLoader will check for
file plugin.txt containing a list of fully qualified plugin class names.
If class names are found - these classes will be loaded. Otherwise,
PluginLoader will check the entire plugin directory for classes 
implementing Plugin interface and load all such classes. The former
approach using plugin.txt file is more efficient. ( plugin.txt file should 
contain class name per line.)
- after all plugin classes found for the current plugin - PluginLoader
will instantiate these using PluginClassLoader and call method load() on
each plugin implementation.


PluginClassLoader:
- capable of loading plugin classes from specified directory.
- in case of duplicate class definitions - first class found is taken.
At the moment we don't have any control over order of loading, so it is 
advisable to have one version of each class, otherwise there could be
some unpredictable behaviour in a plugin. If PluginClassLoader found a 
definition of class already defined in the classpath - this class will be 
loaded from the classpath.


Ant build files:
- docco plugins directory contains build.xml and buildPlugin.xml scripts.
- build.xml should be called to compile and build a distribution of all
plugins found in this dir. (Assuming each subdirectory is containing 
a plugin).
- buildPlugin.xml can be called from build.xml to compile and build 
distribution of an individual plugin. This script can be also used 
standalone - in this case we recommend to copy it into your plugin 
directory and change properties corresponding to your plugin specifics.


What distribution should look like.
We expect a plugin directory containing:
- libs
- plugin.jar
- plugin.txt


How to write a plugin:
- create a plugin dir structure as described above
- implement Plugin interface as described above
- figure out distribution mechanism (probably using our ant script),
create distribution directory in the form described above. 
- copy plugin directory into plugins dir in the program dist. 


