extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include "set_impl.h"

struct Sarl_Set *sarl_set_create()
{
  Sarl_Set *S = new Sarl_Set();
  sarl_ref_count_init(&S->m_ref_count);
  return S;
}

struct Sarl_Set *sarl_set_copy(struct Sarl_SetIterator *it)
{
  struct Sarl_Set *s = new Sarl_Set();
  sarl_ref_count_init(&s->m_ref_count);
  SARL_SET_ITERATOR_FOR(it) {
    sarl_set_insert(s, sarl_set_iterator_val(it));
  }
  return s;
}

void sarl_set_insert(struct Sarl_Set *S, Sarl_Index x)
{
  S->m_set.insert(x);
}
  
void sarl_set_remove(struct Sarl_Set *S, Sarl_Index x)
{
  S->m_set.erase(x);
}

void sarl_set_decr_ref(struct Sarl_Set *S)
{
  if ( sarl_ref_count_decr(&S->m_ref_count) ) {
    delete S;
  };
}

void sarl_set_incr_ref(struct Sarl_Set *S)
{
  sarl_ref_count_incr(&S->m_ref_count);
}

