#ifndef LATTICE_ITERATOR_IMPL_H
#define LATTICE_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>

struct Sarl_LatticeIterator : Sarl_Iterator {
  struct Sarl_ContextIterator*   context;
  struct Sarl_ConceptIterator*   concept;
};

#endif
