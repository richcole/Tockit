#ifndef SARL_RELATION_ITERATOR_H
#define SARL_RELATION_ITERATOR_H

#include <sarl/index.h>

/* construction of relation objects */
extern struct RelationIterator *sarl_relation_iterator_create(
  struct Relation *
);

extern struct RelationIterator *sarl_relation_iterator_join(
  struct RelationIterator *, struct RelationIterator *
);

extern struct RelationIterator *sarl_relation_iterator_inverse(
  struct RelationIterator *
);

#endif
