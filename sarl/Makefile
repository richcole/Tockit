include config.mak

OBJECTS = \
  set \
  set_iterator \
  relation \
  relation_iterator \
  inverse_relation_iterator \
  plain_relation_iterator \
  domain_set_iterator \
  plain_set_iterator \
  interval_set_iterator \
  intersection_set_iterator \
  intent_set_iterator \
  union_set_iterator \
  set_minus_set_iterator \
  join_relation_iterator \
  ref_count \
  context \
  context_iterator \
  context_complement_relation_iterator \
  lectic \
  concept_iterator \
  lattice_iterator \
  plain_lattice_iterator \
  filter_lattice_iterator \
  string \
  transitive_relation \
  lattice \
  cached_lattice_iterator \
  assert \
  dictionary \
  plain_dictionary \
  dictionary_indexes_set_iterator \
  input_stream \
  output_stream \
  read_cxt_context \
  write_cxt_context

TESTS = \
  test_set \
  test_set_iterator_meet \
  test_set_iterator_union \
  test_set_iterator_set_minus \
  test_relation_001 \
  test_relation_002 \
  test_relation_003 \
  test_relation_004 \
  test_set_iterator_interval_001 \
  test_lectic_001 \
  test_lectic_002 \
  test_lectic_003 \
  test_lattice_001 \
  test_dictionary_001 \
  test_read_cxt_context_001

PUBLIC_HEADERS = \
  cpp/Set.h \
  cpp/SetIterator.h \
  cpp/Relation.h \
  cpp/RelationIterator.h \
  cpp/Lattice.h \
  cpp/LatticeIterator.h \
  index.h \
  pair.h \
  set.h \
  set_iterator.h \
  relation.h \
  relation_iterator.h 

CLASSES = \
  Set \
  SetIterator \
  Relation \
  RelationIterator

PYTHON_SARL_LIB =  python/_sarl.so
JAVA_SARL_LIB   =  java/libsarl_java.so

export LD_LIBRARY_PATH := $(LD_LIBRARY_PATH):$(SARL_ROOT)/lib_d

test_base: libs $(TESTS:%=%.exe) 
	LD_LIBRARY_PATH=`pwd`/lib_d:$$LD_LIBRARY_PATH; \
	for x in $(TESTS:%=%.exe) ; do echo $$x:; ./$$x || exit -1 ; done

# Main.

main: $(PYTHON_SARL_LIB) $(JAVA_SARL_LIB)

.PHONY: test base_test java_test cpp_test python_test
.PHONY: clean base_clean python_clean java_clean cpp_clean 
.PHONY: $(TESTS)

# Test.

test: libs base_test python_test java_test cpp_test

# Base.

base_test: libs $(TESTS) 

$(TESTS) : $(TESTS:%=%.exe)
	echo LD_LIBRARY_PATH=$$LD_LIBRARY_PATH; echo ./$@.exe; ./$@.exe

# Libs.

libs: lib/libsarl.so lib_d/libsarl.so lib/libsarl++.so lib_d/libsarl++.so

$(PYTHON_SARL_LIB): lib_d/libsarl.so lib_d/libsarl++.so \
        $(OBJECTS:%=objs_d/%.o) python/sarl_wrap.o
	$(CC) $(DEBUG) -shared -o $@ $(OBJECTS:%=objs_d/%.o) python/sarl_wrap.o \
         -Llib_d -lstdc++ -lsarl++ -lsarl

$(JAVA_SARL_LIB): lib_d/libsarl.so lib_d/libsarl++.so \
        $(OBJECTS:%=objs_d/%.o) java/sarl_wrap.o 
	$(CC) $(DEBUG) -shared -o $@ $(OBJECTS:%=objs_d/%.o) java/sarl_wrap.o \
          -Llib_d -lstdc++ -lsarl++ -lsarl

lib_d/libsarl.so: $(OBJECTS:%=objs_d/%.o) lib_d
	$(CC) $(DEBUG) -shared -o $@ $(OBJECTS:%=objs_d/%.o) -lstdc++

