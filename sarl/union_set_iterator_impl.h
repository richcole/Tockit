#ifndef SARL_UNION_SET_ITERATOR_IMPL_H
#define SARL_UNION_SET_ITERATOR_IMPL_H

#include <sarl/set_iterator_impl.h>

struct UnionSetIterator : SetIterator
{
  struct SetIterator* first;
  struct SetIterator* second;
};


#endif
