#ifndef SARL_LATTICE_H
#define SARL_LATTICE_H

#include <sarl/index.h>

extern struct Sarl_Lattice *
  sarl_lattice_copy(struct Sarl_LatticeIterator *);

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create(struct Sarl_Lattice *);

/* reference counting interface */
extern void sarl_lattice_decr_ref(struct Sarl_Lattice *);
extern void sarl_lattice_incr_ref(struct Sarl_Lattice *);


#endif
