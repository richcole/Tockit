extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <iostream>

#include <sarl/relation_impl.h>
#include <sarl/relation_iterator_impl.h>
#include <sarl/join_relation_iterator.h>

void  sarl_relation_iterator_join_advance(
  struct Sarl_RelationIterator *ap_it, 
  Sarl_Pair value);

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

  sarl_relation_iterator_incr_ref(it->mp_first);
  sarl_relation_iterator_incr_ref(it->mp_second);
  
  return it;
}

struct Sarl_RelationIterator* sarl_relation_iterator_join_inverse(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_JoinRelationIterator *join_it = 
    static_cast<Sarl_JoinRelationIterator*>(a_it);

  return sarl_relation_iterator_join(
    sarl_relation_iterator_inverse(join_it->mp_first), 
    sarl_relation_iterator_inverse(join_it->mp_second)
  );
};

/* return true if pair is a member of the join */
bool  sarl_relation_iterator_join_exists_pair(
  struct Sarl_RelationIterator *ap_it, 
  Sarl_Pair value,
  Sarl_Index* join_value)
{
  Sarl_JoinRelationIterator *it = 
    static_cast<Sarl_JoinRelationIterator*>(ap_it);

  Sarl_SetIterator* extent_it = 
    sarl_relation_iterator_extent(it->mp_second, value.rng);

  Sarl_SetIterator* intent_it = 
    sarl_relation_iterator_intent(it->mp_first, value.dom);

  Sarl_SetIterator* and_it = 
    sarl_set_iterator_meet(
      extent_it, intent_it
    );

  int return_value;

  if ( ! sarl_set_iterator_at_end(and_it) ) {
    return_value = true;
    *join_value = sarl_set_iterator_val(and_it);
  }
  else {
    return_value = false;
  }

  sarl_set_iterator_decr_ref(extent_it);
  sarl_set_iterator_decr_ref(intent_it);
  sarl_set_iterator_decr_ref(and_it);

  return return_value;
};

void  sarl_relation_iterator_join_next_gte(
  struct Sarl_RelationIterator *ap_it, 
  Sarl_Pair value)
{
  Sarl_JoinRelationIterator *it = 
    static_cast<Sarl_JoinRelationIterator*>(ap_it);

  /* determine if the iterator is already past value */
  if ( sarl_relation_iterator_at_end(it) ||
    sarl_pair_compare(
      sarl_relation_iterator_val(it),
      value) > 0 
  ) {
    return;
  };
  
  sarl_relation_iterator_join_advance(ap_it, value);
}

/* relation_iterator moving operations */
void  sarl_relation_iterator_join_advance(
  struct Sarl_RelationIterator *ap_it, 
  Sarl_Pair value)
{
  Sarl_JoinRelationIterator *it = 
    static_cast<Sarl_JoinRelationIterator*>(ap_it);

  Sarl_SetIterator *range_it = 
    sarl_relation_iterator_range(it->mp_second);

  Sarl_Index join_value;

  sarl_relation_iterator_next_gte(it->mp_first, sarl_pair(value.dom, 0));
  while( ! sarl_relation_iterator_at_end(it->mp_first) ) {
    
    /* search for the next value from value.dom to something
     * after value.rng 
     */
    sarl_set_iterator_next_gte(range_it, value.rng);
    while( ! sarl_set_iterator_at_end(range_it) ) {
      if ( sarl_relation_iterator_join_exists_pair(
	     ap_it,
	     sarl_pair(
	       sarl_relation_iterator_val(it->mp_first).dom,
	       sarl_set_iterator_val(range_it)
	     ),
	     &join_value
	   )) 
      {
	sarl_relation_iterator_reset(it->mp_first);
	sarl_relation_iterator_next_gte(
	  it->mp_first,
	  sarl_pair(
	    sarl_relation_iterator_val(it->mp_first).dom,
	    join_value
	  )
	);
	
	sarl_relation_iterator_reset(it->mp_second);
	sarl_relation_iterator_next_gte(it->mp_second,
	  sarl_pair(
	    join_value, 
	    sarl_set_iterator_val(range_it)
	  )
	);
	return;
      }
      sarl_set_iterator_next(range_it);
    }

    /* otherwise increment the domain of first */
    sarl_relation_iterator_next_gte(
      it->mp_first,
      sarl_pair(
	sarl_relation_iterator_val(it->mp_first).dom + 1,
	0
      )
    );

  } // [ while ! it->mp_first.at_end() ]
  
  sarl_set_iterator_decr_ref(range_it);
};

void  sarl_relation_iterator_join_next(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_Pair pair = sarl_relation_iterator_join_val(a_it);
  sarl_relation_iterator_join_next_gte(
    a_it,
    sarl_pair(pair.dom, pair.rng+1)
  );
}

Sarl_Pair sarl_relation_iterator_join_val(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_JoinRelationIterator *it = 
    static_cast<Sarl_JoinRelationIterator*>(a_it);
  return sarl_pair(
    sarl_relation_iterator_val(it->mp_first).dom,
    sarl_relation_iterator_val(it->mp_second).rng
  );
}

int sarl_relation_iterator_join_at_end(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_JoinRelationIterator *it = 
    static_cast<Sarl_JoinRelationIterator*>(a_it);
  return sarl_relation_iterator_at_end(it->mp_first);
};

void  sarl_relation_iterator_join_reset(
  struct Sarl_RelationIterator *a_it) 
{
  Sarl_JoinRelationIterator *it = 
    static_cast<Sarl_JoinRelationIterator*>(a_it);
  sarl_relation_iterator_reset(it->mp_first);
  sarl_relation_iterator_reset(it->mp_second);
  sarl_relation_iterator_join_advance(it, sarl_pair(0,0));
};

/* reference counting interface */
void sarl_relation_iterator_join_decr_ref(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_JoinRelationIterator *it = 
    static_cast<Sarl_JoinRelationIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_relation_iterator_decr_ref(it->mp_first);
    sarl_relation_iterator_decr_ref(it->mp_second);
    delete it;
  }
}

struct Sarl_RelationIterator* sarl_relation_iterator_join_copy(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_JoinRelationIterator *org_it = 
    static_cast<Sarl_JoinRelationIterator*>(a_it);
  
  Sarl_JoinRelationIterator* copy_it  = new Sarl_JoinRelationIterator();
  sarl_relation_iterator_init(copy_it, org_it->mp_funcs);

  copy_it->mp_first = org_it->mp_first;
  copy_it->mp_second = org_it->mp_second;
  
  sarl_relation_iterator_incr_ref(copy_it->mp_first);
  sarl_relation_iterator_incr_ref(copy_it->mp_second);
  
  return copy_it;
}

