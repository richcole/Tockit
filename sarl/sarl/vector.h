#ifndef SARL_VECTOR_H
#define SARL_VECTOR_H

#include <sarl/index.h>

struct Sarl_Vector;

/* construction of vector objects */
extern Sarl_Vector *sarl_vector_create(Sarl_Index arity);
extern Sarl_Vector *sarl_vector_copy(Sarl_Vector* v);

extern void sarl_vector_set(Sarl_Vector *, Sarl_Index index, Sarl_Index value);
extern Sarl_Index sarl_vector_get(Sarl_Vector *r, Sarl_Index index);
extern Sarl_Index sarl_vector_arity(Sarl_Vector *v);

/* reference counting interface */
extern void sarl_vector_decr_ref(Sarl_Vector *);
extern void sarl_vector_incr_ref(Sarl_Vector *);

#endif
