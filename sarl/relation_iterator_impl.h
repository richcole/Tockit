#ifndef SARL_RELATION_ITERATOR_IMPL_H
#define SARL_RELATION_ITERATOR_IMPL_H

#include <sarl/pair.h>

struct Sarl_RelationIteratorFunctionTable {
  void  (*next_gte)(struct Sarl_RelationIterator *, struct Sarl_Pair);
  void  (*next)(struct Sarl_RelationIterator *);
  struct Sarl_Pair (*val)(struct Sarl_RelationIterator *);
  int   (*at_end)(struct Sarl_RelationIterator *);
  void  (*reset)(struct Sarl_RelationIterator *);
  void  (*decr_ref)(struct Sarl_RelationIterator *);
  struct Sarl_RelationIterator*  (*copy)(struct Sarl_RelationIterator *);
  struct Sarl_RelationIterator*  (*inverse)(struct Sarl_RelationIterator *);
};

struct Sarl_RelationIterator
{
  RefCount m_ref_count;
  Sarl_RelationIteratorFunctionTable *mp_funcs;
};

struct Sarl_PlainRelationIterator : Sarl_RelationIterator
{
  typedef Sarl_Relation::Sarl_RelationImpl::const_iterator Iterator;

  Sarl_Relation* mp_relation;
  Iterator  m_it;
};

struct Sarl_JoinRelationIterator : Sarl_RelationIterator
{
  RefCount m_ref_count;
  Sarl_RelationIteratorFunctionTable *mp_funcs;

  Sarl_RelationIterator* mp_first;
  Sarl_RelationIterator* mp_second;
};

inline void sarl_relation_iterator_init(
  struct Sarl_RelationIterator *it,
  Sarl_RelationIteratorFunctionTable *ap_funcs)
{
  it->mp_funcs = ap_funcs;
  sarl_ref_count_init(&it->m_ref_count);
}

#endif
