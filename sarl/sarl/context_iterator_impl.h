#ifndef SARL_CONTEXT_ITERATOR_IMPL_H
#define SARL_CONTEXT_ITERATOR_IMPL_H

extern "C" {
#include <sarl/ref_count.h>
}

#include <sarl/iterator_impl.h>

struct Sarl_ContextIterator : Sarl_Iterator {
  struct Sarl_SetIterator*       G;
  struct Sarl_SetIterator*       M;
  struct Sarl_RelationIterator*  I;
};

inline void sarl_context_iterator_init(struct Sarl_ContextIterator* p_it)
{
  sarl_iterator_init(p_it);
  p_it->G = 0;
  p_it->M = 0;
  p_it->I = 0;
};

#endif
