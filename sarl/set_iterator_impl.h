#ifndef SARL_SET_ITERATOR_IMPL_H
#define SARL_SET_ITERATOR_IMPL_H

#include <set>

struct Sarl_SetIteratorFunctionTable {
  void  (*next_gte)(struct Sarl_SetIterator *, Sarl_Index);
  void  (*next)(struct Sarl_SetIterator *);
  Sarl_Index (*val)(struct Sarl_SetIterator *);
  int   (*at_end)(struct Sarl_SetIterator *);
  void  (*reset)(struct Sarl_SetIterator *);
  void  (*decr_ref)(struct Sarl_SetIterator *);
  struct Sarl_SetIterator*  (*copy)(struct Sarl_SetIterator *);
};

struct Sarl_SetIterator
{
  RefCount m_ref_count;
  Sarl_SetIteratorFunctionTable *mp_funcs;
};

struct Sarl_PlainSetIterator : Sarl_SetIterator
{
  typedef std::set<Sarl_Index>::const_iterator Iterator;

  Sarl_Set*     mp_set;
  Iterator m_it;
};

inline void sarl_set_iterator_init(
  struct Sarl_SetIterator *it,
  Sarl_SetIteratorFunctionTable *ap_funcs)
{
  it->mp_funcs = ap_funcs;
  sarl_ref_count_init(&it->m_ref_count);
}

#endif
