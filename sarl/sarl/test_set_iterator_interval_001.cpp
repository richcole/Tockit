extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/set_iterator.h>
  
}

#include <sarl/test.h>
#include "args.cpp"

int main(int num_args, char **args)
{
  Sarl_SetIterator      *r;
  Sarl_SetIterator      *s;
  Sarl_SetIterator      *t;

  int            i, j;
  int            LEN = 10;

  r = sarl_set_iterator_interval(1, 4);
  s = sarl_set_iterator_interval(4, 6);
  t = sarl_set_iterator_meet(r, s);

  SARL_TEST_ASSERT_EQ(sarl_set_iterator_count(r), 4);
  SARL_TEST_ASSERT_EQ(sarl_set_iterator_count(s), 3);
  SARL_TEST_ASSERT_EQ(sarl_set_iterator_count(t), 1);

  sarl_set_iterator_decr_ref(r);
  sarl_set_iterator_decr_ref(s);
  sarl_set_iterator_decr_ref(t);
};
