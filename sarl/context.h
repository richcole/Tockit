#ifndef SARL_CONTEXT_H
#define SARL_CONTEXT_H

#include <sarl/index.h>
#include <sarl/pair.h>

extern struct Sarl_Context *sarl_context_create();
extern struct Sarl_Context *sarl_context_copy(struct Sarl_ContextIterator *);
extern void sarl_context_insert(struct Sarl_Context *, Sarl_Index, Sarl_Index);
extern void sarl_context_remove(struct Sarl_Context *, Sarl_Index, Sarl_Index);

extern struct Sarl_ContextIterator *sarl_context_create_from_relation(
  struct Sarl_RelationIterator *
);

extern struct Sarl_SetIterator *sarl_context_objects(
  struct Sarl_ContextIterator *
);

extern struct Sarl_SetIterator *sarl_context_attributes(
  struct Sarl_ContextIterator *
);

extern struct Sarl_RelationIterator *sarl_context_incidence(
  struct Sarl_ContextIterator *
); 						     

extern void sarl_context_remove_pair(
  struct Sarl_Context *, Sarl_Pair
);

extern void sarl_context_remove_object(
  struct Sarl_Context *, Sarl_Index
);

extern void sarl_context_remove_attribute(
  struct Sarl_Context *, Sarl_Index
);

extern void sarl_context_decr_ref(
	struct Sarl_Context *ap_context
);

extern void sarl_context_incr_ref(
	struct Sarl_Context *ap_context
);

#endif


