#ifndef SARL_UNION_SET_ITERATOR_IMPL_H
#define SARL_UNION_SET_ITERATOR_IMPL_H

#include <sarl/set_iterator_impl.h>

struct Sarl_UnionSetIterator : Sarl_SetIterator
{
  struct Sarl_SetIterator* first;
  struct Sarl_SetIterator* second;
};


#endif
