#ifndef SARL_RELATION_ITERATOR_IMPL_H
#define SARL_RELATION_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>

struct Sarl_RelationIteratorFunctionTable {
  void  (*next_gte)(struct Sarl_RelationIterator *, struct Sarl_Pair);
  void  (*next)(struct Sarl_RelationIterator *);
  struct Sarl_Pair (*value)(struct Sarl_RelationIterator *);
  int   (*at_end)(struct Sarl_RelationIterator *);
  void  (*reset)(struct Sarl_RelationIterator *);
  void  (*decr_ref)(struct Sarl_RelationIterator *);
  struct Sarl_RelationIterator*  (*copy)(struct Sarl_RelationIterator *);
  struct Sarl_RelationIterator*  (*inverse)(struct Sarl_RelationIterator *);
};

struct Sarl_RelationIterator : Sarl_Iterator
{
  Sarl_RelationIteratorFunctionTable *mp_funcs;
};

struct Sarl_PlainRelationIterator : Sarl_RelationIterator
{
  typedef Sarl_Relation::Sarl_RelationImpl::const_iterator Iterator;

  Sarl_Relation* relation;
  Iterator  it;
};

struct Sarl_JoinRelationIterator : Sarl_RelationIterator
{
  Sarl_RelationIterator* first;
  Sarl_RelationIterator* second;
};

struct Sarl_ContextComplementRelationIterator : Sarl_RelationIterator
{
  Sarl_SetIterator*      G;
  Sarl_SetIterator*      M;
  Sarl_RelationIterator* I;

	Sarl_SetIterator*      intent;
};

inline void sarl_relation_iterator_init(
  struct Sarl_RelationIterator *it,
  Sarl_RelationIteratorFunctionTable *ap_funcs)
{
  it->mp_funcs = ap_funcs;
  it->m_ownership = SARL_HAS_OWNER;
  sarl_ref_count_init(&it->m_ref_count);
}

#endif
