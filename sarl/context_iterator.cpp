extern "C" {

#include <sarl/context_iterator.h>
#include <sarl/context.h>

#include <sarl/set.h>	
#include <sarl/set_iterator.h>	

#include <sarl/relation.h>	
#include <sarl/relation_iterator.h>	
#include <sarl/lectic.h>

}

#include <sarl/context_impl.h>
#include <sarl/context_iterator_impl.h>
#include <iostream>

using namespace std;

struct Sarl_ContextIterator *
  sarl_context_iterator_create(
		struct Sarl_Context* a_context)
{
	Sarl_ContextIterator* p_it = new Sarl_ContextIterator();
	sarl_context_iterator_init(p_it);

	p_it->G = sarl_set_iterator_create(a_context->G);
	p_it->M = sarl_set_iterator_create(a_context->M);
	p_it->I = sarl_relation_iterator_create(a_context->I);

	return p_it;
}

struct Sarl_ContextIterator *
  sarl_context_iterator_create_from_relation(
    struct Sarl_RelationIterator* r)
{
  Sarl_ContextIterator* p_it = new Sarl_ContextIterator();
  sarl_context_iterator_init(p_it);
  
  p_it->G = sarl_relation_iterator_domain(r);
  p_it->M = sarl_relation_iterator_range(r);
  p_it->I = sarl_relation_iterator_obtain_ownership(r);

  return p_it;
}

struct Sarl_ContextIterator *
  sarl_context_iterator_copy(
		struct Sarl_ContextIterator* a_context)
{
	Sarl_ContextIterator* p_it = new Sarl_ContextIterator();
	sarl_context_iterator_init(p_it);

	p_it->G = sarl_set_iterator_copy(a_context->G);
	p_it->M = sarl_set_iterator_copy(a_context->M);
	p_it->I = sarl_relation_iterator_copy(a_context->I);

	return p_it;
}

struct Sarl_ContextIterator *
  sarl_context_iterator_copy_iterators(
		struct Sarl_SetIterator*      a_G,
		struct Sarl_SetIterator*      a_M,
		struct Sarl_RelationIterator* a_I)
{
	Sarl_ContextIterator* p_it = new Sarl_ContextIterator();
	sarl_context_iterator_init(p_it);

	p_it->G = sarl_set_iterator_copy(a_G);
	p_it->M = sarl_set_iterator_copy(a_M);
	p_it->I = sarl_relation_iterator_copy(a_I);

	return p_it;
};

void
  sarl_context_iterator_decr_ref(
    struct Sarl_ContextIterator *a_it
  )
{
  if ( sarl_ref_count_decr(&a_it->ref_count) ) {
    sarl_set_iterator_decr_ref(a_it->G);
    sarl_set_iterator_decr_ref(a_it->M);
    sarl_relation_iterator_decr_ref(a_it->I);
    delete a_it;
  }
};

struct Sarl_SetIterator *
  sarl_context_iterator_objects(
    struct Sarl_ContextIterator *context_it)
{
  return sarl_set_iterator_copy(context_it->G);
};


struct Sarl_SetIterator *
  sarl_context_iterator_attributes(
    struct Sarl_ContextIterator *context_it)
{
  return sarl_set_iterator_copy(context_it->M);
};


struct Sarl_RelationIterator *
  sarl_context_iterator_incidence(
    struct Sarl_ContextIterator *context_it)
{
  return sarl_relation_iterator_copy(context_it->I);
};
 						     

struct Sarl_ContextIterator *
  sarl_context_iterator_inverse(
		struct Sarl_ContextIterator *context_it)
{
  Sarl_ContextIterator* p_it = new Sarl_ContextIterator();
  sarl_context_iterator_init(p_it);

  p_it->G = sarl_set_iterator_copy(context_it->M);
  p_it->M = sarl_set_iterator_copy(context_it->G);
  p_it->I = sarl_relation_iterator_inverse(context_it->I);

  return p_it;
};

struct Sarl_ContextIterator *
  sarl_context_iterator_complement(
		struct Sarl_ContextIterator *context_it)
{
  Sarl_ContextIterator* p_it = new Sarl_ContextIterator();
  sarl_context_iterator_init(p_it);

