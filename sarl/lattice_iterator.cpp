extern "C" {

#include <sarl/config.h>
#include <sarl/concept_iterator.h>
#include <sarl/context_iterator.h>
#include <sarl/lattice_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/concept_iterator_impl.h>
#include <sarl/lattice_iterator_impl.h>
#include <sarl/test.h>

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create(
    struct Sarl_ContextIterator *context)
{
  struct Sarl_LatticeIterator *result = new Sarl_LatticeIterator;
  sarl_lattice_iterator_init(result);
  
  result->context = sarl_context_iterator_obtain_ownership(context);
  result->A = 0;
  result->B = 0;
  
  sarl_lattice_iterator_reset(result);
  return result;
};


void 
  sarl_lattice_iterator_reset(
    struct Sarl_LatticeIterator *it
  )
{
  struct Sarl_SetIterator* A;
  struct Sarl_SetIterator* B;

  if ( it->A != 0 ) {
    sarl_set_iterator_decr_ref(it->A);
    A = 0;
  };
  
  if ( it->B != 0 ) {
    sarl_set_iterator_decr_ref(it->B);
    B = 0;
  };

  it->B = sarl_context_iterator_attributes(it->context);
  it->A = sarl_context_iterator_extent_set(it->context, it->B);
};


void 
  sarl_lattice_iterator_next(
     struct Sarl_LatticeIterator *it
  )
{
  struct Sarl_SetIterator* next_A;
  struct Sarl_SetIterator* next_B;
  
  sarl_set_iterator_release_ownership(it->A);
  next_A = sarl_context_iterator_next_extent(it->context, it->A);

  if ( next_A != 0 ) {
    next_B = sarl_context_iterator_intent_set(it->context, next_A);
  }
  else {
    next_A = next_B = 0;
  }

  sarl_set_iterator_decr_ref(it->A);
  sarl_set_iterator_decr_ref(it->B);
  
  it->A = next_A;
  it->B = next_B;
};

int  
  sarl_lattice_iterator_at_end(
    struct Sarl_LatticeIterator *it)
{
  return it->A == 0;
};

void 
  sarl_lattice_iterator_next_gte(
    struct Sarl_LatticeIterator *it,
     struct Sarl_ConceptIterator *c
  )
{
  struct Sarl_SetIterator* A      = sarl_concept_iterator_extent(c);
  struct Sarl_SetIterator* next_A = sarl_concept_iterator_extent(c);
  struct Sarl_SetIterator* next_B = sarl_concept_iterator_extent(c);

  sarl_set_iterator_release_ownership(A);
  if ( sarl_set_iterator_lexical_compare(A, it->A) > 0 ) {

    sarl_set_iterator_reset(A);
    next_A = sarl_context_iterator_next_extent(it->context, A);

    if ( next_A != 0 ) {
      next_B = sarl_context_iterator_intent_set(it->context, next_A);
    }
    else {
      next_A = next_B = 0;
    }

    sarl_set_iterator_release_ownership(it->A);
    sarl_set_iterator_release_ownership(it->B);
    sarl_set_iterator_decr_ref(it->A);
    sarl_set_iterator_decr_ref(it->B);
  
    it->A = next_A;
    it->B = next_B;
  }
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal_from_intent(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator *B)
{
  SARL_NOT_IMPLEMENTED;
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter_from_extent(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator *A)
{
  SARL_NOT_IMPLEMENTED;
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter(
    struct Sarl_LatticeIterator *it,
    struct Sarl_ConceptIterator *c)
{
  SARL_NOT_IMPLEMENTED;
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal(
    struct Sarl_LatticeIterator *it,
    struct Sarl_LatticeIterator *c)
{
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_extent(
    struct Sarl_LatticeIterator *it)
{
  return sarl_set_iterator_copy(it->A);
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_intent(
    struct Sarl_LatticeIterator *it
  )
{
  return sarl_set_iterator_copy(it->B);
};

struct Sarl_ConceptIterator *
  sarl_lattice_iterator_value(
    struct Sarl_LatticeIterator *it
  )
{
  return sarl_concept_iterator_create(it->A, it->B);
};

void
  sarl_lattice_iterator_decr_ref(
    struct Sarl_LatticeIterator *it
  )
{
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    if (it->A) {
      sarl_set_iterator_release_ownership(it->A);
      sarl_set_iterator_decr_ref(it->A);
    }
    if (it->B) {
      sarl_set_iterator_release_ownership(it->B);
      sarl_set_iterator_decr_ref(it->B);
    }
    sarl_context_iterator_release_ownership(it->context);
    sarl_context_iterator_decr_ref(it->context);
    delete it;
  }
};


void
  sarl_lattice_iterator_incr_ref(
    struct Sarl_LatticeIterator *it
  )
{
  sarl_iterator_incr_ref(it);
};


