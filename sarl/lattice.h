#ifndef SARL_LATTICE_H
#define SARL_LATTICE_H

#include <sarl/index.h>

extern struct Lattice *
  sarl_lattice_copy(struct LatticeIterator *);

extern struct LatticeIterator *
  sarl_lattice_iterator_create(struct Lattice *);

/* reference counting interface */
extern void sarl_lattice_decr_ref(struct Sarl_Lattice *);
extern void sarl_lattice_incr_ref(struct Sarl_Lattice *);


#endif