  p_it->G = sarl_set_iterator_copy(context_it->M);
  p_it->M = sarl_set_iterator_copy(context_it->G);
  p_it->I = sarl_relation_iterator_context_complement(context_it);

  return p_it;
};

struct Sarl_RelationIterator*
  sarl_context_iterator_up_arrow(
		struct SarlContextIterator *context_it)
{
  cerr << "sarl_context_iterator_up_arrow: Not implemented." << endl;
  return 0;
};


struct Sarl_RelationIterator*
  sarl_context_iterator_down_arrow(
		 struct SarlContextIterator *context_it)
{
  cerr << "sarl_context_iterator_down_arrow: Not implemented." << endl;
  return 0;
};
 

struct Sarl_RelationIterator*
  sarl_context_iterator_updown_arrow(
		struct SarlContextIterator *context_it)
{
  cerr << "sarl_context_iterator_down_arrow: Not implemented." << endl;
  return 0;
}

struct Sarl_SetIterator *
  sarl_context_iterator_intent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *A
  )
{
  Sarl_SetIterator* result;
  Sarl_RelationIterator* I;
  
  if ( sarl_set_iterator_is_empty(A) ) {
    result = sarl_context_iterator_attributes(K);
  }
  else {
    I = sarl_context_iterator_incidence(K);

    sarl_relation_iterator_release_ownership(I);
    result = sarl_relation_iterator_intent_set(I, A);
    sarl_relation_iterator_decr_ref(I);
  }
  return result;
};


struct Sarl_SetIterator *
  sarl_context_iterator_extent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *B
  )
{
  Sarl_SetIterator* result;
  Sarl_RelationIterator* I;
  
  if ( sarl_set_iterator_is_empty(B) ) {
    result = sarl_context_iterator_objects(K);
  }
  else {
    I = sarl_context_iterator_incidence(K);

    sarl_relation_iterator_release_ownership(I);
    result = sarl_relation_iterator_extent_set(I, B);
    sarl_relation_iterator_decr_ref(I);
  }
  return result;
};


struct Sarl_SetIterator *
  sarl_context_iterator_intent_extent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *A
  )
{
  Sarl_SetIterator *intent;
  Sarl_SetIterator *intent_extent;
  
  intent = sarl_context_iterator_intent_set(K, A);
  sarl_set_iterator_release_ownership(intent);

  intent_extent = sarl_context_iterator_extent_set(K, intent);
  sarl_set_iterator_decr_ref(intent);

  return intent_extent;
};


struct Sarl_SetIterator *
  sarl_context_iterator_extent_intent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *B
  )
{
  Sarl_SetIterator *extent;
  Sarl_SetIterator *extent_intent;

  extent = sarl_context_iterator_extent_set(K, B);
  sarl_set_iterator_release_ownership(extent);

  extent_intent = sarl_context_iterator_intent_set(K, extent);
  sarl_set_iterator_decr_ref(extent);

  return extent_intent;
};

struct Sarl_SetIterator* 
  sarl_context_iterator_next_extent(
    Sarl_ContextIterator *K,
    Sarl_SetIterator     *A)
{
  struct Sarl_SetIterator *G;
  struct Sarl_SetIterator *i;

  struct Sarl_SetIterator *curr;
  struct Sarl_SetIterator *next;
  struct Sarl_SetIterator *m;

  struct Sarl_SetIterator *next_ii = 0;

  bool   finished = false;

  curr = sarl_set_iterator_obtain_ownership(A);

  G = sarl_context_iterator_objects(K);
  sarl_set_iterator_release_ownership(G);
  i = sarl_set_iterator_minus(G, A);
  
  for(sarl_set_iterator_reset(i);
      ! finished && ! sarl_set_iterator_at_end(i);
      sarl_set_iterator_next(i)
  ) 
  {
    // curr = curr (+) i
    sarl_set_iterator_release_ownership(curr);
    next = sarl_set_iterator_lectic_next_gte(
      curr, 
      sarl_set_iterator_value(i),
      G);

    // until last(curr'') == last(curr)
    next_ii = sarl_context_iterator_intent_extent_set(K, next);
    m = sarl_set_iterator_minus(next_ii, curr);
    if ( sarl_set_iterator_last(m) == sarl_set_iterator_value(i) ) {
      finished = true;
    }
    else {
      sarl_set_iterator_decr_ref(next_ii);
    }

    sarl_set_iterator_decr_ref(m);
    sarl_set_iterator_decr_ref(curr);
    curr = next;
  };

  sarl_set_iterator_decr_ref(curr);
  sarl_set_iterator_decr_ref(G);
  sarl_set_iterator_decr_ref(i);
  
  return next_ii;
};

