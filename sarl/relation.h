#ifndef SARL_RELATION_H
#define SARL_RELATION_H

#include <sarl/index.h>

/* construction of relation objects */
extern struct Relation *sarl_relation_create();
extern struct Relation *sarl_relation_copy(RelationIterator *);
extern void sarl_relation_insert(struct Relation *, Index, Index);
extern void sarl_relation_remove(struct Relation *, Index, Index);

/* iterator constructive operations */
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

#endif
