#ifndef SARL_RELATION_ITERATOR_H
#define SARL_RELATION_ITERATOR_H

#include <sarl/index.h>

/* construction of relation objects */
extern struct ContextIterator *sarl_context_iterator_create(
  struct Context *
);

extern struct ContextIterator *sarl_context_iterator_inverse(
  struct ContextIterator *
);

extern struct ContextIterator *sarl_context_iterator_complement(
  struct ContextIterator *
);

#endif
