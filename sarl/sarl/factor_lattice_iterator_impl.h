#ifndef SARL_FACTOR_LATTICE_ITERATOR_IMPL_H
#define SARL_FACTOR_LATTICE_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>
#include <sarl/lattice_iterator_impl.h>

enum Sarl_FactorType { 
  SARL_FT_ATTRIBUTE,  /* factoring is done on attributes */
  SARL_FT_OBJECT      /* factoring is done on objects */
};

struct Sarl_FactorLatticeIterator : Sarl_LatticeIterator {
  struct Sarl_LatticeIterator*   L;     /* lattice being factored */
  struct Sarl_SetIterator*       A;     /* factor attributes or objects */
  struct Sarl_SetIterator*       curr;  /* current */
  enum   Sarl_FactorType         factor_type;
};

#endif
