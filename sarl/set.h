#ifndef SARL_SET_H
#define SARL_SET_H

#include <sarl/index.h>

struct Sarl_Set;
struct Sarl_SetIterator;

/* construction of set objects */
extern struct Sarl_Set *sarl_set_create();
extern struct Sarl_Set *sarl_set_copy(struct Sarl_SetIterator *);
extern void sarl_set_insert(struct Sarl_Set *, Sarl_Index);
extern void sarl_set_remove(struct Sarl_Set *, Sarl_Index);

/* reference counting interface */
extern void sarl_set_decr_ref(struct Sarl_Set *);
extern void sarl_set_incr_ref(struct Sarl_Set *);

#endif
