#ifndef SARL_SET_ITERATOR_IMPL_H
#define SARL_SET_ITERATOR_IMPL_H

#include "set_iterator_impl.h"

struct Sarl_NaryRelationIndexSetIterator : public Sarl_SetIterator 
{
  Sarl_Index i;
  Sarl_NaryRelation *r;
};

#endif
