extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/set.h>
  
}

#include <sarl/test.h>
#include "args.cc"

int main(int num_args, char **args)
{
  Sarl_Relation         *r;
  Sarl_Set              *s;
  
  Sarl_RelationIterator *r_it;
  Sarl_SetIterator      *intent;
  Sarl_SetIterator      *extent;

  Sarl_SetIterator      *s_it;

  bool TRACE_OUTPUT     = (find_args(num_args, args, "-trace") != 0);  

  int            i, j;
  int            LEN = 10;

  int            count = 0;

  r = sarl_relation_create();
  s = sarl_set_create();
  
  for(i=1;i<=LEN;i++) {
    for(j=1;j<=LEN;j++) {
      if ( i % j == 0 ) {
        sarl_relation_insert(r, i, j);
      }
    }
  }

  sarl_set_insert(s, 6);
  sarl_set_insert(s, 8);
  
  r_it = sarl_relation_iterator_create(r);

  s_it   = sarl_set_iterator_create(s);
  intent = sarl_relation_iterator_intent_set(r_it, s_it);

  SARL_TEST_ASSERT(intent != 0);
  SARL_TEST_ASSERT_EQ(sarl_set_iterator_count(intent), 2);

  count=0;
  SARL_SET_ITERATOR_FOR(intent) {
    ++count;
  }

  SARL_TEST_ASSERT_EQ(count, 2);

  sarl_set_iterator_decr_ref(intent);
  sarl_set_iterator_decr_ref(s_it);

  sarl_set_remove(s, 6);
  sarl_set_remove(s, 8);
  sarl_set_insert(s, 1);
  sarl_set_insert(s, 2);

  s_it   = sarl_set_iterator_create(s);
  extent = sarl_relation_iterator_extent_set(r_it, s_it);
  
  SARL_TEST_ASSERT(extent != 0);
  SARL_TEST_ASSERT_EQ(sarl_set_iterator_count(extent), 5);

  sarl_set_iterator_decr_ref(extent);
  sarl_set_iterator_decr_ref(s_it);

  sarl_set_decr_ref(s);
  sarl_relation_decr_ref(r);
  sarl_relation_iterator_decr_ref(r_it);
};
