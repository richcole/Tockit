#ifndef SARL_LATTICE_H
#define SARL_LATTICE_H

#include <sarl/index.h>

extern struct Sarl_Lattice *
  sarl_lattice_copy(struct Sarl_LatticeIterator *);

extern struct Sarl_LatticeIterator *
  sarl_lattice_create(struct Sarl_Lattice *);

extern struct Sarl_SetIterator *
  sarl_lattice_concepts(struct Sarl_Lattice* );

extern struct Sarl_RelationIterator *
  sarl_lattice_covering(struct Sarl_Lattice* );

extern struct Sarl_RelationIterator *
  sarl_lattice_ordering(struct Sarl_Lattice* );

extern struct Sarl_RelationIterator *
  sarl_lattice_extent(struct Sarl_Lattice* );

extern struct Sarl_RelationIterator *
  sarl_lattice_intent(struct Sarl_Lattice* );

extern struct Sarl_SetIterator *
  sarl_lattice_concept_extent(struct Sarl_Lattice* L, Sarl_Index concept);

extern struct Sarl_SetIterator *
  sarl_lattice_concept_intent(struct Sarl_Lattice* L, Sarl_Index concept);

extern struct Sarl_RelationIterator *
  sarl_lattice_object_contingent(struct Sarl_Lattice* );

extern struct Sarl_RelationIterator *
  sarl_lattice_attribute_contingent(struct Sarl_Lattice* );

/* reference counting interface */

extern void sarl_lattice_decr_ref(struct Sarl_Lattice *);
extern void sarl_lattice_incr_ref(struct Sarl_Lattice *);


#endif
