#ifndef SARL_RELATION_ITERATOR_IMPL_H
#define SARL_RELATION_ITERATOR_IMPL_H

#include <sarl/pair.h>

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

struct Sarl_RelationIterator
{
  Sarl_RefCount ref_count;
  Sarl_RelationIteratorFunctionTable *funcs;
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

inline void sarl_relation_iterator_init(
  struct Sarl_RelationIterator *it,
  Sarl_RelationIteratorFunctionTable *ap_funcs)
{
  it->funcs = ap_funcs;
  sarl_ref_count_init(&it->ref_count);
}

#endif
