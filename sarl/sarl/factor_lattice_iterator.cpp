extern "C" {

#include <sarl/global.h>
#include <sarl/concept_iterator.h>
#include <sarl/context_iterator.h>
#include <sarl/lattice_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>

#include <sarl/filter_lattice_iterator.h>
  
}

#include <sarl/factor_lattice_iterator_impl.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

static void sarl_lattice_iterator_factor_next_gte(
  struct Sarl_LatticeIterator *it, 
  struct Sarl_ConceptIterator* value);

static void sarl_lattice_iterator_factor_next(
  struct Sarl_LatticeIterator *it);

static Sarl_ConceptIterator* sarl_lattice_iterator_factor_value(
  struct Sarl_LatticeIterator *it);

static int sarl_lattice_iterator_factor_at_end(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_factor_reset(
  struct Sarl_LatticeIterator *it);

static void sarl_lattice_iterator_factor_decr_ref(
  struct Sarl_LatticeIterator *it);

static struct Sarl_LatticeIterator* sarl_lattice_iterator_factor_copy(
  struct Sarl_LatticeIterator *it);

static struct Sarl_SetIterator* 
  sarl_lattice_iterator_factor_extent(
    struct Sarl_LatticeIterator *it
  );

static struct Sarl_SetIterator* 
  sarl_lattice_iterator_factor_intent(
    struct Sarl_LatticeIterator *it
  );

/* function prototypes used in function table declaired below */

struct Sarl_LatticeIteratorFunctionTable s_factorLatticeIteratorTable = 
{
  sarl_lattice_iterator_factor_next_gte,
  sarl_lattice_iterator_factor_next,
  sarl_lattice_iterator_factor_value,
  sarl_lattice_iterator_factor_at_end,
  sarl_lattice_iterator_factor_reset,
  sarl_lattice_iterator_factor_decr_ref,
  sarl_lattice_iterator_factor_copy,

  sarl_lattice_iterator_factor_extent,
  sarl_lattice_iterator_factor_intent
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create_object_factor(
    struct Sarl_LatticeIterator *L, 
    struct Sarl_SetIterator *G_s
  )
{
  struct Sarl_FactorLatticeIterator *result = new Sarl_FactorLatticeIterator;
  sarl_lattice_iterator_init(result, &s_factorLatticeIteratorTable);
  
  result->L = sarl_lattice_iterator_obtain_ownership(L);
  result->A = sarl_set_iterator_obtain_ownership(G_s);
  result->factor_type = SARL_FT_OBJECT;
  
  sarl_lattice_iterator_reset(result);
  return result;
};


void 
  sarl_lattice_iterator_factor_reset(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  sarl_lattice_iterator_reset(it->L);
  sarl_set_iterator_reset(it->A);
};


void 
  sarl_lattice_iterator_factor_next(
     struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  /* advance to the next extent based on extent */

  Sarl_SetIterator *prev_extent    = 0;
  Sarl_SetIterator *curr_extent    = 0;

  // get the current intent
  prev_extent = sarl_lattice_iterator_extent(it);
  sarl_set_iterator_release_ownership(prev_extent);

  bool finished = false;
  while( ! sarl_lattice_iterator_at_end(it->L) && ! finished ) {
    sarl_lattice_iterator_next(it->L);
    curr_extent = sarl_lattice_iterator_extent(it);
    sarl_set_iterator_release_ownership(curr_extent);
    if ( ! sarl_set_iterator_subseteq(curr_extent, prev_extent) ) {
      finished = true;
    };
    sarl_set_iterator_decr_ref(curr_extent);
  };

  sarl_set_iterator_decr_ref(prev_extent);
};

int  
  sarl_lattice_iterator_factor_at_end(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  return sarl_lattice_iterator_at_end(it->L); 
};

void 
  sarl_lattice_iterator_factor_next_gte(
    struct Sarl_LatticeIterator *a_it,
     struct Sarl_ConceptIterator *c
  )
{
  // Sarl_FactorLatticeIterator* it = 
  //   static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  // maybe the following will work: needs a proof
  // advance the lattice iterator
  // sarl_lattice_iterator_next_gte(it->L, c);

  SARL_NOT_IMPLEMENTED;
};


struct Sarl_SetIterator *
  sarl_lattice_iterator_factor_extent(
    struct Sarl_LatticeIterator *a_it)
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  Sarl_SetIterator* L_extent;
  Sarl_SetIterator* result;

  L_extent =   sarl_lattice_iterator_extent(it->L);
  sarl_set_iterator_release_ownership(L_extent);
  result = sarl_set_iterator_meet(L_extent, it->A);
  sarl_set_iterator_decr_ref(L_extent);
  
  return result;
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_factor_intent(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  return sarl_lattice_iterator_intent(it->L);
};

struct Sarl_ConceptIterator *
  sarl_lattice_iterator_factor_value(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  Sarl_SetIterator *intent = sarl_lattice_iterator_intent(it);
  Sarl_SetIterator *extent = sarl_lattice_iterator_extent(it);
  Sarl_ConceptIterator *result = 0;
  
  sarl_set_iterator_release_ownership(intent);
  sarl_set_iterator_release_ownership(extent);
  result = sarl_concept_iterator_create(intent, extent);
  sarl_set_iterator_decr_ref(intent);
  sarl_set_iterator_decr_ref(extent);
  
  return result;
};

void
  sarl_lattice_iterator_factor_decr_ref(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  if ( sarl_ref_count_decr(&it->ref_count) ) {
    if (it->L) {
      sarl_lattice_iterator_release_ownership(it->L);
      sarl_lattice_iterator_decr_ref(it->L);
    }
    if (it->A) {
      sarl_set_iterator_release_ownership(it->A);
      sarl_set_iterator_decr_ref(it->A);
    }
    delete it;
  }
};

struct Sarl_LatticeIterator*
  sarl_lattice_iterator_factor_copy(
    struct Sarl_LatticeIterator *a_it
  )
{
  Sarl_FactorLatticeIterator* it = 
    static_cast<struct Sarl_FactorLatticeIterator*>(a_it);

  Sarl_FactorLatticeIterator* copy_it =
    new Sarl_FactorLatticeIterator;
  
  sarl_lattice_iterator_init(copy_it, &s_factorLatticeIteratorTable);

  copy_it->L = sarl_lattice_iterator_copy(it->L);
  copy_it->A = sarl_set_iterator_copy(it->A);

  return copy_it;
};


