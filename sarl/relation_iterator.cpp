extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <iostream>

#include <sarl/relation_impl.h>
#include <sarl/relation_iterator_impl.h>
#include <sarl/relation_iterator_plain.h>

/* function prototypes used in function table declaired below */

extern struct Sarl_RelationIteratorFunctionTable 
  s_inverse_relation_iterator_table;

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

/* constructive operations */
struct Sarl_RelationIterator *sarl_relation_iterator_create(
  struct Sarl_Relation *r)
{
  PlainSarl_RelationIterator* it  = new PlainSarl_RelationIterator();
  sarl_relation_iterator_init(it, &s_plain_relation_iterator_table);

  it->mp_relation = r;
  sarl_relation_incr_ref(it->mp_relation);
  
  it->m_it = r->forward.begin();
  return it;
};

struct Sarl_RelationIterator* sarl_relation_iterator_plain_inverse(
  struct Sarl_RelationIterator *a_it)
{
  PlainSarl_RelationIterator *plain_it = 
    static_cast<PlainSarl_RelationIterator*>(a_it);
  PlainSarl_RelationIterator* it  = new PlainSarl_RelationIterator();
  sarl_relation_iterator_init(it, &s_inverse_relation_iterator_table);

  it->mp_relation = plain_it->mp_relation;
  sarl_relation_incr_ref(it->mp_relation);
  
  it->m_it = plain_it->mp_relation->reverse.begin();
  return it;
};


int sarl_relation_iterator_is_empty(
  struct Sarl_RelationIterator *it
)
{
  return sarl_relation_iterator_at_end(it);
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
  PlainSarl_RelationIterator *it = 
    static_cast<PlainSarl_RelationIterator*>(a_it);
  if ( ! sarl_relation_iterator_at_end(it) ) {
    it->m_it++;
  }
}

Sarl_Pair sarl_relation_iterator_plain_val(
  struct Sarl_RelationIterator *a_it)
{
  PlainSarl_RelationIterator *it = 
    static_cast<PlainSarl_RelationIterator*>(a_it);
  if ( ! sarl_relation_iterator_at_end(it) ) {
    return *it->m_it;
  }
}

int sarl_relation_iterator_plain_at_end(
  struct Sarl_RelationIterator *a_it)
{
  PlainSarl_RelationIterator *it = 
    static_cast<PlainSarl_RelationIterator*>(a_it);
  return it->m_it == it->mp_relation->forward.end();
};

void  sarl_relation_iterator_plain_reset(
  struct Sarl_RelationIterator *a_it) 
{
  PlainSarl_RelationIterator *it = 
    static_cast<PlainSarl_RelationIterator*>(a_it);
  it->m_it = it->mp_relation->forward.begin();
};

/* reference counting interface */
void sarl_relation_iterator_plain_decr_ref(struct Sarl_RelationIterator *a_it)
{
  PlainSarl_RelationIterator *it = 
    static_cast<PlainSarl_RelationIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_relation_decr_ref(it->mp_relation);
    delete it;
  }
}

struct Sarl_RelationIterator* sarl_relation_iterator_plain_copy(
  struct Sarl_RelationIterator *a_it)
{
  PlainSarl_RelationIterator *org_it = 
    static_cast<PlainSarl_RelationIterator*>(a_it);
  
  PlainSarl_RelationIterator* copy_it  = new PlainSarl_RelationIterator();
  sarl_relation_iterator_init(copy_it, org_it->mp_funcs);

  copy_it->mp_relation = org_it->mp_relation;
  sarl_relation_incr_ref(copy_it->mp_relation);
  
  copy_it->m_it = org_it->m_it;
  return copy_it;
}

/* functions delegating to the function table */
void  sarl_relation_iterator_next_gte(
  struct Sarl_RelationIterator *it, 
  Sarl_Pair value)
{
  it->mp_funcs->next_gte(it, value);
}

void  sarl_relation_iterator_next(struct Sarl_RelationIterator *it)
{
  it->mp_funcs->next(it);
}

struct Sarl_Pair sarl_relation_iterator_val(
  struct Sarl_RelationIterator *it)
{
  return it->mp_funcs->val(it);
}

int sarl_relation_iterator_at_end(
  struct Sarl_RelationIterator *it)
{
  return it->mp_funcs->at_end(it);
};

void  sarl_relation_iterator_reset(
  struct Sarl_RelationIterator *it) 
{
  it->mp_funcs->reset(it);
};

void sarl_relation_iterator_decr_ref(
  struct Sarl_RelationIterator *it)
{
  it->mp_funcs->decr_ref(it);
}

void sarl_relation_iterator_incr_ref(
  struct Sarl_RelationIterator *it)
{
  sarl_ref_count_incr(&it->m_ref_count);
};

struct Sarl_RelationIterator* sarl_relation_iterator_copy(
  struct Sarl_RelationIterator *a_it)
{
  return a_it->mp_funcs->copy(a_it);
};

struct Sarl_RelationIterator* sarl_relation_iterator_inverse(
  struct Sarl_RelationIterator *a_it)
{
  return a_it->mp_funcs->inverse(a_it);
}

Sarl_Index  sarl_relation_iterator_count(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_RelationIterator *it_copy = sarl_relation_iterator_copy(a_it);
  Sarl_Index count = 0;
  
  SARL_RELATION_ITERATOR_FOR(it_copy) {
    ++count;
  }
  
  sarl_relation_iterator_decr_ref(it_copy);
  return count;
};

Sarl_Index  sarl_relation_iterator_count_remaining(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_RelationIterator *it_copy = sarl_relation_iterator_copy(a_it);
  Sarl_Index count = 0;
  
  while(! sarl_relation_iterator_at_end(it_copy) ) {
    ++count;
    sarl_relation_iterator_next(it_copy);
  }
  
  sarl_relation_iterator_decr_ref(it_copy);
  return count;
};

struct Sarl_SetIterator *sarl_relation_iterator_range(
  struct Sarl_RelationIterator *it)
{
  struct Sarl_RelationIterator *inv_it =
    sarl_relation_iterator_inverse(it);
  struct Sarl_SetIterator *rng_it =
    sarl_relation_iterator_domain(inv_it);
  sarl_relation_iterator_decr_ref(inv_it);
  return rng_it;
};

struct Iterator *sarl_relation_intent(
  struct Sarl_RelationIterator *, Sarl_Index);

struct Iterator *sarl_relation_extent(
  struct Sarl_RelationIterator *, Sarl_Index);
