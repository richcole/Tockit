xtern "C" {
#include <sarl/config.h>
#include <sarl/context_reduce.h>
#include <sarl/context.h>
#include <sarl/map.h>
}

void sarl_context_intent_map(Sarl_ContextIterator K, Sarl_Relation intent_map)
{
  Sarl_SetIterator* M = 
    sarl_context_iterator_attributes(K);   /* K = (G,M,I) */
  
  SARL_SET_ITERATOR_FOR(M) {
    Sarl_Index         m     = sarl_set_iterator_value(M);
    Sarl_Set*          m_set = sarl_set_create();
    Sarl_SetIterator*  m_set_it = sarl_set_iterator_create(m_set);

    Sarl_SetIterator* m_ii = 
      sarl_context_iterator_extent_intent_set(K, m_set_it);

    FOR_SET(m_ii) {
      sarl_relation_insert(intent_map, m, sarl_set_iterator_value(m_ii));
    };
    
    sarl_set_iterator_decr_ref(m_set_it);
    sarl_set_decr_ref(m_set);
    sarl_set_iterator_decr_ref(m_ii);
  };
};
