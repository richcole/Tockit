#ifndef SARL_CONTEXT_ITERATOR_H
#define SARL_CONTEXT_ITERATOR_H

#include <sarl/index.h>
#include <sarl/pair.h>

extern struct Sarl_ContextIterator *
  sarl_context_iterator_create
(
  struct Context *
);

/*! 
 * construct a context from iterators. The context iterator is 
 * defined as follows: 
 *   self.G_it = union(G_it, domain(I_it))
 *   self.M_it = union(M_it, range(I_It))
 *   self.I_It = copy(I_it)
 */
extern struct Sarl_ContextIterator *
  sarl_context_iterator_create
(
	struct SetIterator*      G_it,
  struct SetIterator*      M_it,
  struct RelationIterator* I_it,
);

extern struct Sarl_SetIterator *
  sarl_context_iterator_objects
(
  struct Sarl_ContextIterator *
);

extern struct Sarl_SetIterator *
  sarl_context_iterator_attributes
(
  struct Sarl_ContextIterator *
);

extern struct Sarl_RelationIterator *
sarl_context_incidence
(
  struct Sarl_ContextIterator *
); 						     

extern struct Sarl_ContextIterator *
  sarl_context_iterator_inverse
(
  struct Sarl_ContextIterator *
);

extern struct Sarl_ContextIterator *
  sarl_context_iterator_complement
(
  struct Sarl_ContextIterator *
);

extern struct Sarl_RelationIterator*
   sarl_context_iterator_up_arrow
(
	struct SarlContextIterator *
);

extern struct Sarl_RelationIterator*
   sarl_context_iterator_down_arrow
(
	struct SarlContextIterator *
);

extern struct Sarl_RelationIterator*
   sarl_context_iterator_updown_arrow
(
	struct SarlContextIterator *
);

#endif
