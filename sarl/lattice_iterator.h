#ifndef SARL_LATTICE_ITERATOR_H
#define SARL_LATTICE_ITERATOR_H

#include <sarl/index.h>

extern struct Lattice *sarl_lattice_iterator_create(struct Lattice *);

extern void sarl_lattice_iterator_reset(struct LatticeIterator *);
extern int  sarl_lattice_iterator_at_end(struct LatticeIterator *);
extern void sarl_lattice_iterator_next(struct LatticeIterator *);

extern struct LatticeIterator *sarl_lattice_iterator_ideal_from_intent(
  struct Iterator *
);

extern struct LatticeIterator *sarl_lattice_iterator_filter_from_extent(
  struct Iterator *
);

extern struct LatticeIterator *sarl_lattice_iterator_filter(
  struct LatticeIterator *
);

extern struct LatticeIterator *sarl_lattice_iterator_ideal(
  struct LatticeIterator *
);

extern struct Iterator *sarl_lattice_iterator_intent(struct LatticeIterator *);
extern struct Iterator *sarl_lattice_iterator_extent(struct LatticeIterator *);

#endif
