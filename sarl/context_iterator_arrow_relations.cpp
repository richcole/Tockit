extern "C" {

#include <sarl/context_iterator.h>
#include <sarl/context.h>

#include <sarl/set.h>	
#include <sarl/set_iterator.h>	

#include <sarl/relation.h>	
#include <sarl/relation_iterator.h>	
#include <sarl/lectic.h>

}

#include <sarl/cpp/ContextIterator.h>
#include <sarl/cpp/SetIterator.h>

#include <sarl/context_impl.h>
#include <sarl/context_iterator_impl.h>
#include <iostream>

int
sarl_context_iterator_down_arrow(
  Sarl_ContextIterator* a_K_it, Sarl_Index g, Sarl_Index m) 
{
  /* g is maximal amongst elements that don't have m.
   *
   * i.e. for h in G \ m' ; g' subseteq h'
   */
  ContextIterator K_it(a_K_it, SARL_INCR_REF); // wrap a_K_it

  SetIterator G   = K_it.objects();
  SetIterator m_i = K_it.extent(m);
  
  SetIterator cand = minus(G, m_i);
  SetIterator g_i = K_it.intent(g);
  
  SARL_FOR(cand) {
    SetIterator h_i = K_it.extent(cand.value());
    if ( ! g_i.subseteq(h_i) ) {
      break;
    }
  };

  return ! cand.at_end();
};
