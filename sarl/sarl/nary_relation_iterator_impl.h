#ifndef SARL_NARY_RELATION_ITERATOR_IMPL_H
#define SARL_NARY_RELATION_ITERATOR_IMPL_H

#include "iterator.h"

struct Sarl_NaryRelation;

struct Sarl_NaryRelationIterator : public Sarl_Iterator
{
  Sarl_SetIterator *it;
  Sarl_NaryRelation *r;
};

#endif
