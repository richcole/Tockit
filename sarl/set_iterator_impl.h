#ifndef SARL_SET_ITERATOR_IMPL_H
#define SARL_SET_ITERATOR_IMPL_H

#include <set>

struct SetIteratorFunctionTable {
  void  (*next_gte)(struct SetIterator *, Index);
  void  (*next)(struct SetIterator *);
  Index (*val)(struct SetIterator *);
  int   (*at_end)(struct SetIterator *);
  void  (*reset)(struct SetIterator *);
  void  (*decr_ref)(struct SetIterator *);
};

struct SetIterator
{
  RefCount m_ref_count;
  SetIteratorFunctionTable *mp_funcs;
};

struct PlainSetIterator : SetIterator
{
  typedef std::set<Index>::const_iterator Iterator;

  Set*     mp_set;
  Iterator m_it;
};

#endif
