#ifndef RELATION_ITERATOR_PLAIN_H
#define RELATION_ITERATOR_PLAIN_H

extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

/* function prototypes used in function table declaired below */

extern void sarl_relation_iterator_plain_next_gte(
  struct Sarl_RelationIterator *it, 
  Sarl_Pair value);

extern void sarl_relation_iterator_plain_next(
  struct Sarl_RelationIterator *it);

extern Sarl_Pair sarl_relation_iterator_plain_val(
  struct Sarl_RelationIterator *it);

extern int sarl_relation_iterator_plain_at_end(
  struct Sarl_RelationIterator *it);

extern void sarl_relation_iterator_plain_reset(
  struct Sarl_RelationIterator *it);

extern void sarl_relation_iterator_plain_decr_ref(
  struct Sarl_RelationIterator *it);

extern struct Sarl_RelationIterator* sarl_relation_iterator_plain_copy(
  struct Sarl_RelationIterator *it);

extern struct Sarl_RelationIterator* sarl_relation_iterator_plain_inverse(
  struct Sarl_RelationIterator *it);

#endif
