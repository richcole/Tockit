#ifndef SARL_SET_ITERATOR_H
#define SARL_SET_ITERATOR_H

#include <sarl/index.h>

struct SetIterator;
struct Set;

/* iterator macro */
#define SARL_SET_ITERATOR_FOR(x) \
  for( \
    sarl_set_iterator_reset(x);   \
    !sarl_set_iterator_at_end(x); \
    sarl_set_iterator_next(x) \
  )

/* constructive operations */
extern struct SetIterator *sarl_set_iterator_create(struct Set *);

/* omit ---

extern struct SetIterator *sarl_set_iterator_meet(
  struct SetIterator *, struct SetIterator *
);

extern struct SetIterator *sarl_set_iterator_union(
  struct SetIterator *, struct SetIterator *
);

extern struct SetIterator *sarl_set_iterator_minus(
  struct SetIterator *, struct SetIterator *
);

--- omit */

/* boolean operations */

/* omit ---

extern int sarl_set_iterator_lexical_less(
  struct SetIterator *, struct SetIterator *
);

extern int sarl_set_iterator_subset(
  struct SetIterator *, struct SetIterator *
);

*/

extern int sarl_set_iterator_is_empty(
  struct SetIterator *
);

/* set_iterator moving operations */
extern void  sarl_set_iterator_next_gte(struct SetIterator *, Index);
extern void  sarl_set_iterator_next(struct SetIterator *);
extern Index sarl_set_iterator_val(struct SetIterator *);
extern int   sarl_set_iterator_at_end(struct SetIterator *);
extern void  sarl_set_iterator_reset(struct SetIterator *);

/* reference counting interface */
extern void sarl_set_iterator_decr_ref(struct SetIterator *);
extern void sarl_set_iterator_incr_ref(struct SetIterator *);

#endif
