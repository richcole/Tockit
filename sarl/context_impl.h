#ifndef SARL_CONTEXT_IMPL_H
#define SARL_CONTEXT_IMPL_H

#include <sarl/ref_count.h>

struct Sarl_Context {
  Sarl_RefCount ref_count;

  struct Sarl_Set * G;
  struct Sarl_Set * M;
  struct Sarl_Relation * I;
}

inline sarl_context_init(struct Sarl_Context* p_context)
{
	sarl_ref_count_init(&p_context->ref_count);
	p_context->G = 0;
	p_context->M = 0;
	p_context->I = 0;
};

#endif
