#ifndef LATTICE_ITERATOR_IMPL_H
#define LATTICE_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>

struct Sarl_LatticeIteratorFunctionTable {
  void  (*next_gte)(struct Sarl_LatticeIterator *, Sarl_ConceptIterator *);
  void  (*next)(struct Sarl_LatticeIterator *);
  struct Sarl_ConceptIterator* (*value)(struct Sarl_LatticeIterator *);
  int   (*at_end)(struct Sarl_LatticeIterator *);
  void  (*reset)(struct Sarl_LatticeIterator *);
  void  (*decr_ref)(struct Sarl_LatticeIterator *);
  struct Sarl_LatticeIterator*  (*copy)(struct Sarl_LatticeIterator *);

  struct Sarl_SetIterator* (*extent)(struct Sarl_LatticeIterator *);
  struct Sarl_SetIterator* (*intent)(struct Sarl_LatticeIterator *);

  struct Sarl_LatticeIterator* (*filter_from_extent)(
    struct Sarl_LatticeIterator *it,
    struct Sarl_SetIterator *filter
  );
};

struct Sarl_LatticeIterator : Sarl_Iterator 
{
  Sarl_LatticeIteratorFunctionTable* funcs;
};

struct Sarl_PlainLatticeIterator : Sarl_LatticeIterator {
  struct Sarl_ContextIterator*   context;
  struct Sarl_SetIterator*       A; /* objects    */
  struct Sarl_SetIterator*       B; /* attributes */
};

struct Sarl_FilterLatticeIterator : Sarl_LatticeIterator {
  struct Sarl_ContextIterator*   context;
  struct Sarl_SetIterator*       A; /* objects       */
  struct Sarl_SetIterator*       B; /* attributes    */
  struct Sarl_SetIterator*       filter; /* extent filter */
};

inline void sarl_plain_lattice_iterator_init(
  struct Sarl_LatticeIterator *it, 
  struct Sarl_LatticeIteratorFunctionTable *ap_funcs)
{
  sarl_iterator_init(it);
  it->funcs = ap_funcs;
}

#endif
