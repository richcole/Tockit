#ifndef SARL_DOMAIN_SET_ITERATOR_IMPL_H
#define SARL_DOMAIN_SET_ITERATOR_IMPL_H

extern "C" {
  #include <sarl/relation_iterator.h>
}

#include <sarl/set_iterator_impl.h>

struct Sarl_DomainSetIterator : Sarl_SetIterator
{
  Sarl_RelationIterator *rel_it;
};


#endif
