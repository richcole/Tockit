#ifndef SARL_LECTIC_H
#define SARL_LECTIC_H

#include <sarl/index.h>
#include <sarl/pair.h>

/*! 
  Determine the lectically next subset of G after A.
  The lectic order is derived by considering the first element
  of G the least significant bit and the last element 
  the most significant bit.
*/
extern struct Sarl_SetIterator*
  sarl_set_iterator_lectic_next(
    struct Sarl_SetIterator *A,
    struct Sarl_SetIterator *G
  );

extern struct Sarl_SetIterator*
  sarl_set_iterator_lectic_next_gte(
    struct Sarl_SetIterator *A,
    Sarl_Index               i,
    struct Sarl_SetIterator *G
    );

#endif

