#ifndef SARL_RELATION_H
#define SARL_RELATION_H

#include <sarl/index.h>
#include <sarl/pair.h>

struct Sarl_RelationIterator;
struct Sarl_Relation;

/* construction of relation objects */
extern struct Sarl_Relation *sarl_relation_create();
extern struct Sarl_Relation *sarl_relation_copy(struct Sarl_RelationIterator *);

extern void sarl_relation_insert(struct Sarl_Relation *, Sarl_Index, Sarl_Index);
extern void sarl_relation_remove(struct Sarl_Relation *, Sarl_Index, Sarl_Index);

extern void sarl_relation_insert_pair(struct Sarl_Relation *r, struct Sarl_Pair pair);
extern void sarl_relation_remove_pair(struct Sarl_Relation *r, struct Sarl_Pair pair);

/* reference counting interface */
extern void sarl_relation_decr_ref(struct Sarl_Relation *);
extern void sarl_relation_incr_ref(struct Sarl_Relation *);

#endif
