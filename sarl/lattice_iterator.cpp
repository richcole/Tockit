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

void 
  sarl_lattice_iterator_reset(
    struct Sarl_LatticeIterator *it
  )
{
  it->funcs->reset(it);
};


void 
  sarl_lattice_iterator_next(
     struct Sarl_LatticeIterator *it
  )
{
  it->funcs->next(it);
};

int  
  sarl_lattice_iterator_at_end(
    struct Sarl_LatticeIterator *it)
{
  return it->funcs->at_end(it);
};

void 
  sarl_lattice_iterator_next_gte(
    struct Sarl_LatticeIterator *it,
     struct Sarl_ConceptIterator *c
  )
{
  it->funcs->next_gte(it, c);
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
  it->funcs->filter_from_extent(it, A);
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter(
    struct Sarl_LatticeIterator *it,
    struct Sarl_ConceptIterator *c)
{
  SARL_NOT_IMPLEMENTED;
  //  it->funcs->filter(it, c);
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal(
    struct Sarl_LatticeIterator *it,
    struct Sarl_LatticeIterator *c)
{
  SARL_NOT_IMPLEMENTED;
  //  it->funcs->ideal(it, c);
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_extent(
    struct Sarl_LatticeIterator *it)
{
  return it->funcs->extent(it);
};

struct Sarl_SetIterator *
  sarl_lattice_iterator_intent(
    struct Sarl_LatticeIterator *it
  )
{
  return it->funcs->intent(it);
};

struct Sarl_ConceptIterator *
  sarl_lattice_iterator_value(
    struct Sarl_LatticeIterator *it
  )
{
  return it->funcs->value(it);
};

void
  sarl_lattice_iterator_decr_ref(
    struct Sarl_LatticeIterator *it
  )
{
  return it->funcs->decr_ref(it);
};


void
  sarl_lattice_iterator_incr_ref(
    struct Sarl_LatticeIterator *it
  )
{
  sarl_iterator_incr_ref(it);
};

struct Sarl_LatticeIterator*
  sarl_lattice_iterator_copy(
    struct Sarl_LatticeIterator *it
  )
{
  return it->funcs->copy(it);
};


