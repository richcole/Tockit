#ifndef SARL_SET_MINUS_SET_ITERATOR_IMPL_H
#define SARL_SET_MINUS_SET_ITERATOR_IMPL_H

#include <sarl/set_iterator_impl.h>

struct SetMinusSetIterator : SetIterator
{
  struct SetIterator* first;
  struct SetIterator* second;
};


#endif
