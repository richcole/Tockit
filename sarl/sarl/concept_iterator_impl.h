#ifndef CONCEPT_ITERATOR_IMPL_H
#define CONCEPT_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>

struct Sarl_ConceptIterator : Sarl_Iterator {
  struct Sarl_SetIterator*   extent;
  struct Sarl_SetIterator*   intent;
};

inline void sarl_concept_iterator_init(
  struct Sarl_ConceptIterator *it)
{
  it->ownership = SARL_HAS_OWNER;
  sarl_ref_count_init(&it->ref_count);
}


#endif
