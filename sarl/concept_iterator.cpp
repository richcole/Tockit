extern "C" {

#include <sarl/concept_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/concept_iterator_impl.h>
#include <sarl/test.h>

struct Sarl_SetIterator*
  sarl_concept_iterator_extent(
    struct Sarl_ConceptIterator *c
  )
{
  return sarl_set_iterator_copy(c->extent);
};

struct Sarl_SetIterator*
  sarl_concept_iterator_intent(
    struct Sarl_ConceptIterator *c
  )
{
  return sarl_set_iterator_copy(c->intent);
};

struct Sarl_ConceptIterator*
  sarl_concept_iterator_create(
    struct Sarl_SetIterator *extent,
    struct Sarl_SetIterator *intent
  )
{
  Sarl_ConceptIterator const* it = new Sarl_ConceptIterator();
  sarl_concept_iterator_init(it);
  
  it->extent = sarl_set_iterator_obtain_ownership(extent);
  it->intent = sarl_set_iterator_obtain_ownership(intent);

  return it;
};


int
  sarl_concept_iterator_decr_ref(
    struct Sarl_ConceptIterator *
  )
{
  /* not implemented */
};


void
  sarl_concept_iterator_incr_ref(
    struct Sarl_ConceptIterator *
  )
{
};


struct Sarl_SetIterator*
  sarl_concept_iterator_obtain_ownership(
    struct Sarl_ConceptIterator *
  )
{
};


void
  sarl_concept_iterator_release_ownership(
    struct Sarl_ConceptIterator *
  )
{
};

