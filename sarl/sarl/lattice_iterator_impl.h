#ifndef LATTICE_ITERATOR_IMPL_H
#define LATTICE_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>

struct Sarl_ConceptIterator;
struct Sarl_LatticeIterator;
struct Sarl_SetIterator;

struct Sarl_LatticeIteratorFunctionTable {
  void  (*next_gte)(Sarl_LatticeIterator *, Sarl_ConceptIterator *);
  void  (*prev_leq)(Sarl_LatticeIterator *, Sarl_ConceptIterator *);
  void  (*next)(Sarl_LatticeIterator *);
  void  (*prev)(Sarl_LatticeIterator *);
  Sarl_ConceptIterator* (*value)(Sarl_LatticeIterator *);
  int   (*at_end)(Sarl_LatticeIterator *);
  void  (*reset)(Sarl_LatticeIterator *);
  void  (*reset_last)(Sarl_LatticeIterator *);
  void  (*decr_ref)(Sarl_LatticeIterator *);
  Sarl_LatticeIterator*  (*copy)(Sarl_LatticeIterator *);

  Sarl_SetIterator* (*extent)(Sarl_LatticeIterator *);
  Sarl_SetIterator* (*intent)(Sarl_LatticeIterator *);

  Sarl_LatticeIterator* (*upper_covers)(Sarl_LatticeIterator *);
  Sarl_LatticeIterator* (*lower_covers)(Sarl_LatticeIterator *);

  Sarl_LatticeIterator* (*filter)(Sarl_LatticeIterator *);
  Sarl_LatticeIterator* (*ideal)(Sarl_LatticeIterator *);
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

struct Sarl_CachedLatticeIterator : Sarl_LatticeIterator {
  struct Sarl_Lattice*           lattice;
  struct Sarl_SetIterator*       concept;
};

inline void sarl_lattice_iterator_init(
  struct Sarl_LatticeIterator *it, 
  struct Sarl_LatticeIteratorFunctionTable *ap_funcs)
{
  sarl_iterator_init(it);
  it->funcs = ap_funcs;
}

#endif
