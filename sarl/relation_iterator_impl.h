#ifndef SARL_RELATION_ITERATOR_IMPL_H
#define SARL_RELATION_ITERATOR_IMPL_H

struct RelationIteratorFunctionTable {
  void  (*next_gte)(struct RelationIterator *, Pair);
  void  (*next)(struct RelationIterator *);
  Pair (*val)(struct RelationIterator *);
  int   (*at_end)(struct RelationIterator *);
  void  (*reset)(struct RelationIterator *);
  void  (*decr_ref)(struct RelationIterator *);
  struct RelationIterator*  (*copy)(struct RelationIterator *);
};

struct RelationIterator
{
  RefCount m_ref_count;
  RelationIteratorFunctionTable *mp_funcs;
};

struct PlainRelationIterator : RelationIterator
{
  typedef Relation::RelationImpl::const_iterator Iterator;

  Relation* mp_relation;
  Iterator  m_it;
};

inline void sarl_relation_iterator_init(
  struct RelationIterator *it,
  RelationIteratorFunctionTable *ap_funcs)
{
  it->mp_funcs = ap_funcs;
  sarl_ref_count_init(&it->m_ref_count);
}

#endif
