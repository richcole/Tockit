#ifndef SARL_CONCEPT_ITERATOR_H
#define SARL_CONCEPT_ITERATOR_H

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

#endif
