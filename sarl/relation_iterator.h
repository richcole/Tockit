#ifndef SARL_RELATION_ITERATOR_H
#define SARL_RELATION_ITERATOR_H

#include <sarl/index.h>
#include <sarl/pair.h>

/* iterator macro */
#define SARL_RELATION_ITERATOR_FOR(x) \
  for( \
    sarl_relation_iterator_reset(x);   \
    !sarl_relation_iterator_at_end(x); \
    sarl_relation_iterator_next(x) \
  )

/* set_iterator moving operations */
extern void  sarl_relation_iterator_next_gte(
  struct Sarl_RelationIterator *, 
  struct Sarl_Pair);

extern void  sarl_relation_iterator_next(
  struct Sarl_RelationIterator *);

extern struct Sarl_Pair sarl_relation_iterator_value(
  struct Sarl_RelationIterator *);

extern int   sarl_relation_iterator_at_end(
  struct Sarl_RelationIterator *);

extern void  sarl_relation_iterator_reset(
  struct Sarl_RelationIterator *);

extern void sarl_relation_iterator_decr_ref(
  struct Sarl_RelationIterator *);

extern void sarl_relation_iterator_incr_ref(
  struct Sarl_RelationIterator *);

extern struct Sarl_RelationIterator* sarl_relation_iterator_copy(
  struct Sarl_RelationIterator *);

extern struct Sarl_RelationIterator* sarl_relation_iterator_inverse(
  struct Sarl_RelationIterator *);

/* iterator constructive operations */

extern struct Sarl_SetIterator *sarl_relation_iterator_domain(
  struct Sarl_RelationIterator *);

extern struct Sarl_SetIterator *sarl_relation_iterator_range(
  struct Sarl_RelationIterator *);

extern struct Sarl_SetIterator *sarl_relation_iterator_intent(
  struct Sarl_RelationIterator *, Sarl_Index);

extern struct Sarl_SetIterator *sarl_relation_iterator_extent(
  struct Sarl_RelationIterator *, Sarl_Index);

/* omit ---

extern struct Sarl_SetIterator *sarl_relation_intent_set(
  struct Sarl_RelationIterator *, struct Iterator *
);

extern struct Sarl_SetIterator *sarl_relation_extent_set(
  struct Sarl_RelationIterator *, struct Iterator *
);

extern struct Sarl_SetIterator *sarl_relation_extent_intent_set(
  struct Sarl_RelationIterator *, struct Sarl_SetIterator *
);

extern struct Sarl_SetIterator *sarl_relation_intent_extent_set(
  struct Sarl_RelationIterator *, struct Sarl_SetIterator *
);

*/

/* construction of relation objects */

extern struct Sarl_RelationIterator *sarl_relation_iterator_create(
  struct Sarl_Relation *
);

extern struct Sarl_RelationIterator *sarl_relation_iterator_join(
  struct Sarl_RelationIterator *, struct Sarl_RelationIterator *
);

extern struct Sarl_RelationIterator *sarl_relation_iterator_inverse(
  struct Sarl_RelationIterator *
);





#endif
