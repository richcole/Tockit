#ifndef SARL_CONCEPT_ITERATOR_H
#define SARL_CONCEPT_ITERATOR_H

#include <sarl/index.h>

struct Sarl_ConceptLatticeIterator;
struct Sarl_ConceptIterator;

extern struct Sarl_ConceptIterator*
  sarl_concept_iterator_create(
    struct Sarl_SetIterator *extent,
    struct Sarl_SetIterator *intent
  );

extern struct Sarl_SetIterator*
  sarl_concept_iterator_extent(
    struct Sarl_ConceptIterator *
  );

extern struct Sarl_SetIterator*
  sarl_concept_iterator_intent(
    struct Sarl_ConceptIterator *
  );

extern int
  sarl_concept_iterator_decr_ref(
    struct Sarl_ConceptIterator *
  );

extern void
  sarl_concept_iterator_incr_ref(
    struct Sarl_ConceptIterator *
  );

extern struct Sarl_SetIterator*
  sarl_concept_iterator_obtain_ownership(
    struct Sarl_ConceptIterator *
  );

extern void
  sarl_concept_iterator_release_ownership(
    struct Sarl_ConceptIterator *
  );

#endif
