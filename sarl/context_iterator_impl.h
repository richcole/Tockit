#ifndef SARL_CONTEXT_IMPL_H
#define SARL_CONTEXT_IMPL_H

#include <sarl/ref_count.h>

struct Sarl_ContextIterator {
  Sarl_RefCount ref_count;

  struct Sarl_SetIterator * G;
  struct Sarl_SetIterator * M;
  struct Sarl_RelationIterator * I;
}

inline sarl_context_iterator_init(struct Sarl_Context* p_context)
{
	sarl_ref_count_init(&p_context->ref_count);
	p_context->G = 0;
	p_context->M = 0;
	p_context->I = 0;
};

#endif
