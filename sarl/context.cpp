extern "C" {
#include <sarl/context.h>
#include <sarl/set.h>
#include <sarl/relation.h>
}

#include <sarl/context_impl.h>
#include <sarl/context_iterator_impl.h>
#include <sarl/set_impl.h>
#include <sarl/relation_impl.h>
	
struct Sarl_Context *sarl_context_create()
{
  Sarl_Context* p_context = new Sarl_Context();
  sarl_context_init(p_context);
  
  p_context->G = sarl_set_create();
  p_context->M = sarl_set_create();
  p_context->I = sarl_relation_create();
  
  return p_context;
}


struct Sarl_Context *
sarl_context_copy(
	struct Sarl_ContextIterator *ap_context)
{
  Sarl_Context* p_context = new Sarl_Context();
  sarl_context_init(p_context);
  
  p_context->G = sarl_set_copy(ap_context->G);
  p_context->M = sarl_set_copy(ap_context->M);
  p_context->I = sarl_relation_copy(ap_context->I);
  
  return p_context;
}


void 
sarl_context_insert(
  struct Sarl_Context *ap_context, 
  Sarl_Index g, Sarl_Index m)
{
  Sarl_Pair pair = sarl_pair(g,m);
  sarl_context_insert_pair(ap_context, pair);
}

void 
sarl_context_insert_pair(
  struct Sarl_Context *ap_context, 
  Sarl_Pair            a_pair)
{
  sarl_relation_insert_pair(ap_context->I, a_pair);
  sarl_set_insert(ap_context->G, a_pair.dom);
  sarl_set_insert(ap_context->M, a_pair.rng);
}


void 
sarl_context_insert_object(
  struct Sarl_Context *ap_context, 
  Sarl_Index a_value)
{
  sarl_set_insert(ap_context->G, a_value);
}

void 
sarl_context_insert_attribute(
  struct Sarl_Context *ap_context, 
	Sarl_Index a_value)
{
  sarl_set_insert(ap_context->M, a_value);
}

void 
sarl_context_remove_pair(
  struct Sarl_Context *ap_context, 
  Sarl_Pair a_pair)
{
  sarl_relation_insert_pair(ap_context->I, a_pair);
}

void 
sarl_context_remove(
  struct Sarl_Context *ap_context, 
  Sarl_Index g, Sarl_Index m)
{
  sarl_relation_insert_pair(ap_context->I, sarl_pair(g,m));
}

void 
sarl_context_remove_object(
  struct Sarl_Context *ap_context, 
	Sarl_Index a_value)
{
  sarl_relation_remove_intent(ap_context->I, a_value);
  sarl_set_remove(ap_context->G, a_value);
};


void 
sarl_context_remove_attribute(
  struct Sarl_Context *ap_context, 
	Sarl_Index a_value)
{
  sarl_relation_remove_extent(ap_context->I, a_value);
  sarl_set_remove(ap_context->M, a_value);
};


void
sarl_context_decr_ref(
	struct Sarl_Context *a_context)
{
  if ( sarl_ref_count_decr(&a_context->ref_count) ) {
    sarl_set_decr_ref(a_context->G);
    sarl_set_decr_ref(a_context->M);
    sarl_relation_decr_ref(a_context->I);
    delete a_context;
  }
};

void
sarl_context_incr_ref(
	struct Sarl_Context *ap_context)
{
  sarl_ref_count_incr(&ap_context->ref_count);
};


