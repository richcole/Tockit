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
  struct RelationIterator *, 
  struct Pair);

extern void  sarl_relation_iterator_next(
  struct RelationIterator *);

extern struct Pair sarl_relation_iterator_val(
  struct RelationIterator *);

extern int   sarl_relation_iterator_at_end(
  struct RelationIterator *);

extern void  sarl_relation_iterator_reset(
  struct RelationIterator *);

/* iterator constructive operations */

/* omit ---

extern struct Iterator *sarl_relation_domain(struct RelationIterator *);
extern struct Iterator *sarl_relation_range(struct RelationIterator *);
extern struct Iterator *sarl_relation_intent(struct RelationIterator *, Index);
extern struct Iterator *sarl_relation_extent(struct RelationIterator *, Index);

extern struct Iterator *sarl_relation_intent_set(
  struct RelationIterator *, struct Iterator *
);

extern struct Iterator *sarl_relation_extent_set(
  struct RelationIterator *, struct Iterator *
);

extern struct Iterator *sarl_relation_extent_intent_set(
  struct RelationIterator *, struct Iterator *
);

extern struct Iterator *sarl_relation_intent_extent_set(
  struct RelationIterator *, struct Iterator *
);

/*

/* construction of relation objects */

/* omit ---

extern struct RelationIterator *sarl_relation_iterator_create(
  struct Relation *
);

extern struct RelationIterator *sarl_relation_iterator_join(
  struct RelationIterator *, struct RelationIterator *
);

extern struct RelationIterator *sarl_relation_iterator_inverse(
  struct RelationIterator *
);

-- omit */

#endif
