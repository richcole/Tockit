#ifndef LATTICE_ITERATOR_IMPL_H
#define LATTICE_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>

struct Sarl_LatticeIterator : Sarl_Iterator {
  struct Sarl_ContextIterator*   context;
  struct Sarl_SetIterator*       A; /* objects    */
  struct Sarl_SetIterator*       B; /* attributes */
};

inline void sarl_lattice_iterator_init(
  struct Sarl_LatticeIterator *it)
{
  sarl_iterator_init(it);
}

#endif
