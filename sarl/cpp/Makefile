CCC = g++

CPP_FLAGS = -DSARL_NATIVE
OBJECTS = Set SetIterator Relation RelationIterator RefCount
TEST = test_intersection test_relation test_relation_join

SARL_LIB_DIR   = ../lib
SARL_LIB_DIR_D = ../lib_d

export LD_LIBRARY_PATH += $(SARL_LIB_DIR_D)

target: $(SARL_LIB_DIR_D)/libsarl++.so $(SARL_LIB_DIR)/libsarl++.so

.PHONY: clean test $(TEST)
$(TEST):
	./$@.exe

%.o: %.cpp
	$(CCC) -ggdb $(CPP_FLAGS) $(DEBUG) -c -I../.. -o $@ $< 

%.exe: %.o 
	$(CCC) -ggdb -o $@ $^ -L$(SARL_LIB_DIR_D) -lsarl++ -lsarl

$(SARL_LIB_DIR_D)/libsarl++.so: $(OBJECTS:%=%.o)
	$(CC) $(DEBUG) -shared -o $@ $^

$(SARL_LIB_DIR)/libsarl++.so: $(OBJECTS:%=%.o)
	$(CC) $(OPTIM) -shared -o $@ $^

clean: 
	rm -f *.o *.so *.exe depend.mak.bak

depend:
	makedepend -Y../.. *.cpp -fdepend.mak 

include depend.mak