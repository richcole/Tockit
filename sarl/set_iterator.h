#ifndef SARL_SET_ITERATOR_H
#define SARL_SET_ITERATOR_H

#include <sarl/index.h>

struct Sarl_SetIterator;
struct Sarl_Set;

/* iterator macro */
#define SARL_SET_ITERATOR_FOR(x) \
  for( \
    sarl_set_iterator_reset(x);   \
    !sarl_set_iterator_at_end(x); \
    sarl_set_iterator_next(x) \
  )

/* copy operations */

extern struct Sarl_SetIterator *
  sarl_set_iterator_copy(struct Sarl_SetIterator *);

/*! the function will return either _it_, or _it->copy()_. 
 *  In anycase the returned object will have a reference count of
 *  1 and so decr_ref_count, should be called on it.
 */
extern struct Sarl_SetIterator *
  sarl_set_iterator_obtain_ownership(struct Sarl_SetIterator *);

/*! the function will set the object to be not owned but will not
 *  decrement the reference count. 
 */
extern struct Sarl_SetIterator *
  sarl_set_iterator_release_ownership(struct Sarl_SetIterator *);

/* constructive operations */

extern struct Sarl_SetIterator *
  sarl_set_iterator_create(
    struct Sarl_Set *
  );

extern struct Sarl_SetIterator *
  sarl_set_iterator_interval(
      Sarl_Index a_lower,
      Sarl_Index a_upper);

extern struct Sarl_SetIterator *
  sarl_set_iterator_meet(
    struct Sarl_SetIterator *A, 
    struct Sarl_SetIterator *B
  );

extern struct Sarl_SetIterator *
  sarl_set_iterator_union(
    struct Sarl_SetIterator *A, 
    struct Sarl_SetIterator *B
  );

extern struct Sarl_SetIterator *
  sarl_set_iterator_minus(
    struct Sarl_SetIterator *, struct Sarl_SetIterator *
  );

/* boolean operations */

extern int sarl_set_iterator_lexical_compare(
  struct Sarl_SetIterator *, struct Sarl_SetIterator *
);

extern int sarl_set_iterator_subset(
  struct Sarl_SetIterator *, struct Sarl_SetIterator *
);

extern int sarl_set_iterator_is_empty(
  struct Sarl_SetIterator *
);

/* set_iterator moving operations */
extern void  sarl_set_iterator_next_gte(struct Sarl_SetIterator *, Sarl_Index);
extern void  sarl_set_iterator_next(struct Sarl_SetIterator *);
extern Sarl_Index sarl_set_iterator_value(struct Sarl_SetIterator *);
extern int   sarl_set_iterator_at_end(struct Sarl_SetIterator *);
extern void  sarl_set_iterator_reset(struct Sarl_SetIterator *);

/** return then number of elements in the sequence without regard to
 *  the current location of the iterator. This doesn't move the
 *  iterator.
 *
 *  This is an expensive operation because it involves copying the
 *  iterator and iterating though the entire sequence.
 */
extern Sarl_Index  sarl_set_iterator_count(struct Sarl_SetIterator *);

/** return then number of elements in the sequence counting from the
 *  element currently refered to by the iterator to the end of the
 *  sequence.
 *
 *  This is an expensive operation because it involves copying the
 *  iterator and iterating though the remaining elements of the sequence.
 */
extern Sarl_Index sarl_set_iterator_count_remaining(struct Sarl_SetIterator *);

/* reference counting interface */
extern void sarl_set_iterator_decr_ref(struct Sarl_SetIterator *);
extern void sarl_set_iterator_incr_ref(struct Sarl_SetIterator *);

extern Sarl_Index sarl_set_iterator_last(struct Sarl_SetIterator *);

#endif
