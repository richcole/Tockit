CCC = g++

CPP_FLAGS = -DSARL_NATIVE
OBJECTS = Set SetIterator Relation RelationIterator

target: sarl++.so

test: test_intersection.exe test_relation.exe

%.o: %.cpp
	$(CCC) -ggdb $(CPP_FLAGS) $(DEBUG) -c -I../.. -o $@ $< 

%.exe: %.o sarl++.so ../sarl.so 
	$(CCC) -ggdb -o $@ $^

sarl++.so: $(OBJECTS:%=%.o)
	$(CC) $(DEBUG) -shared -o $@ $^

clean: 
	rm -f *.o *.so *.exe depend.mak.bak

depend:
	makedepend -Y../.. *.cpp -fdepend.mak 

include depend.mak