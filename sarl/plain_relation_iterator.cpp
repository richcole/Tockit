extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <iostream>

#include <sarl/relation_impl.h>
#include <sarl/relation_iterator_impl.h>
#include <sarl/plain_relation_iterator.h>

/* function prototypes used in function table declaired below */

struct Sarl_RelationIteratorFunctionTable s_plain_relation_iterator_table = 
{
  sarl_relation_iterator_plain_next_gte,
  sarl_relation_iterator_plain_next,
  sarl_relation_iterator_plain_val,
  sarl_relation_iterator_plain_at_end,
  sarl_relation_iterator_plain_reset,
  sarl_relation_iterator_plain_decr_ref,
  sarl_relation_iterator_plain_copy,
  sarl_relation_iterator_plain_inverse
};

/* relation_iterator moving operations */
void  sarl_relation_iterator_plain_next_gte(
  struct Sarl_RelationIterator *it, 
  Sarl_Pair value)
{
  while( ! sarl_relation_iterator_at_end(it) && 
    sarl_pair_compare(sarl_relation_iterator_val(it),value) < 0 ) 
  {
    sarl_relation_iterator_next(it);
  }
}

void  sarl_relation_iterator_plain_next(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator *it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  if ( ! sarl_relation_iterator_at_end(it) ) {
    it->m_it++;
  }
}

Sarl_Pair sarl_relation_iterator_plain_val(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator *it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  if ( ! sarl_relation_iterator_at_end(it) ) {
    return *it->m_it;
  }
}

int sarl_relation_iterator_plain_at_end(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator *it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  return it->m_it == it->mp_relation->forward.end();
};

void  sarl_relation_iterator_plain_reset(
  struct Sarl_RelationIterator *a_it) 
{
  Sarl_PlainRelationIterator *it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  it->m_it = it->mp_relation->forward.begin();
};

/* reference counting interface */
void sarl_relation_iterator_plain_decr_ref(struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator *it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_relation_decr_ref(it->mp_relation);
    delete it;
  }
}

struct Sarl_RelationIterator* sarl_relation_iterator_plain_copy(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator *org_it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  
  Sarl_PlainRelationIterator* copy_it  = new Sarl_PlainRelationIterator();
  sarl_relation_iterator_init(copy_it, org_it->mp_funcs);

  copy_it->mp_relation = org_it->mp_relation;
  sarl_relation_incr_ref(copy_it->mp_relation);
  
  copy_it->m_it = org_it->m_it;
  return copy_it;
}

