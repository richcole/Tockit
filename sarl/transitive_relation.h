#ifndef SARL_TRANSITIVE_RELATION_H
#define SARL_TRANSITIVE_RELATION_H

#include <sarl/index.h>
#include <sarl/pair.h>

struct Sarl_TransitiveRelation;

/* construction of relation objects */
extern struct Sarl_TransitiveRelation *
  sarl_transitive_relation_create();

extern struct Sarl_TransitiveRelation *
  sarl_transitive_relation_copy(
    struct Sarl_TransitiveRelationIterator *
  );

extern void sarl_transitive_relation_insert(
  struct Sarl_TransitiveRelation *, Sarl_Index lower, Sarl_Index upper);

extern void sarl_transitive_relation_remove(
  struct Sarl_TransitiveRelation *, Sarl_Index lower, Sarl_Index upper);

extern void sarl_transitive_relation_insert_pair(
  struct Sarl_TransitiveRelation *r, struct Sarl_Pair pair);

extern void sarl_transitive_relation_remove_pair(
  struct Sarl_TransitiveRelation *r, struct Sarl_Pair pair);

extern void sarl_transitive_relation_remove_extent(
  struct Sarl_TransitiveRelation *r, Sarl_Index index);

extern void sarl_transitive_relation_remove_intent(
  struct Sarl_TransitiveRelation *r, Sarl_Index index);

extern struct Sarl_SetIterator*
  sarl_transitive_relation_upper_covers(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index);

extern struct Sarl_SetIterator*
  sarl_transitive_relation_lower_covers(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index);

extern struct Sarl_SetIterator*
  sarl_transitive_relation_up_set(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index);

extern struct Sarl_SetIterator*
  sarl_transitive_relation_down_set(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index);

extern struct Sarl_SetIterator*
  sarl_transitive_relation_upper_bounds(
    struct Sarl_TransitiveRelation* r, 
    Sarl_SetIterator* index);

extern struct Sarl_SetIterator*
  sarl_transitive_relation_lower_bounds(
    struct Sarl_TransitiveRelation* r, 
    Sarl_SetIterator* index);


/* reference counting interface */
extern void sarl_transitive_relation_decr_ref(
  struct Sarl_TransitiveRelation *);

extern void sarl_transitive_relation_incr_ref(
  struct Sarl_TransitiveRelation *);

#endif
