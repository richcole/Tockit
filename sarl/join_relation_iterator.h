#ifndef RELATION_ITERATOR_JOIN_H
#define RELATION_ITERATOR_JOIN_H

extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

/* function prototypes used in function table declaired below */

extern void sarl_relation_iterator_join_next_gte(
  struct Sarl_RelationIterator *it, 
  Sarl_Pair value);

extern void sarl_relation_iterator_join_next(
  struct Sarl_RelationIterator *it);

extern Sarl_Pair sarl_relation_iterator_join_value(
  struct Sarl_RelationIterator *it);

extern int sarl_relation_iterator_join_at_end(
  struct Sarl_RelationIterator *it);

extern void sarl_relation_iterator_join_reset(
  struct Sarl_RelationIterator *it);

extern void sarl_relation_iterator_join_decr_ref(
  struct Sarl_RelationIterator *it);

extern struct Sarl_RelationIterator* sarl_relation_iterator_join_copy(
  struct Sarl_RelationIterator *it);

extern struct Sarl_RelationIterator* sarl_relation_iterator_join_inverse(
  struct Sarl_RelationIterator *it);

#endif
