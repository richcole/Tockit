#ifndef SARL_RELATION_H
#define SARL_RELATION_H

#include <sarl/index.h>
#include <sarl/pair.h>

struct RelationIterator;
struct Relation;

/* construction of relation objects */
extern struct Relation *sarl_relation_create();
extern struct Relation *sarl_relation_copy(struct RelationIterator *);

extern void sarl_relation_insert(struct Relation *, Index, Index);
extern void sarl_relation_remove(struct Relation *, Index, Index);

extern void sarl_relation_insert_pair(struct Relation *r, struct Pair pair);
extern void sarl_relation_remove_pair(struct Relation *r, struct Pair pair);

/* reference counting interface */
extern void sarl_relation_decr_ref(struct Relation *);
extern void sarl_relation_incr_ref(struct Relation *);

#endif
