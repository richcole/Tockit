extern "C" {

#include <sarl/global.h>
#include <sarl/concept_iterator.h>
#include <sarl/context_iterator.h>
#include <sarl/lattice_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>

#include <sarl/filter_lattice_iterator.h>
  
}

#include <sarl/concept_iterator_impl.h>
#include <sarl/lattice_iterator_impl.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

static void sarl_lattice_iterator_plain_next_gte(
  struct Sarl_LatticeIterator *it, 
  struct Sarl_ConceptIterator* value);

static void sarl_lattice_iterator_plain_prev_leq(
  struct Sarl_LatticeIterator *it, 
  struct Sarl_ConceptIterator* value);

static void sarl_lattice_iterator_plain_next(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_plain_prev(
  struct Sarl_LatticeIterator *it);

static Sarl_ConceptIterator* sarl_lattice_iterator_plain_value(
  struct Sarl_LatticeIterator *it);

static int sarl_lattice_iterator_plain_at_end(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_plain_reset(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_plain_reset_last(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_plain_decr_ref(
  struct Sarl_LatticeIterator *it);

static struct Sarl_LatticeIterator* sarl_lattice_iterator_plain_copy(
  struct Sarl_LatticeIterator *it);

static struct Sarl_SetIterator* 
  sarl_lattice_iterator_plain_extent(
    struct Sarl_LatticeIterator *it
  );

static struct Sarl_SetIterator* 
  sarl_lattice_iterator_plain_intent(
    struct Sarl_LatticeIterator *it
  );

static struct Sarl_LatticeIterator* 
  sarl_lattice_iterator_plain_filter_from_extent(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator* filter
  );

/* function prototypes used in function table declaired below */

struct Sarl_LatticeIteratorFunctionTable s_plainLatticeIteratorTable = 
{
  sarl_lattice_iterator_plain_prev_leq,
  sarl_lattice_iterator_plain_next_gte,
  sarl_lattice_iterator_plain_next,
  sarl_lattice_iterator_plain_prev,
  sarl_lattice_iterator_plain_value,
  sarl_lattice_iterator_plain_at_end,
  sarl_lattice_iterator_plain_reset,
  sarl_lattice_iterator_plain_reset_last,
  sarl_lattice_iterator_plain_decr_ref,
  sarl_lattice_iterator_plain_copy,

  sarl_lattice_iterator_plain_extent,
  sarl_lattice_iterator_plain_intent
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create_from_context(
    struct Sarl_ContextIterator *context)
{
  struct Sarl_PlainLatticeIterator *result = new Sarl_PlainLatticeIterator;
  sarl_lattice_iterator_init(result, &s_plainLatticeIteratorTable);
  
  result->context = sarl_context_iterator_obtain_ownership(context);
  result->A = 0;
  result->B = 0;
  
  sarl_lattice_iterator_reset(result);
  return result;
};


void 
  sarl_lattice_iterator_plain_reset(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

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
  sarl_lattice_iterator_plain_reset_last(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

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

  it->A = sarl_context_iterator_objects(it->context);
  it->B = sarl_context_iterator_intent_set(it->context, it->A);
};


void 
  sarl_lattice_iterator_plain_next(
     struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

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


void 
  sarl_lattice_iterator_plain_prev(
     struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

  struct Sarl_SetIterator* next_A;
  struct Sarl_SetIterator* next_B;
  
  sarl_set_iterator_release_ownership(it->B);
  next_B = sarl_context_iterator_next_intent(it->context, it->B);

  if ( next_B != 0 ) {
    next_A = sarl_context_iterator_extent_set(it->context, next_B);
  }
  else {
    next_A = 0;
  }

  sarl_set_iterator_decr_ref(it->A);
  sarl_set_iterator_decr_ref(it->B);
  
  it->A = next_A;
  it->B = next_B;
};

int  
  sarl_lattice_iterator_plain_at_end(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

  return it->A == 0;
};

void 
  sarl_lattice_iterator_plain_next_gte(
    struct Sarl_LatticeIterator *a_it,
     struct Sarl_ConceptIterator *c
  )
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

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

void 
  sarl_lattice_iterator_plain_prev_leq(
    struct Sarl_LatticeIterator *a_it,
     struct Sarl_ConceptIterator *c
  )
{
  SARL_NOT_IMPLEMENTED;
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_plain_ideal_from_intent(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator *B)
{
  SARL_NOT_IMPLEMENTED;
	return 0;
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_plain_filter_from_extent(
    struct Sarl_LatticeIterator *a_it,
    struct Sarl_SetIterator *filter)
{
  struct Sarl_PlainLatticeIterator *it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

  return sarl_lattice_iterator_filter_create(
    it->context, it->A, filter);
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_plain_filter(
    struct Sarl_LatticeIterator *it,
    struct Sarl_ConceptIterator *c)
{
  SARL_NOT_IMPLEMENTED;
	return 0;
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_plain_ideal(
    struct Sarl_LatticeIterator *a_it,
    struct Sarl_LatticeIterator *c)
{
  SARL_NOT_IMPLEMENTED;
	return 0;
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_plain_extent(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

  return sarl_set_iterator_copy(it->A);
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_plain_intent(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

  return sarl_set_iterator_copy(it->B);
};

struct Sarl_ConceptIterator *
  sarl_lattice_iterator_plain_value(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

  return sarl_concept_iterator_create(it->A, it->B);
};

void
  sarl_lattice_iterator_plain_decr_ref(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

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

struct Sarl_LatticeIterator*
  sarl_lattice_iterator_plain_copy(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_PlainLatticeIterator* it = 
    static_cast<struct Sarl_PlainLatticeIterator*>(a_it);

  Sarl_PlainLatticeIterator* copy_it =
    new Sarl_PlainLatticeIterator;
  
  sarl_lattice_iterator_init(copy_it, &s_plainLatticeIteratorTable);

  copy_it->A = sarl_set_iterator_copy(it->A);
  copy_it->B = sarl_set_iterator_copy(it->B);
  copy_it->context = sarl_context_iterator_copy(it->context);

  return copy_it;
};


