#ifndef SARL_DOMAIN_SET_ITERATOR_IMPL_H
#define SARL_DOMAIN_SET_ITERATOR_IMPL_H

#include <sarl/set_iterator_impl.h>

struct DomainSetIterator : SetIterator
{
  RelationIterator *rel_it;
};


#endif
