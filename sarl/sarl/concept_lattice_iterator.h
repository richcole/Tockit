#ifndef SARL_CONCEPT_LATTICE_ITERATOR_H
#define SARL_CONCEPT_LATTICE_ITERATOR_H

/* This interface is unsatifactory because it doesn't let us 
   consider subsets of the lattice as sets.
*/

#include <sarl/index.h>

struct Sarl_ConceptLatticeIterator;
struct Sarl_ConceptIterator;

struct Sarl_SetIterator *
  sarl_concept_iterator_extent
(
	struct Sarl_ConceptIterator *
);

struct Sarl_SetIterator *
  sarl_concept_iterator_intent
(
	struct Sarl_ConceptIterator *
);

struct Sarl_ConceptLatticeIterator *
  sarl_concept_lattice_iterator_create
(
	struct Sarl_ContextIterator *
);

struct Sarl_ConceptIterator*
  sarl_concept_lattice_iterator_val
(
  struct Sarl_ConceptLatticeIterator *
);

void
  sarl_concept_lattice_iterator_next
(
  struct Sarl_ConceptLatticeIterator *
);

void
  sarl_concept_lattice_iterator_prev
(
  struct Sarl_ConceptLatticeIterator *
);

void
  sarl_concept_lattice_iterator_next_gte
(
  struct Sarl_ConceptLatticeIterator *,
  struct Sarl_ConceptIterator *
);

void
  sarl_concept_lattice_iterator_prev_leq
(
  struct Sarl_ConceptLatticeIterator *,
  struct Sarl_ConceptIterator *
);

bool
  sarl_concept_lattice_iterator_at_end
(
  struct Sarl_ConceptLatticeIterator *
);

/*!
 * Find the next concept which is below the concept indicated by 
 * ConceptIterator 
 */
void
  sarl_concept_lattice_iterator_next_below
(
  struct Sarl_ConceptLatticeIterator *,
  struct Sarl_ConceptIterator *
);


void
  sarl_concept_lattice_iterator_prev_above
(
  struct Sarl_ConceptLatticeIterator *,
  struct Sarl_ConceptIterator *
);


#endif

