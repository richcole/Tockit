extern "C" {

#include <sarl/global.h>
#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/set.h>
  
}

#include <sarl/test.h>
#include "args.cpp"

int main(int num_args, char **args)
{
  Sarl_Relation         *r;
  Sarl_RelationIterator *r_it;

  r = sarl_relation_create();

  sarl_relation_insert(r, 1,2);
  sarl_relation_insert(r, 1,4);
  sarl_relation_insert(r, 1,3);
  sarl_relation_insert(r, 2,3);
  sarl_relation_remove(r, 1,3);

  r_it = sarl_relation_iterator_create(r);
  
  SARL_RELATION_ITERATOR_FOR(r_it) {
    Sarl_Pair p = sarl_relation_iterator_value(r_it);
    SARL_TEST_ASSERT(! (p.dom == 1 && p.rng == 3));
  };

  sarl_relation_decr_ref(r);
  sarl_relation_iterator_decr_ref(r_it);

  return 0;
};
