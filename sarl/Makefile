include config.mak

OBJECTS = \
  set \
  set_iterator \
  relation \
  relation_iterator \
  inverse_relation_iterator \
  plain_relation_iterator \
  domain_set_iterator \
  intersection_set_iterator \
  intent_set_iterator \
  union_set_iterator \
  set_minus_set_iterator \
  join_relation_iterator \
  ref_count 

TESTS = \
  test_set \
  test_set_iterator_meet \
  test_set_iterator_union \
  test_set_iterator_set_minus

PUBLIC_HEADERS = \
  cpp/Set.h \
  cpp/SetIterator.h \
  index.h \
  pair.h \
  set.h \
  set_iterator.h \
  relation.h \
  relation_iterator.h 

JAVA_INC   = \
  /usr/java/j2sdk1.4.0/include \
  /usr/java/j2sdk1.4.0/include/linux

PYTHON_INC = \
  /usr/include/python1.5/

SWIG_INC = \
  /usr/local/include

CLASSES = \
  Set \
  SetIterator \
  Relation \
  RelationIterator

target: python/sarlcmodule.so java/libsarlmodule.so
libs: lib_d/libsarl.so lib_d/libsarl.so

test: target test_base test_python test_java test_cpp

test_base: $(TESTS:%=%.exe) 
	LD_LIBRARY_PATH=`pwd`/lib_d:$$LD_LIBRARY_PATH; \
	for x in $^ ; do echo $$x:; ./$$x || exit -1 ; done


python/sarlcmodule.so: lib_d/libsarl.so lib_d/libsarl++.so \
        $(OBJECTS:%=%.o) python/sarl_wrap.o
	$(CC) $(DEBUG) -shared -o $@ $(OBJECTS:%=%.o) python/sarl_wrap.o \
         -Llib_d -lstdc++ -lsarl++ -lsarl

java/libsarlmodule.so: lib_d/libsarl.so lib_d/libsarl++.so \
        $(OBJECTS:%=%.o) java/sarl_wrap.o libs
	$(CC) $(DEBUG) -shared -o $@ $(OBJECTS:%=%.o) java/sarl_wrap.o \
          -Llib_d -lstdc++ -lsarl++ -lsarl

lib_d/libsarl.so: $(OBJECTS:%=%.o) lib_d
	$(CC) $(DEBUG) -shared -o $@ $(OBJECTS:%=%.o) -lstdc++

lib/libsarl.so: $(OBJECTS:%=%.o) lib
	$(CC) $(OPTIM) -shared -o $@ $(OBJECTS:%=%.o) -lstdc++

lib/libsarl++.so: lib
	cd cpp; make ../$@

lib_d/libsarl++.so: lib_d
	cd cpp; make ../$@

%.o: %.cpp
	$(CC) $(DEBUG) -c -I../ -o $@ $< 

%.o: %.cpp
	$(CC) $(DEBUG) -c -I../ -o $@ $< 

%.exe: %.o
	$(CCC) $(DEBUG) -I../ -o $@ $^ -lstdc++ -Llib_d -lsarl

java/%.class: java/%.java
	cd java; $(JAVAC) ../$<

python/sarl_wrap.o: python/sarl_wrap.c
	$(CCC) -c -I../ -DSARL_NATIVE $(SWIG_INC:%=-I%) $(PYTHON_INC:%=-I%) \
          -o $@ $^

python/sarl_wrap.c: $(PUBLIC_HEADERS) sarl.input
	cd python; swig -I.. -o sarl_wrap.c -c++ -shadow -python sarl.input 

test_python: java/TestSarl.class
	LD_LIBRARY_PATH=`pwd`/lib_d:$$LD_LIBRARY_PATH; \
	cd python; python test_sarl.py

java/sarl_wrap.o: java/sarl_wrap.c
	$(CCC) -c -I../ $(SWIG_INC:%=-I%) $(JAVA_INC:%=-I%) -o $@ $^

java/sarl_wrap.c: $(PUBLIC_HEADERS) sarl.input
	cd java; swig -I.. -o sarl_wrap.c -c++ -shadow -java ../sarl.input

java/TestSarl.class: java/TestSarl.java
	cd java; javac TestSarl.java

test_java: java/TestSarl.class $(CLASSES:%=java/%.class)
	LD_LIBRARY_PATH=.:`pwd`/lib_d:$$LD_LIBRARY_PATH; \
	cd java; java TestSarl

test_cpp: 
	cd cpp; make test

clean: java_clean
	rm -f python/*.pyc python/sarl_wrap.* python/*.so
	rm -f java/*.class java/SetIterator.java java/Set.java java/*.so
	rm -f java/sarl_wrap.*
	rm -f *.o *.so *.exe depend.mak.bak

java_clean:
	rm -f java/sarl_wrap.* 
	rm -f java/*.class java/sarl.java 
	rm -f java/*Iterator.java java/Set*.java java/Relation*.java

depend:
	makedepend -Y../ *.cpp -fdepend.mak 

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
