#ifndef SARL_LATTICE_H
#define SARL_LATTICE_H

#include <sarl/index.h>

extern struct Lattice *sarl_lattice_create(struct ContextIterator *);
extern struct Lattice *sarl_lattice_copy(struct LatticeIterator *);

#endif
