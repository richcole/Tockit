extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/set_iterator.h>
  
}

#include <sarl/test.h>
#include "args.cc"

int main(int num_args, char **args)
{
  Sarl_Relation         *r;
  Sarl_RelationIterator *it;
  Sarl_SetIterator      *intent;
  Sarl_SetIterator      *intent_copy;

  bool TRACE_OUTPUT     = (find_args(num_args, args, "-trace") != 0);  

  int            i, j;
  int            LEN = 10;

  r = sarl_relation_create();
  
  for(i=1;i<=LEN;i++) {
    for(j=1;j<=LEN;j++) {
      if ( i % j == 0 ) {
        sarl_relation_insert(r, i, j);
      }
    }
  }

  it = sarl_relation_iterator_create(r);
  intent = sarl_relation_iterator_intent(it, 6);

  sarl_relation_iterator_decr_ref(it);
  sarl_relation_decr_ref(r);

  SARL_SET_ITERATOR_FOR(intent) {
    if (TRACE_OUTPUT) {
      fprintf(stdout, "intent.value= %d\n", sarl_set_iterator_val(intent));
    }
  };
  
  intent_copy = sarl_set_iterator_copy(intent);

  SARL_SET_ITERATOR_FOR(intent_copy) {
    if (TRACE_OUTPUT) {
      fprintf(stdout, "intent_copy.value= %d\n", 
        sarl_set_iterator_val(intent_copy));
    }
  };
  
  sarl_set_iterator_reset(intent);
    
  if (TRACE_OUTPUT) {
    fprintf(stdout, "intent.count=%d\n", 
      sarl_set_iterator_count_remaining(intent));
  };

  SARL_TEST_ASSERT(sarl_set_iterator_count_remaining(intent) == 4);

  sarl_set_iterator_decr_ref(intent);
  sarl_set_iterator_decr_ref(intent_copy);
};
