extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include "set_impl.h"

struct Set *sarl_set_create()
{
  Set *S = new Set();
  sarl_ref_count_init(&S->m_ref_count);
  return S;
}

struct Set *sarl_set_copy(struct SetIterator *it)
{
  struct Set *s = new Set();
  SARL_SET_ITERATOR_FOR(it) {
    sarl_set_insert(s, sarl_set_iterator_val(it));
  }
  return s;
}

void sarl_set_insert(struct Set *S, Index x)
{
  S->m_set.insert(x);
}
  
void sarl_set_remove(struct Set *S, Index x)
{
  S->m_set.erase(x);
}

void sarl_set_decr_ref(struct Set *S)
{
  if ( sarl_ref_count_decr(&S->m_ref_count) ) {
    delete S;
  };
}

void sarl_set_incr_ref(struct Set *S)
{
  sarl_ref_count_incr(&S->m_ref_count);
}

