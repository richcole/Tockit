extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <iostream>

#include <sarl/relation_impl.h>
#include <sarl/relation_iterator_impl.h>
#include <sarl/join_relation_iterator.h>

/* function prototypes used in function table declaired below */

struct Sarl_RelationIteratorFunctionTable s_join_relation_iterator_table = 
{
  sarl_relation_iterator_join_next_gte,
  sarl_relation_iterator_join_next,
  sarl_relation_iterator_join_val,
  sarl_relation_iterator_join_at_end,
  sarl_relation_iterator_join_reset,
  sarl_relation_iterator_join_decr_ref,
  sarl_relation_iterator_join_copy,
  sarl_relation_iterator_join_inverse
};

Sarl_RelationIterator *sarl_relation_iterator_join(
  struct Sarl_RelationIterator *ap_first, 
  struct Sarl_RelationIterator *ap_second)
{
  Sarl_JoinRelationIterator* it  = new Sarl_JoinRelationIterator();
  sarl_relation_iterator_init(it, &s_join_relation_iterator_table);

  it->mp_first = ap_first;
  it->mp_second = ap_second;

  sarl_relation_incr_ref(it->mp_first);
  sarl_relation_incr_ref(it->mp_second);
  
  return it;
}

struct Sarl_RelationIterator* sarl_relation_iterator_join_inverse(
  struct Sarl_RelationIterator *a_it)
{
  JoinSarl_RelationIterator *join_it = 
    static_cast<JoinSarl_RelationIterator*>(a_it);

  return sarl_relation_iterator_join(
    sarl_relation_iterator_inverse(join_it->mp_first), 
    sarl_relation_iterator_inverse(join_it->mp_second)
  );
};

/* relation_iterator moving operations */
void  sarl_relation_iterator_join_next_gte(
  struct Sarl_RelationIterator *ap_it, 
  Sarl_Pair value)
{
  JoinSarl_RelationIterator *it = 
    static_cast<JoinSarl_RelationIterator*>(ap_it);
  
  /* determine if the iterator is already past value */
  if ( sarl_relation_iterator_at_end(it) ||
    sarl_pair_compare(
      sarl_relation_iterator_val(it),
      value) > 0 
  ) {
    return;
  };

  /* advance past value->dom */
  sarl_relation_iterator_next_gte(it->mp_first, value->dom);
  sarl_relation_iterator_reset(it->mp_second)
  
  while( 
    sarl_relation_iterator_next_gte(
      it->mp_second, 
      sarl_pair(
	sarl_relation_iterator_val(it->mp_first).rng, 
	value.rng
      )
    ),
    sarl_relation_iterator_at_end(
      it->mp_second
    )
  ) {
    /*! \todo{review this algorithm for improved efficiency} */
    sarl_relation_iterator_next(it->mp_first);

    if ( sarl_relation_iterator_at_end(it->mp_first) ) {
      return;
    }
    else if ( sarl_relation_iterator_val(it->mp_first).dom != value.dom ) {
      sarl_relation_iterator_join_advance(it);
      return;
    }
  }
};

void  sarl_relation_iterator_join_advance(
  struct Sarl_RelationIterator *ap_it)
{
  JoinSarl_RelationIterator *it = 
    static_cast<JoinSarl_RelationIterator*>(ap_it);

  sarl_relation_iterator_reset(it->mp_second);
  while(
    sarl_relation_iterator_next_gte(
      it->mp_second, 
      sarl_pair(
	sarl_relation_iterator_val(it->mp_first).rng, 
	0
      )
    ),
    sarl_relation_iterator_at_end(
      it->mp_second
    )
  {
    
  
};

void  sarl_relation_iterator_join_next(
  struct Sarl_RelationIterator *a_it)
{
  SarlPair pair = sarl_relation_iterator_join_val(a_it);
  sarl_relation_iterator_join_next_gte(
    sarl_pair(pair.dom, pair.rng+1)
  )
    }

  JoinSarl_RelationIterator *it = 
    static_cast<JoinSarl_RelationIterator*>(a_it);
  if ( ! sarl_relation_iterator_at_end(it) ) {
    it->m_it++;
  }
}

Sarl_Pair sarl_relation_iterator_join_val(
  struct Sarl_RelationIterator *a_it)
{
  JoinSarl_RelationIterator *it = 
    static_cast<JoinSarl_RelationIterator*>(a_it);
  if ( ! sarl_relation_iterator_at_end(it) ) {
    return *it->m_it;
  }
}

int sarl_relation_iterator_join_at_end(
  struct Sarl_RelationIterator *a_it)
{
  JoinSarl_RelationIterator *it = 
    static_cast<JoinSarl_RelationIterator*>(a_it);
  return it->m_it == it->mp_relation->forward.end();
};

void  sarl_relation_iterator_join_reset(
  struct Sarl_RelationIterator *a_it) 
{
  JoinSarl_RelationIterator *it = 
    static_cast<JoinSarl_RelationIterator*>(a_it);
  it->m_it = it->mp_relation->forward.begin();
};

/* reference counting interface */
void sarl_relation_iterator_join_decr_ref(
  struct Sarl_RelationIterator *a_it)
{
  JoinSarl_RelationIterator *it = 
    static_cast<JoinSarl_RelationIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_relation_decr_ref(it->mp_relation);
    delete it;
  }
}

struct Sarl_RelationIterator* sarl_relation_iterator_join_copy(
  struct Sarl_RelationIterator *a_it)
{
  JoinSarl_RelationIterator *org_it = 
    static_cast<JoinSarl_RelationIterator*>(a_it);
  
  JoinSarl_RelationIterator* copy_it  = new JoinSarl_RelationIterator();
  sarl_relation_iterator_init(copy_it, org_it->mp_funcs);

  copy_it->mp_relation = org_it->mp_relation;
  sarl_relation_incr_ref(copy_it->mp_relation);
  
  copy_it->m_it = org_it->m_it;
  return copy_it;
}

