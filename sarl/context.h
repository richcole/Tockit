#ifndef SARL_CONTEXT_H
#define SARL_CONTEXT_H

#include <sarl/index.h>

extern struct Context *sarl_context_create();
extern struct Context *sarl_context_copy(struct ContextIterator *);
extern void sarl_context_insert(struct Context *, index, index);
extern void sarl_context_remove(struct Context *, index, index);

extern struct ContextIterator *sarl_context_create_from_relation(
  struct RelationIterator *
);

extern struct Iterator *sarl_context_objects(
  struct ContextIterator *
);

extern struct Iterator *sarl_context_attributes(
  struct ContextIterator *
);

extern struct RelationIterator *sarl_context_incidence(
  struct ContextIterator *
); 						     

#endif
