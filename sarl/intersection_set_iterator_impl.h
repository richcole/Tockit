#ifndef SARL_INTERSECTION_SET_ITERATOR_IMPL_H
#define SARL_INTERSECTION_SET_ITERATOR_IMPL_H

#include <sarl/set_iterator_impl.h>

struct IntersectionSetIterator : SetIterator
{
  struct SetIterator* first;
  struct SetIterator* second;
};


#endif
