extern "C" {
#include <sarl/global.h>
#include <sarl/context_reduce.h>
#include <sarl/context.h>
#include <sarl/map.h>
}

void sarl_context_reduce(
  Sarl_ContextIterator* K, 
  Sarl_Context* K_r, 
  Sarl_Map* alpha_g, 
  Sarl_Map* alpha_m
)
{
  Sarl_Relation *intent_map = sarl_relation_create();
  Sarl_Relation *intent;
  Sarl_Relation *intent_extent;
  Sarl_SetIterator *m_set_it;
  Sarl_SetIterator *X;
  
  sarl_context_intent_map(K, intent_map);
  
  intent = sarl_relation_intent(intent_map, m);
  m_set_it = sarl_set_iterator_create_from_index(m);
  sarl_set_iterator_release_ownership(m_set_it);
  X = set_iterator_minus(intent, m_set_it);
  intent_extent = sarl_relation_intent_extent(intent_map, X);

  if ( sarl_set_iterator_eq(intent_extent, intent) ) {
    

  
  
  
  
};



