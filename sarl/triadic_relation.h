#ifndef SARL_TRIADIC_RELATION_H
#define SARL_TRIADIC_RELATION_H

#include <sarl/index.h>
#include <sarl/triple.h>
#include <sarl/perm.h>

struct Sarl_TriadicRelationIterator;
struct Sarl_TriadicRelation;

/* construction of triadic_relation objects */
extern struct Sarl_TriadicRelation *sarl_triadic_relation_create();
extern struct Sarl_TriadicRelation *sarl_triadic_relation_copy(
  struct Sarl_TriadicRelationIterator *);

extern void sarl_triadic_relation_insert(
  struct Sarl_TriadicRelation *, Sarl_Index, Sarl_Index, Sarl_Index);

extern void sarl_triadic_relation_remove(
  struct Sarl_TriadicRelation *, Sarl_Index, Sarl_Index, Sarl_Index);

extern void sarl_triadic_relation_insert_triple(
  struct Sarl_TriadicRelation *r, struct Sarl_Triple triple);

extern void sarl_triadic_relation_remove_triple(
  struct Sarl_TriadicRelation *r, struct Sarl_Triple triple);

/* reference counting interface */
extern void sarl_triadic_relation_decr_ref(struct Sarl_TriadicRelation *);
extern void sarl_triadic_relation_incr_ref(struct Sarl_TriadicRelation *);

#endif