struct Sarl_SetIterator* 
  sarl_context_iterator_next_extent_superseteq(
    Sarl_ContextIterator *K,
    Sarl_SetIterator     *A, 
    Sarl_SetIterator     *parent_extent)
{
  struct Sarl_SetIterator *G;
  struct Sarl_SetIterator *i;

  struct Sarl_SetIterator *curr;
  struct Sarl_SetIterator *next;
  struct Sarl_SetIterator *m;
  struct Sarl_SetIterator *tmp;

  struct Sarl_SetIterator *next_ii = 0;

  Sarl_Index               start_value;
  bool                     finished = false;

  // obtain ownership of A
  A             = sarl_set_iterator_obtain_ownership(A);
  parent_extent = sarl_set_iterator_obtain_ownership(parent_extent);
  curr          = sarl_set_iterator_obtain_ownership(parent_extent);
  G             = sarl_context_iterator_objects(K);

  sarl_set_iterator_reset(A);
  sarl_set_iterator_reset(parent_extent);
  sarl_set_iterator_reset(curr);

  // determine last(A \ parent_extent)
  tmp = sarl_set_iterator_minus(A, parent_extent);
  if ( sarl_set_iterator_at_end(tmp) ) {
    if ( sarl_set_iterator_is_empty(A) ) {
      start_value = 1;
    }
    else {
      start_value = sarl_set_iterator_last(G) + 1;
    }
  }
  else {
    start_value = sarl_set_iterator_last(tmp) + 1;
  }
  sarl_set_iterator_decr_ref(tmp);
  
  sarl_set_iterator_release_ownership(G);
  sarl_set_iterator_release_ownership(A);
  sarl_set_iterator_reset(G);
  i = sarl_set_iterator_minus(G, A);

  for(sarl_set_iterator_reset(i), sarl_set_iterator_next_gte(i, start_value);
      ! finished && ! sarl_set_iterator_at_end(i);
      sarl_set_iterator_next(i)
  ) 
  {
    // curr = curr (+) i
    sarl_set_iterator_release_ownership(curr);
    tmp = sarl_set_iterator_lectic_next_gte(
      curr, 
      sarl_set_iterator_value(i),
      G);
    sarl_set_iterator_release_ownership(tmp);
    next = sarl_set_iterator_union(tmp, parent_extent);
    sarl_set_iterator_decr_ref(tmp);

    // until last(curr'') == last(curr)
    next_ii = sarl_context_iterator_intent_extent_set(K, next);
    m = sarl_set_iterator_minus(next_ii, curr);
    if ( sarl_set_iterator_last(m) == sarl_set_iterator_value(i) ) {
      finished = true;
    }
    else {
      sarl_set_iterator_decr_ref(next_ii);
    }

    sarl_set_iterator_decr_ref(m);
    sarl_set_iterator_decr_ref(curr);
    curr = next;
  };

  sarl_set_iterator_decr_ref(curr);
  sarl_set_iterator_decr_ref(G);
  sarl_set_iterator_decr_ref(A);
  sarl_set_iterator_decr_ref(i);
  sarl_set_iterator_decr_ref(parent_extent);
  
  return next_ii;
};

void sarl_context_iterator_incr_ref(struct Sarl_ContextIterator *it)
{
  sarl_ref_count_incr(&it->ref_count);
};

struct Sarl_ContextIterator *
  sarl_context_iterator_obtain_ownership(
    struct Sarl_ContextIterator* it
  )
{
  return sarl_iterator_obtain_ownership(it, sarl_context_iterator_copy);
};

struct Sarl_ContextIterator *
  sarl_context_iterator_release_ownership(
    struct Sarl_ContextIterator* it
  )
{
  return sarl_iterator_release_ownership(it);
};

  

