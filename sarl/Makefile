CCC   = g++
DEBUG = -ggdb 

OBJECTS = \
  set \
  set_iterator \
  relation \
  relation_iterator \
  domain_set_iterator \
  intersection_set_iterator \
  union_set_iterator \
  set_minus_set_iterator \
  ref_count

TESTS = \
  test_set \
  test_set_iterator_meet \
  test_set_iterator_union \
  test_set_iterator_set_minus

PUBLIC_HEADERS = \
  cpp/Set.h \
  cpp/SetIterator.h \
  cpp/Namespace.h \
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

target: python/sarlcmodule.so java/libsarl.so

test: test_base test_python test_java

test_base: $(TESTS:%=%.exe) 
	bash execute_tests.sh $^

python/sarlcmodule.so: $(OBJECTS:%=%.o) python/sarl_wrap.o 
	$(CC) $(DEBUG) -shared -o $@ $^ -lstdc++

java/libsarl.so: $(OBJECTS:%=%.o) java/sarl_wrap.o 
	$(CC) $(DEBUG) -shared -o $@ $^ -lstdc++

sarl.so: $(OBJECTS:%=%.o) 
	$(CC) $(DEBUG) -shared -o $@ $^ -lstdc++

%.o: %.cpp
	$(CC) $(DEBUG) -c -I../ -o $@ $< 

%.o: %.cpp
	$(CC) $(DEBUG) -c -I../ -o $@ $< 

%.exe: %.o sarl.so
	$(CCC) $(DEBUG) -I../ -o $@ $^ -lstdc++ 

python/sarl_wrap.o: python/sarl_wrap.c
	$(CCC) -c -I../ -DSARL_NATIVE $(SWIG_INC:%=-I%) $(PYTHON_INC:%=-I%) \
          -o $@ $^

python/sarl_wrap.c: $(PUBLIC_HEADERS) sarl.input
	cd python; swig -I.. -o sarl_wrap.c -c++ -shadow -python sarl.input 

java/sarl_wrap.o: java/sarl_wrap.c
	$(CCC) -c -I../ $(SWIG_INC:%=-I%) $(JAVA_INC:%=-I%) -o $@ $^

java/sarl_wrap.c: $(PUBLIC_HEADERS) sarl.input
	cd java; swig -I.. -o sarl_wrap.c -c++ -shadow -java ../sarl.input

java/TestSarl.class: java/TestSarl.java
	cd java; javac TestSarl.java

test_java: java/TestSarl.class
	cd java; java TestSarl

java/sarl_wrap.c: index.h set.h set_iterator.h sarl.input
	swig -o $@ -java sarl.input
	mv sarl.java java

java/test_sarl.class: java/test_sarl.java
	javac -sourcepath ./java java/test_sarl.java

test_java: java/test_sarl.class
	cd java; java test_sarl

clean: 
	rm -f python/*.pyc python/sarl_wrap.* python/*.so
	rm -f java/*.class java/SetIterator.java java/Set.java java/*.so
	rm -f java/sarl_wrap.*
	rm -f *.o *.so *.exe depend.mak.bak

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
