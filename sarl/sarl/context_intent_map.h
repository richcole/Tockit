#ifndef SARL_CONTEXT_INTENT_MAP_H
#define SARL_CONTEXT_INTENT_MAP_H

class Sarl_Context;
class Sarl_Map;

/*! produce a intent_mapd context K_r and two maps alpha_g, and alpha_m that
 *  map objects and attributes to equivalence classes 
 */

void sarl_context_intent_map(
  Sarl_ContextIterator* K, 
  Sarl_Context* K_r, 
  Sarl_Map* alpha_g, 
  Sarl_Map* alpha_m
);

#endif
