#ifndef SARL_RELATION_ITERATOR_H
#define SARL_RELATION_ITERATOR_H

struct NaryRelationIterator;

extern Sarl_SetIterator* 
  nary_relation_iterator_create(
    Sarl_NaryRelation *r
  );

extern Sarl_SetIterator* 
  nary_relation_iterator_set_iterator(
    Sarl_NaryRelationIterator *r_it
  );


#endif
