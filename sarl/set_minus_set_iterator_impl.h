#ifndef SARL_SET_MINUS_SET_ITERATOR_IMPL_H
#define SARL_SET_MINUS_SET_ITERATOR_IMPL_H

#include <sarl/set_iterator_impl.h>

struct Sarl_SetMinusSetIterator : Sarl_SetIterator
{
  struct Sarl_SetIterator* first;
  struct Sarl_SetIterator* second;
};


#endif
