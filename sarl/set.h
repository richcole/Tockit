#ifndef SARL_SET_H
#define SARL_SET_H

#include <sarl/index.h>

struct Set;
struct SetIterator;

/* construction of set objects */
extern struct Set *sarl_set_create();
extern struct Set *sarl_set_copy(struct SetIterator *);
extern void sarl_set_insert(struct Set *, Index);
extern void sarl_set_remove(struct Set *, Index);

/* reference counting interface */
extern void sarl_set_decr_ref(struct Set *);
extern void sarl_set_incr_ref(struct Set *);

#endif