lib/libsarl.so: $(OBJECTS:%=objs/%.o) lib
	$(CC) $(OPTIM) -shared -o $@ $(OBJECTS:%=objs/%.o) -lstdc++

lib/libsarl++.so: lib 
	$(MAKE) -C cpp ../$@

lib_d/libsarl++.so: lib_d
	$(MAKE) -C cpp ../$@

# Rules.

objs_d/%.o: %.cpp
	$(CCC) $(DEBUG) -c -I../ -o $@ $< 

objs/%.o: %.cpp
	$(CCC) $(OPTIM) -c -I../ -o $@ $< 

%.exe: objs_d/%.o
	$(CCC) $(DEBUG) -I../ -o $@ $^ -lstdc++ -Llib_d -lsarl

java/%.class: java/%.java
	cd java; $(JAVAC) ../$<

%.gxl: %
	$(CPPX) -g "-c -I../" $<


$(TESTS:%=%.exe) : $(TESTS:%=objs_d/%.o) $(OBJECTS:%=objs_d/%.o)
	$(CCC) $(DEBUG) -I../ -o $@ -lstdc++ $(@:%.exe=objs_d/%.o) $(OBJECTS:%=objs_d/%.o)


# CPPX

base_gxl : headers.cpp.gxl

# Python.

python/sarl_wrap.o: python/sarl_wrap.c
	$(CCC) -c -I../ -DSARL_NATIVE $(SWIG_INC:%=-I%) $(PYTHON_INC:%=-I%) \
          -o $@ $^

python/sarl_wrap.c: $(PUBLIC_HEADERS) sarl.input
	cd python; $(SWIG) -I.. -o sarl_wrap.c -c++ -python sarl.input 

test_python: libs java/TestSarl.class
	LD_LIBRARY_PATH=`pwd`/lib_d:$$LD_LIBRARY_PATH; \

python_test: $(PYTHON_SARL_LIB)
	cd python; $(PYTHON) test_sarl.py

# Java.

java/sarl_wrap.o: java/sarl_wrap.c
	$(CCC) -c -I../ $(SWIG_INC:%=-I%) $(JAVA_INC:%=-I%) -o $@ $^

java/sarl_wrap.c: $(PUBLIC_HEADERS) sarl.input
	cd java; $(SWIG) -I.. -o sarl_wrap.c -c++ -java ../sarl.input

java/TestSarl.class: java/TestSarl.java $(JAVA_SARL_LIB) java/sarl_wrap.c
	cd java; javac TestSarl.java

java_test: java/TestSarl.class $(JAVA_SARL_LIB)
	cd java; java -Djava.library.path=. TestSarl

# C++.

cpp_test: libs
	$(MAKE) -C cpp test

# Clean.

clean: java_clean python_clean cpp_clean lib_clean
	rm -f *.o

cpp_clean:
	$(MAKE) -C cpp clean

python_clean:
	rm -f python/*.pyc python/sarl_wrap.* python/*.so

java_clean:
	rm -f java/sarl_wrap.* 
	rm -f java/*.class java/sarl.java 
	rm -f java/*Iterator.java java/Set*.java java/Relation*.java

lib_clean:
	rm -f lib/*.so lib_d/*.so

# Depend.

depend:
	makedepend -Y../ *.cpp -fdepend.mak >& /dev/null
	sed -e 's/\(^.*\.o:\)/objs\/\1/' depend.mak > depend.mak.tmp
	sed -e 's/\(^.*\.o:\)/objs_d\/\1/' depend.mak >> depend.mak.tmp
	mv depend.mak.tmp depend.mak

# Dirs.

lib:
	mkdir lib 

lib_d:
	mkdir lib_d

obj:
	mkdir obj

obj_d: 
	mkdir obj_d

bin:
	mkdir bin

bin_d:
	mkdir bin_d

include depend.mak
