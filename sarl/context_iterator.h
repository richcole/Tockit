#ifndef SARL_CONTEXT_ITERATOR_H
#define SARL_CONTEXT_ITERATOR_H

#include <sarl/index.h>
#include <sarl/pair.h>

extern struct Sarl_ContextIterator *
  sarl_context_iterator_create
(
  struct Sarl_Context*
);

extern struct Sarl_ContextIterator *
  sarl_context_iterator_create_from_relation
(
  struct Sarl_RelationIterator*
);

extern struct Sarl_ContextIterator *
  sarl_context_iterator_copy
(
  struct Sarl_SetIterator*      G_it,
  struct Sarl_SetIterator*      M_it,
  struct Sarl_RelationIterator* I_it
);

extern struct Sarl_ContextIterator *
  sarl_context_iterator_obtain_ownership(
    struct Sarl_ContextIterator* it
  );

extern struct Sarl_ContextIterator *
  sarl_context_iterator_release_ownership(
    struct Sarl_ContextIterator* it
  );

extern struct Sarl_ContextIterator *
  sarl_context_iterator_copy
(
  struct Sarl_SetIterator*      G_it,
  struct Sarl_SetIterator*      M_it,
  struct Sarl_RelationIterator* I_it
);

extern struct Sarl_SetIterator *
  sarl_context_iterator_objects(
    struct Sarl_ContextIterator *
  );

extern struct Sarl_SetIterator *
  sarl_context_iterator_attributes(
    struct Sarl_ContextIterator *
  );

extern struct Sarl_RelationIterator *
  sarl_context_iterator_incidence(
    struct Sarl_ContextIterator *
  ); 						     

extern struct Sarl_ContextIterator *
  sarl_context_iterator_inverse(
    struct Sarl_ContextIterator *
  );

extern struct Sarl_ContextIterator *
  sarl_context_iterator_complement(
    struct Sarl_ContextIterator *
  );

extern struct Sarl_RelationIterator*
   sarl_context_iterator_up_arrow(
     struct SarlContextIterator *
   );

extern struct Sarl_RelationIterator*
  sarl_context_iterator_down_arrow(
    struct SarlContextIterator *
  );

extern struct Sarl_RelationIterator*
  sarl_context_iterator_updown_arrow(
    struct SarlContextIterator *
  );

extern struct Sarl_SetIterator *
  sarl_context_iterator_intent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *A
  );

extern struct Sarl_SetIterator *
  sarl_context_iterator_extent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *B
  );

extern struct Sarl_SetIterator *
  sarl_context_iterator_intent_extent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *A
  );

extern struct Sarl_SetIterator *
  sarl_context_iterator_extent_intent_set(
    struct Sarl_ContextIterator *K, 
    struct Sarl_SetIterator *B
  );

extern struct Sarl_SetIterator* 
  sarl_context_iterator_next_extent(
    Sarl_ContextIterator *K,
    Sarl_SetIterator     *A
  );

struct Sarl_SetIterator* 
  sarl_context_iterator_next_extent_superseteq(
    Sarl_ContextIterator *K,
    Sarl_SetIterator     *A, 
    Sarl_SetIterator     *parent_extent
  );

/* reference counting interface */
extern void 
  sarl_context_iterator_decr_ref(
    struct Sarl_ContextIterator *it
  );

extern void 
  sarl_context_iterator_incr_ref(
    struct Sarl_ContextIterator *it
  );


#endif
