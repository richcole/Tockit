extern "C" {

#include <sarl/global.h>
#include <sarl/concept_iterator.h>
#include <sarl/context_iterator.h>
#include <sarl/lattice_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
}

#include <sarl/iterator_impl.h>  
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
  sarl_lattice_iterator_filter(
    struct Sarl_LatticeIterator *it,
    struct Sarl_ConceptIterator *c)
{
  SARL_NOT_IMPLEMENTED;
  //  it->funcs->filter(it, c);
  return SARL_ERROR;
};


struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal(
    struct Sarl_LatticeIterator *it,
    struct Sarl_LatticeIterator *c)
{
  SARL_NOT_IMPLEMENTED;
  //  it->funcs->ideal(it, c);
  return SARL_ERROR;
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

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_obtain_ownership(
    struct Sarl_LatticeIterator *it
  )
{
  return sarl_iterator_obtain_ownership(it, sarl_lattice_iterator_copy);
};

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_release_ownership(
    struct Sarl_LatticeIterator *it
  )
{
  return sarl_iterator_release_ownership(it);
};

