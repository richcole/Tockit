CCC = g++

CPP_FLAGS = -DSARL_NATIVE
OBJECTS = Set SetIterator Relation RelationIterator RefCount
TEST = test_intersection test_relation test_relation_join

target: sarl++.so

test: $(TEST:%=%.exe)
	LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.; \
  for x in $(TEST:%=%.exe) ; do ./$$x ; done

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