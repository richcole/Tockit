#ifndef SARL_FILTER_LATTICE_ITERATOR_H
#define SARL_FILTER_LATTICE_ITERATOR_H

#include <sarl/index.h>

struct Sarl_ContextIterator;
struct Sarl_LatticeIterator;
struct Sarl_ConceptIterator;

struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter_create(
    struct Sarl_ContextIterator *context,
    struct Sarl_SetIterator* A,
    struct Sarl_SetIterator* filter);

#endif
