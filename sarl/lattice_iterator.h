#ifndef SARL_LATTICE_ITERATOR_H
#define SARL_LATTICE_ITERATOR_H

#include <sarl/index.h>

struct Sarl_ContextIterator;
struct Sarl_LatticeIterator;
struct Sarl_ConceptIterator;

/* iterator macro */
#define SARL_LATTICE_ITERATOR_FOR(x) \
  for( \
    sarl_lattice_iterator_reset(x);   \
    !sarl_lattice_iterator_at_end(x); \
    sarl_lattice_iterator_next(x) \
  )

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create_from_context(
    struct Sarl_ContextIterator *
  );

extern void 
  sarl_lattice_iterator_reset(
    struct Sarl_LatticeIterator *
  );

extern void
  sarl_lattice_iterator_decr_ref(
    struct Sarl_LatticeIterator *
  );

extern void
  sarl_lattice_iterator_incr_ref(
    struct Sarl_LatticeIterator *
  );

extern int  
  sarl_lattice_iterator_at_end(
    struct Sarl_LatticeIterator *
  );

extern void 
  sarl_lattice_iterator_next(
     struct Sarl_LatticeIterator *
  );

extern void 
  sarl_lattice_iterator_next_gte(
    struct Sarl_LatticeIterator *K,
     struct Sarl_ConceptIterator *c
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal_from_intent(
    struct Sarl_SetIterator *it,
    struct Sarl_SetIterator *B
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter_from_extent(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator *A
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter(
    struct Sarl_LatticeIterator *it
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal(
    struct Sarl_LatticeIterator *it
  );

extern struct Sarl_SetIterator *
  sarl_lattice_iterator_intent(
    struct Sarl_LatticeIterator *it
  );

extern struct Sarl_SetIterator *
  sarl_lattice_iterator_extent(
    struct Sarl_LatticeIterator *it
  );

#endif
