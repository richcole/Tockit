extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/lectic.h>
  
}

#include <sarl/test.h>
#include "args.cpp"

int main(int num_args, char **args)
{
  Sarl_SetIterator      *G;
  Sarl_SetIterator      *curr, *next;

  int            i, j;
  int            LEN = 10;

  G    = sarl_set_iterator_interval(1, 4);
  curr = sarl_set_iterator_interval(1, 0);  // empty set
  
  for(i=0;i<16;++i) {

    j = 0;
    SARL_SET_ITERATOR_FOR(curr) {
      j += (0x1 << (sarl_set_iterator_value(curr) - 1));
    };
    sarl_set_iterator_reset(curr);

    SARL_TEST_ASSERT_EQ(i, j);

    sarl_set_iterator_release_ownership(curr);
    next = sarl_set_iterator_lectic_next(curr, G);
    sarl_set_iterator_decr_ref(curr);
    curr = next;
  };

  sarl_set_iterator_decr_ref(G);
  sarl_set_iterator_decr_ref(curr);
};
