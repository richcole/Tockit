extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
#include <sarl/set_iterator.h>

}

#include <iostream>

#include <sarl/relation_impl.h>
#include <sarl/relation_iterator_impl.h>
#include <sarl/plain_relation_iterator.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

extern struct Sarl_RelationIteratorFunctionTable 
  s_plain_relation_iterator_table;

extern struct Sarl_RelationIteratorFunctionTable 
  s_inverse_relation_iterator_table;

/* constructive operations */
struct Sarl_RelationIterator *sarl_relation_iterator_create(
  struct Sarl_Relation *r)
{
  Sarl_PlainRelationIterator* it  = new Sarl_PlainRelationIterator();
  sarl_relation_iterator_init(it, &s_plain_relation_iterator_table);

  it->relation = r;
  sarl_relation_incr_ref(it->relation);
  
  it->it = r->forward.begin();
  return it;
};

int sarl_relation_iterator_is_empty(
  struct Sarl_RelationIterator *it
)
{
  return sarl_relation_iterator_at_end(it);
};

/* functions delegating to the function table */

void  sarl_relation_iterator_next_gte(
  struct Sarl_RelationIterator *it, 
  Sarl_Pair value)
{
  it->funcs->next_gte(it, value);
}

void  sarl_relation_iterator_next(
  struct Sarl_RelationIterator *it)
{
  it->funcs->next(it);
}

struct Sarl_Pair sarl_relation_iterator_value(
  struct Sarl_RelationIterator *it)
{
  return it->funcs->value(it);
}

int sarl_relation_iterator_at_end(
  struct Sarl_RelationIterator *it)
{
  return it->funcs->at_end(it);
};

void  sarl_relation_iterator_reset(
  struct Sarl_RelationIterator *it) 
{
  it->funcs->reset(it);
};

void sarl_relation_iterator_decr_ref(
  struct Sarl_RelationIterator *it)
{
  it->funcs->decr_ref(it);
}

void sarl_relation_iterator_incr_ref(
  struct Sarl_RelationIterator *it)
{
  sarl_ref_count_incr(&it->ref_count);
};

struct Sarl_RelationIterator* sarl_relation_iterator_copy(
  struct Sarl_RelationIterator *a_it)
{
  return a_it->funcs->copy(a_it);
};

struct Sarl_RelationIterator* sarl_relation_iterator_inverse(
  struct Sarl_RelationIterator *a_it)
{
  return a_it->funcs->inverse(a_it);
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

struct Sarl_SetIterator *
sarl_construct_aggregate(
  struct Sarl_SetIterator **v, 
  int lower, 
  int upper, 
  struct Sarl_SetIterator *(*func)(
    struct Sarl_SetIterator *a, struct Sarl_SetIterator *b)
)
{
  Sarl_SetIterator *left, *right, *result;

  if ( upper - lower == 3 ) {
    result = func(
      v[lower], 
      left = sarl_set_iterator_release_ownership(
        sarl_construct_aggregate(v, lower+1, upper, func)
      )
    );
    sarl_set_iterator_decr_ref(left);
  }
  else if ( upper - lower == 2 ) {
    result = func(v[lower], v[lower+1]);
  }
  else {
    result = func(
      left = sarl_set_iterator_release_ownership(
        sarl_construct_aggregate(v, lower, (lower + upper)/2, func)
      ),
      right = sarl_set_iterator_release_ownership(
        sarl_construct_aggregate(v, (lower + upper)/2, upper, func)
      )
    );
    sarl_set_iterator_decr_ref(left);
    sarl_set_iterator_decr_ref(right);
  };

  return result;
};

struct Sarl_SetIterator *sarl_relation_iterator_aggregate(
  struct Sarl_RelationIterator *r, 
  struct Sarl_SetIterator *s,
  struct Sarl_SetIterator *(*op)(
    struct Sarl_RelationIterator *,
    Sarl_Index),
  struct Sarl_SetIterator *(*ag)(
    struct Sarl_SetIterator *,
    struct Sarl_SetIterator *),
  struct Sarl_SetIterator *(*empty_op)(
    struct Sarl_RelationIterator *)
)
{
  Sarl_Index       count, i; 
  Sarl_SetIterator **v;
  Sarl_SetIterator *result;
  
  count = sarl_set_iterator_count(s);
  v = new (struct Sarl_SetIterator *)[count];

  i = 0;
  SARL_SET_ITERATOR_FOR(s) {
    v[i] = op(r, sarl_set_iterator_value(s));
    sarl_set_iterator_release_ownership(v[i]);
    ++i;
  }

  if ( count > 1 ) {
     result = sarl_construct_aggregate(v, 0, count, ag);
  }
  else {
    if ( count == 1 ) {
      result = sarl_set_iterator_obtain_ownership(v[0]);
    }
    else {
      result = (*empty_op)(r);
    }
  }

  for(i=0;i<count;++i) {
    sarl_set_iterator_decr_ref(v[i]);
  };
  
  delete v;
  return result;
};

struct Sarl_SetIterator *sarl_relation_iterator_intent_set(
  struct Sarl_RelationIterator *r, struct Sarl_SetIterator *s
)
{
  return sarl_relation_iterator_aggregate(
    r, s, 
    sarl_relation_iterator_intent, 
    sarl_set_iterator_meet, 
    sarl_relation_iterator_range
  );
};

struct Sarl_SetIterator *sarl_relation_iterator_extent_set(
  struct Sarl_RelationIterator *r, 
  struct Sarl_SetIterator *s)
{
  return sarl_relation_iterator_aggregate(
    r, s, 
    sarl_relation_iterator_extent, 
    sarl_set_iterator_meet, 
    sarl_relation_iterator_domain
  );
};

struct Sarl_SetIterator *sarl_relation_iterator_extent_intent_set(
  struct Sarl_RelationIterator *r, struct Sarl_SetIterator *intent
)
{
  Sarl_SetIterator *extent;
  Sarl_SetIterator *extent_intent;

  extent = sarl_relation_iterator_extent_set(r, intent);
  sarl_set_iterator_release_ownership(extent);

  extent_intent = sarl_relation_iterator_intent_set(r, extent);
  sarl_set_iterator_decr_ref(extent);

  return extent_intent;
};

struct Sarl_SetIterator *sarl_relation_iterator_intent_extent_set(
  struct Sarl_RelationIterator *r, struct Sarl_SetIterator *extent
)
{
  Sarl_SetIterator *intent;
  Sarl_SetIterator *intent_extent;
  
  intent        = sarl_relation_iterator_intent_set(r, extent);
  sarl_set_iterator_release_ownership(intent);

  intent_extent = sarl_relation_iterator_extent_set(r, intent);
  sarl_set_iterator_decr_ref(intent);

  return intent_extent;
};

extern struct Sarl_RelationIterator *
  sarl_relation_iterator_obtain_ownership(
    struct Sarl_RelationIterator *it
  )
{
  Sarl_RelationIterator *result;
  
  if ( it->ownership == SARL_HAS_NO_OWNER ) {
    result = it;
    sarl_relation_iterator_incr_ref(it);
  }
  else {
    result = sarl_relation_iterator_copy(it);
    sarl_relation_iterator_release_ownership(result);
  };
  
  SARL_TEST_ASSERT_EQ(result->ownership, SARL_HAS_NO_OWNER);
  result->ownership = SARL_HAS_OWNER;
  return result;
};

void
  sarl_relation_iterator_release_ownership(
    struct Sarl_RelationIterator *it
  )
{
  SARL_TEST_ASSERT_EQ(it->ownership, SARL_HAS_OWNER);
  it->ownership = SARL_HAS_NO_OWNER;
};







