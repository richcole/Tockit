#ifndef SARL_CONTEXT_REDUCE_H
#define SARL_CONTEXT_REDUCE_H

class Sarl_Context;
class Sarl_Map;
struct Sarl_ContextIterator;


/*! produce a reduced context K_r and two maps alpha_g, and alpha_m that
 *  map objects and attributes to equivalence classes 
 */

void sarl_context_reduce(
  Sarl_ContextIterator* K, 
  Sarl_Context* K_r, 
  Sarl_Map* alpha_g, 
  Sarl_Map* alpha_m
);

#endif
