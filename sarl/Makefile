CCC   = g++
DEBUG = -ggdb 

OBJECTS = \
  set \
  set_iterator \
  relation \
  relation_iterator \
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
  index.h \
  pair.h \
  set.h \
  set_iterator.h \
  relation.h \
  relation_iterator.h 

target: python/sarlmodule.so 

test: $(TESTS:%=%.exe) 
	bash execute_tests.sh $^

python/sarlmodule.so: $(OBJECTS:%=%.o) python/sarl_wrap.o 
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
	$(CC) -c -I../ -I/usr/include/python1.5/ -o $@ $^

python/sarl_wrap.c: $(PUBLIC_HEADERS) python/sarl.input
	swig -o $@ -python python/sarl.input 

clean: 
	rm -f python/*.pyc python/sarl_wrap.* python/*.so
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
