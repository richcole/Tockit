#ifndef SARL_LATTICE_ITERATOR_H
#define SARL_LATTICE_ITERATOR_H

#include <sarl/index.h>

struct Sarl_ContextIterator;
struct Sarl_LatticeIterator;

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create(
    struct Sarl_ContextIterator*
  );

extern void 
  sarl_lattice_iterator_reset(
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

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal_from_intent(
    struct Iterator *
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter_from_extent(
    struct Iterator *
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter(
    struct Sarl_LatticeIterator *
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal(
  struct Sarl_LatticeIterator *
);

extern struct Iterator *
  sarl_lattice_iterator_intent(
    struct Sarl_LatticeIterator *
  );

extern struct Iterator *
  sarl_lattice_iterator_extent(
    struct Sarl_LatticeIterator *
  );

#endif
