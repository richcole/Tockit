#ifndef SARL_SET_ITERATOR_IMPL_H
#define SARL_SET_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>

#include <set>

struct Sarl_SetIteratorFunctionTable {
  void  (*next_gte)(struct Sarl_SetIterator *, Sarl_Index);
  void  (*next)(struct Sarl_SetIterator *);
  Sarl_Index (*value)(struct Sarl_SetIterator *);
  int   (*at_end)(struct Sarl_SetIterator *);
  void  (*reset)(struct Sarl_SetIterator *);
  void  (*decr_ref)(struct Sarl_SetIterator *);
  struct Sarl_SetIterator*  (*copy)(struct Sarl_SetIterator *);
};

struct Sarl_SetIterator : Sarl_Iterator
{
  Sarl_SetIteratorFunctionTable* funcs;
};

struct Sarl_PlainSetIterator : Sarl_SetIterator
{
  typedef std::set<Sarl_Index>::const_iterator Iterator;

  Sarl_Set*  set;
  Iterator   it;
};

struct Sarl_IntentSetIterator : Sarl_SetIterator
{
  struct Sarl_RelationIterator *iterator;
  Sarl_Index object;
};

inline void sarl_set_iterator_init(
  struct Sarl_SetIterator *it,
  Sarl_SetIteratorFunctionTable *ap_funcs)
{
  it->funcs = ap_funcs;
  it->ownership = SARL_HAS_OWNER;
  sarl_ref_count_init(&it->ref_count);
}

#endif
