#ifndef SARL_CONTEXT_ITERATOR_IMPL_H
#define SARL_CONTEXT_ITERATOR_IMPL_H

#include <sarl/ref_count.h>

struct Sarl_ContextIterator : Sarl_Iterator {
  struct Sarl_SetIterator*       G;
  struct Sarl_SetIterator*       M;
  struct Sarl_RelationIterator*  I;
};

inline void sarl_context_iterator_init(struct Sarl_ContextIterator* p_it)
{
    sarl_ref_count_init(&p_it->ref_count);
    p_it->G = 0;
    p_it->M = 0;
    p_it->I = 0;
};

#endif
