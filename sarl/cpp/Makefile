CCC = g++

CPP_FLAGS = -DSARL_NATIVE

OBJECTS = \
  Set      SetIterator      \
  Relation RelationIterator \
  Context  ContextIterator  \
  Lattice  LatticeIterator  \
  RefCount

TESTS = \
  test_additive_layout \
  test_input_stream \
  test_context_reader \
  test_dictionary \
  test_map \
  test_string \
  test_intersection test_relation \
  test_relation_join test_context \
  test_assignment \
  test_stl

SARL_LIB_DIR   = ../lib
SARL_LIB_DIR_D = ../lib_d

export LD_LIBRARY_PATH += $(SARL_LIB_DIR_D)

.PHONY: clean test dirs

target: $(SARL_LIB_DIR_D)/libsarl++.so $(SARL_LIB_DIR)/libsarl++.so

test: dirs $(TESTS:%=tests/%.exe) $(TESTS:%=%.run)

$(TEST): 
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$(SARL_LIB_DIR_D); echo $@; ./$@.exe

objs/%.o: %.cpp
	$(CCC) -ggdb $(CPP_FLAGS) $(OPTIM) -c -I../.. -o $@ $< 

objs_d/%.o: %.cpp objs
	$(CCC) -ggdb $(CPP_FLAGS) $(DEBUG) -c -I../.. -o $@ $< 

tests/%.exe: objs/%.o $(SARL_LIB_DIR_D)/libsarl++.so
	$(CCC) $(DEBUG) -o $@ $< -L$(SARL_LIB_DIR_D) \
        -Wl,-rpath,../lib_d -lsarl++ -lsarl 

%.run: tests/%.exe 
	./$<

$(SARL_LIB_DIR_D)/libsarl++.so: $(OBJECTS:%=objs/%.o)
	$(CCC) $(DEBUG) -shared -o $@ $^

$(SARL_LIB_DIR)/libsarl++.so: $(OBJECTS:%=objs/%.o)
	$(CCC) $(OPTIM) -shared -o $@ $^

clean: 
	rm -f objs/*.o objs_d/*.o *.so *.exe depend.mak.bak

depend:
	makedepend -Y../ *.cpp -fdepend.mak >& /dev/null

dirs: lib lib_d objs objs_d bin bin_d tests


lib:
	mkdir lib 

lib_d:
	mkdir lib_d

objs:
	mkdir objs

objs_d: 
	mkdir objs_d

bin:
	mkdir bin

bin_d:
	mkdir bin_d

tests:
	mkdir tests

include depend.mak

