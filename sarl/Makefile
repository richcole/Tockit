CCC = g++

OBJECTS = set set_iterator

target: sarlmodule.so 
test: test_set.exe

sarlmodule.so: $(OBJECTS:%=%.o) sarl_wrap.o 
	$(CC) -shared -o $@ $^ -lstdc++

%.o: %.cpp
	$(CC) -c -I../ -o $@ $<

%.exe: %.o $(OBJECTS:%=%.o)
	$(CCC) -I../ -o $@ $^

sarl_wrap.o: sarl_wrap.c
	$(CC) -c -I../ -I/usr/include/python1.5/ -o $@ $^

sarl_wrap.c: index.h set.h set_iterator.h sarl.input
	swig -python sarl.input

clean: 
	rm -f sarl_wrap.* *.o *.so *.pyc depend.mak.bak

depend:
	makedepend -Y../ *.cpp -fdepend.mak 

include depend.mak
