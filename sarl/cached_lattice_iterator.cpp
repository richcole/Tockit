extern "C" {

#include <sarl/config.h>
#include <sarl/concept_iterator.h>
#include <sarl/context_iterator.h>
#include <sarl/lattice_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>

#include <sarl/lattice.h>
#include <sarl/filter_lattice_iterator.h>
#include <sarl/relation_iterator.h>
}

#include <sarl/concept_iterator_impl.h>
#include <sarl/lattice_iterator_impl.h>
#include <sarl/lattice_impl.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

static void sarl_lattice_iterator_cached_next_gte(
  struct Sarl_LatticeIterator *it, 
  struct Sarl_ConceptIterator* value);

static void sarl_lattice_iterator_cached_next(
  struct Sarl_LatticeIterator *it);

static Sarl_ConceptIterator* sarl_lattice_iterator_cached_value(
  struct Sarl_LatticeIterator *it);

static int sarl_lattice_iterator_cached_at_end(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_cached_reset(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_cached_decr_ref(
  struct Sarl_LatticeIterator *it);

static struct Sarl_LatticeIterator* sarl_lattice_iterator_cached_copy(
  struct Sarl_LatticeIterator *it);

static struct Sarl_SetIterator* 
  sarl_lattice_iterator_cached_extent(
    struct Sarl_LatticeIterator *it
  );

static struct Sarl_SetIterator* 
  sarl_lattice_iterator_cached_intent(
    struct Sarl_LatticeIterator *it
  );

static struct Sarl_LatticeIterator* 
  sarl_lattice_iterator_cached_filter_from_extent(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator* filter
  );

/* function prototypes used in function table declaired below */

struct Sarl_LatticeIteratorFunctionTable s_CachedLatticeIteratorTable = 
{
  sarl_lattice_iterator_cached_next_gte,
  sarl_lattice_iterator_cached_next,
  sarl_lattice_iterator_cached_value,
  sarl_lattice_iterator_cached_at_end,
  sarl_lattice_iterator_cached_reset,
  sarl_lattice_iterator_cached_decr_ref,
  sarl_lattice_iterator_cached_copy,

  sarl_lattice_iterator_cached_extent,
  sarl_lattice_iterator_cached_intent
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_cached_create(
    struct Sarl_Lattice *lattice)
{
  struct Sarl_CachedLatticeIterator *result = new Sarl_CachedLatticeIterator;
  sarl_lattice_iterator_init(result, &s_CachedLatticeIteratorTable);
  
  result->lattice = lattice;
  sarl_lattice_incr_ref(lattice);

  Sarl_RelationIterator *r_intent_it = 
    sarl_relation_iterator_create(result->lattice->intent);

  Sarl_RelationIterator *r_extent_it = 
    sarl_relation_iterator_create(result->lattice->extent);

  sarl_relation_iterator_release_ownership(r_intent_it);
  sarl_relation_iterator_release_ownership(r_extent_it);

  Sarl_SetIterator* intent_it = 
    sarl_relation_iterator_domain(r_intent_it);

  Sarl_SetIterator* extent_it = 
    sarl_relation_iterator_domain(r_extent_it);

  sarl_relation_iterator_decr_ref(r_intent_it);
  sarl_relation_iterator_decr_ref(r_extent_it);

  sarl_set_iterator_release_ownership(intent_it);
  sarl_set_iterator_release_ownership(extent_it);
  
  result->concept = sarl_set_iterator_union(intent_it, extent_it);

  sarl_set_iterator_decr_ref(intent_it);
  sarl_set_iterator_decr_ref(extent_it);
  
  return result;
};


void 
  sarl_lattice_iterator_cached_reset(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_CachedLatticeIterator* it = 
   static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  sarl_set_iterator_reset(it->concept);
};


void 
  sarl_lattice_iterator_cached_next(
     struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_CachedLatticeIterator* it = 
   static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  sarl_set_iterator_next(it->concept);
};

int  
  sarl_lattice_iterator_cached_at_end(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_CachedLatticeIterator* it = 
   static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  return sarl_set_iterator_at_end(it->concept);
};

void 
  sarl_lattice_iterator_cached_next_gte(
    struct Sarl_LatticeIterator *a_it,
     struct Sarl_ConceptIterator *c
  )
{
  Sarl_CachedLatticeIterator* it = 
   static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  Sarl_SetIterator* B;
  Sarl_SetIterator* B_i;
  
  B = sarl_concept_iterator_intent(c);
  sarl_set_iterator_release_ownership(B);
  B =  sarl_relation_extent_set(it->lattice->intent, B); 

  if ( ! sarl_set_iterator_at_end(B_i) ) {
    sarl_set_iterator_next_gte(it->concept, sarl_set_iterator_value(B_i));
  }
  sarl_set_iterator_decr_ref(B);
  sarl_set_iterator_decr_ref(B_i);
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_cached_ideal_from_intent(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator *B)
{
  SARL_NOT_IMPLEMENTED;
  return 0;
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_cached_filter_from_extent(
    struct Sarl_LatticeIterator *a_it,
    struct Sarl_SetIterator *filter)
{
  SARL_NOT_IMPLEMENTED;
  return SARL_ERROR;
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_cached_filter(
    struct Sarl_LatticeIterator *it,
    struct Sarl_ConceptIterator *c)
{
  SARL_NOT_IMPLEMENTED;
  return SARL_ERROR;
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_cached_ideal(
    struct Sarl_LatticeIterator *a_it,
    struct Sarl_LatticeIterator *c)
{
  SARL_NOT_IMPLEMENTED;
  return SARL_ERROR;
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_cached_extent(
    struct Sarl_LatticeIterator *a_it)
{ 
  Sarl_CachedLatticeIterator* it = 
   static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  return sarl_relation_intent(
    it->lattice->extent, 
    sarl_set_iterator_value(it->concept)
  );
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_cached_intent(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_CachedLatticeIterator* it = 
    static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  return sarl_relation_intent(
    it->lattice->intent, 
    sarl_set_iterator_value(it->concept)
  );
};

struct Sarl_ConceptIterator *
  sarl_lattice_iterator_cached_value(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_CachedLatticeIterator* it = 
    static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  Sarl_SetIterator* A = sarl_lattice_iterator_extent(it);
  Sarl_SetIterator* B = sarl_lattice_iterator_intent(it);
  Sarl_ConceptIterator* result;
  
  sarl_set_iterator_release_ownership(A);
  sarl_set_iterator_release_ownership(B);
  result = sarl_concept_iterator_create(A, B);

  sarl_set_iterator_decr_ref(A);
  sarl_set_iterator_decr_ref(B);

  return result;
};

void
  sarl_lattice_iterator_cached_decr_ref(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_CachedLatticeIterator* it = 
    static_cast<struct Sarl_CachedLatticeIterator*>(a_it);

  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_lattice_decr_ref(it->lattice);
    sarl_set_iterator_decr_ref(it->concept);
    delete it;
  }
};

struct Sarl_LatticeIterator*
  sarl_lattice_iterator_cached_copy(
    struct Sarl_LatticeIterator *a_it
  )
{
  SARL_NOT_IMPLEMENTED;
  return SARL_ERROR;
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create(
    struct Sarl_Lattice *lattice)
{
  return sarl_lattice_iterator_cached_create(lattice);
};


