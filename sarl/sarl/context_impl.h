#ifndef SARL_CONTEXT_IMPL_H
#define SARL_CONTEXT_IMPL_H

extern "C" {
  #include <sarl/ref_count.h>
}
  
#include <sarl/iterator_impl.h>

struct Sarl_Context : Sarl_Iterator {
  struct Sarl_Set * G;
  struct Sarl_Set * M;
  struct Sarl_Relation * I;
};

inline void sarl_context_init(struct Sarl_Context* it)
{
  sarl_iterator_init(it);
  it->G = 0;
  it->M = 0;
  it->I = 0;
};

#endif
