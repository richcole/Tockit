#ifndef SARL_LATTICE_ITERATOR_H
#define SARL_LATTICE_ITERATOR_H

#include <sarl/index.h>

struct Sarl_ContextIterator;
struct Sarl_LatticeIterator;
struct Sarl_ConceptIterator;

/* iterator macro */
#define SARL_LATTICE_ITERATOR_FOR(x) \
  for( \
    sarl_lattice_iterator_reset(x);   \
    !sarl_lattice_iterator_at_end(x); \
    sarl_lattice_iterator_next(x) \
  )

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create_from_context(
    struct Sarl_ContextIterator *
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create(
    struct Sarl_Lattice *
  );

extern void 
  sarl_lattice_iterator_reset(
    struct Sarl_LatticeIterator *
  );

extern void 
  sarl_lattice_iterator_reset_last(
    struct Sarl_LatticeIterator *
  );

extern void
  sarl_lattice_iterator_decr_ref(
    struct Sarl_LatticeIterator *
  );

extern void
  sarl_lattice_iterator_incr_ref(
    struct Sarl_LatticeIterator *
  );

extern int  
  sarl_lattice_iterator_at_end(
    struct Sarl_LatticeIterator *
  );

extern void 
  sarl_lattice_iterator_next(
     struct Sarl_LatticeIterator *
  );

extern void 
  sarl_lattice_iterator_prev(
     struct Sarl_LatticeIterator *
  );

extern Sarl_ConceptIterator*
  sarl_lattice_iterator_value(
     struct Sarl_LatticeIterator *
  );

extern void 
  sarl_lattice_iterator_next_gte(
    struct Sarl_LatticeIterator *K,
     struct Sarl_ConceptIterator *c
  );

extern void 
  sarl_lattice_iterator_prev_leq(
    struct Sarl_LatticeIterator *K,
     struct Sarl_ConceptIterator *c
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_filter(
    struct Sarl_LatticeIterator *it
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_ideal(
    struct Sarl_LatticeIterator *it
  );

extern struct Sarl_SetIterator *
  sarl_lattice_iterator_intent(
    struct Sarl_LatticeIterator *it
  );

extern struct Sarl_SetIterator *
  sarl_lattice_iterator_extent(
    struct Sarl_LatticeIterator *it
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_copy(struct Sarl_LatticeIterator *);

/*! the function will return either _it_, or _it->copy()_. 
 *  In anycase the returned object will have a reference count of
 *  1 and so decr_ref_count, should be called on it.
 */
extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_obtain_ownership(struct Sarl_LatticeIterator *);

/*! the function will lattice the object to be not owned but will not
 *  decrement the reference count. 
 */
extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_release_ownership(struct Sarl_LatticeIterator *);

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create_object_factor(
    struct Sarl_LatticeIterator *L,
    struct Sarl_SetIterator* G_s
  );

extern struct Sarl_LatticeIterator *
  sarl_lattice_iterator_create_attribute_factor(
    struct Sarl_LatticeIterator *L,
    struct Sarl_SetIterator* M_s
  );


#endif
