extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>

#include <sarl/context_iterator.h>
#include <sarl/context_complement_relation_iterator.h>
  
}

#include <iostream>

#include <sarl/relation_impl.h>
#include <sarl/relation_iterator_impl.h>

void  sarl_relation_iterator_context_complement_advance(
  struct Sarl_RelationIterator *ap_it, 
  Sarl_Pair value);

/* function prototypes used in function table declaired below */

struct Sarl_RelationIteratorFunctionTable 
  s_context_complement_relation_iterator_table = 
{
  sarl_relation_iterator_context_complement_next_gte,
  sarl_relation_iterator_context_complement_next,
  sarl_relation_iterator_context_complement_value,
  sarl_relation_iterator_context_complement_at_end,
  sarl_relation_iterator_context_complement_reset,
  sarl_relation_iterator_context_complement_decr_ref,
  sarl_relation_iterator_context_complement_copy,
  sarl_relation_iterator_context_complement_inverse
};

void
  sarl_relation_iterator_context_complement_set_intent(
		struct Sarl_ContextComplementRelationIterator* it)
{
	if ( it->intent != 0 ) {
		sarl_set_iterator_decr_ref(it->intent);
		it->intent = 0;
	}

	while ( ! sarl_set_iterator_at_end(it->G) ) {
		struct Sarl_SetIterator* intent =
			sarl_relation_iterator_intent(it->I, sarl_set_iterator_value(it->G));
		it->intent = sarl_set_iterator_minus(it->M, intent);
		sarl_set_iterator_decr_ref(intent);

		if ( ! sarl_set_iterator_at_end(it->intent) ) {
			break;
		}
	}
};

Sarl_RelationIterator *sarl_relation_iterator_context_complement(
  struct Sarl_ContextIterator  *context_it)
{
  Sarl_ContextComplementRelationIterator* it 
		= new Sarl_ContextComplementRelationIterator();

  sarl_relation_iterator_init(
		it, 
		&s_context_complement_relation_iterator_table
	);

  it->G = sarl_context_iterator_objects(context_it);
  it->M = sarl_context_iterator_attributes(context_it);
  it->I = sarl_context_iterator_incidence(context_it);

	sarl_relation_iterator_context_complement_set_intent(it);
  return it;
}

struct Sarl_RelationIterator* sarl_relation_iterator_context_complement_inverse(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

	cerr << "sarl_relation_iterator_context_complement_inverse: not implemented";
	cerr << endl;
	return 0;
};

void  sarl_relation_iterator_context_complement_next_gte(
  struct Sarl_RelationIterator *a_it, 
  Sarl_Pair value)
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

	Sarl_Pair curr_value = sarl_relation_iterator_value(it);

	if ( value.dom < curr_value.dom ) {
		return;
	}
	else if ( value.dom == curr_value.dom ) {
		if ( value.rng <= curr_value.rng ) {
			return;
		}
		else {
			sarl_set_iterator_next_gte(it->intent, value.rng);
			while	( 
				(! sarl_set_iterator_at_end(it->G)) && 
				sarl_set_iterator_at_end(it->intent))
			{
				sarl_set_iterator_next(it->G);
				sarl_relation_iterator_context_complement_set_intent(it);
			};
		};
	}
	else {
		sarl_set_iterator_next_gte(it->G, value.dom);
		if	( ! sarl_set_iterator_at_end(it->G) ) {
			sarl_relation_iterator_context_complement_set_intent(it);
			if ( sarl_set_iterator_value(it->G) == value.dom ) {
				sarl_set_iterator_next_gte(it->intent, value.rng);

				if ( sarl_set_iterator_at_end(it->intent) ) {
					sarl_set_iterator_next(it->G);
					sarl_relation_iterator_context_complement_set_intent(it);
				}
			}
		}
	};
}

void  sarl_relation_iterator_context_complement_next(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

	if ( it->intent != 0 ) {

		sarl_set_iterator_next(it->intent);
		while	( 
			(! sarl_set_iterator_at_end(it->G)) && 
			sarl_set_iterator_at_end(it->intent))
		{
			sarl_set_iterator_next(it->G);
			sarl_relation_iterator_context_complement_set_intent(it);
		};

	}
}

Sarl_Pair sarl_relation_iterator_context_complement_value(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

	if ( ! sarl_set_iterator_at_end(it->G) ) {

		return sarl_pair(
			sarl_set_iterator_value(it->G),
			sarl_set_iterator_value(it->intent)
		);
		
	}
	else {
		return sarl_pair(0,0);
	}
}

int sarl_relation_iterator_context_complement_at_end(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

	return sarl_set_iterator_at_end(it->G);
};

void  sarl_relation_iterator_context_complement_reset(
  struct Sarl_RelationIterator *a_it) 
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

	sarl_set_iterator_reset(it->G);
	sarl_relation_iterator_context_complement_set_intent(it);
};

/* reference counting interface */
void sarl_relation_iterator_context_complement_decr_ref(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_set_iterator_decr_ref(it->G);
    sarl_set_iterator_decr_ref(it->M);
    sarl_set_iterator_decr_ref(it->intent);
    sarl_relation_iterator_decr_ref(it->I);
    delete it;
  }
}

struct Sarl_RelationIterator* sarl_relation_iterator_context_complement_copy(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_ContextComplementRelationIterator *it = 
    static_cast<Sarl_ContextComplementRelationIterator*>(a_it);

  Sarl_ContextComplementRelationIterator* copy_it  = 
		new Sarl_ContextComplementRelationIterator();
  sarl_relation_iterator_init(copy_it, it->funcs);

  copy_it->G = sarl_set_iterator_copy(it->G);
  copy_it->M = sarl_set_iterator_copy(it->M);
  copy_it->I = sarl_relation_iterator_copy(it->I);
	copy_it->intent = sarl_set_iterator_copy(it->intent);
	
  return copy_it;
}

