extern "C" {

#include <sarl/context_iterator.h>
#include <sarl/context.h>

#include <sarl/set.h>	
#include <sarl/set_iterator.h>	

#include <sarl/relation.h>	
#include <sarl/relation_iterator.h>	

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
	  struct Sarl_ContextIterator *a_it)
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


