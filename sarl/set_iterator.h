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

/* copy operations */
extern struct SetIterator *sarl_set_iterator_copy(struct SetIterator *);

/* constructive operations */
extern struct SetIterator *sarl_set_iterator_create(struct Set *);

extern struct SetIterator *sarl_set_iterator_meet(
  struct SetIterator *, struct SetIterator *
);

extern struct SetIterator *sarl_set_iterator_union(
  struct SetIterator *, struct SetIterator *
);

extern struct SetIterator *sarl_set_iterator_minus(
  struct SetIterator *, struct SetIterator *
);

/* boolean operations */

extern int sarl_set_iterator_lexical_compare(
  struct SetIterator *, struct SetIterator *
);

extern int sarl_set_iterator_subset(
  struct SetIterator *, struct SetIterator *
);

extern int sarl_set_iterator_is_empty(
  struct SetIterator *
);

/* set_iterator moving operations */
extern void  sarl_set_iterator_next_gte(struct SetIterator *, Index);
extern void  sarl_set_iterator_next(struct SetIterator *);
extern Index sarl_set_iterator_val(struct SetIterator *);
extern int   sarl_set_iterator_at_end(struct SetIterator *);
extern void  sarl_set_iterator_reset(struct SetIterator *);

/** return then number of elements in the sequence without regard to
 *  the current location of the iterator. This doesn't move the
 *  iterator.
 *
 *  This is an expensive operation because it involves copying the
 *  iterator and iterating though the entire sequence.
 */
extern Index  sarl_set_iterator_count(struct SetIterator *);

/** return then number of elements in the sequence counting from the
 *  element currently refered to by the iterator to the end of the
 *  sequence.
 *
 *  This is an expensive operation because it involves copying the
 *  iterator and iterating though the remaining elements of the sequence.
 */
extern Index sarl_set_iterator_count_remaining(struct SetIterator *);

/* reference counting interface */
extern void sarl_set_iterator_decr_ref(struct SetIterator *);
extern void sarl_set_iterator_incr_ref(struct SetIterator *);

#endif
