#ifndef SARL_NARY_RELATION_H
#define SARL_NARY_RELATION_H

#include <sarl/index.h>
#include <sarl/pair.h>

struct Sarl_NaryRelation;
struct Sarl_Vector;

/* construction of nary_relation objects */
extern struct Sarl_NaryRelation *sarl_nary_relation_create(Sarl_Index arity);
extern struct Sarl_NaryRelation *sarl_nary_relation_copy(
  struct Sarl_NaryRelationIterator *);

extern void sarl_nary_relation_insert(
  struct Sarl_NaryRelation *, Sarl_Vector *v);

extern void sarl_nary_relation_remove(
  struct Sarl_NaryRelation *, Sarl_Vector *v);

extern int sarl_nary_relation_is_member(
  struct Sarl_NaryRelation *r, Sarl_Vector *v);

extern Sarl_Index sarl_nary_relation_count(
  struct Sarl_NaryRelation *r);

/* return a set iterator 
extern Sarl_SetIterator sarl_nary_relation_set_iterator();


/* reference counting interface */
extern void sarl_nary_relation_decr_ref(struct Sarl_NaryRelation *);
extern void sarl_nary_relation_incr_ref(struct Sarl_NaryRelation *);

#endif
