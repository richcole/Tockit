#ifndef SARL_NARY_RELATION_ITERATOR_IMPL_H
#define SARL_NARY_RELATION_ITERATOR_IMPL_H

#include "iterator.h"
#include <vector>

struct Sarl_NaryRelation;

struct Sarl_NaryRelationIterator : public Sarl_Iterator
{
  std::vector<SetIterator *> domains;
  Sarl_NaryRelation *r;
};

#endif
