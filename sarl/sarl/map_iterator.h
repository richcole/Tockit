#ifndef SARL_MAP_ITERATOR_H
#define SARL_MAP_ITERATOR_H

#include <sarl/index.h>
#include <sarl/pair.h>

/* iterator macro */
#define SARL_MAP_ITERATOR_FOR(x) \
  for( \
    sarl_map_iterator_reset(x);   \
    !sarl_map_iterator_at_end(x); \
    sarl_map_iterator_next(x) \
  )

/* set_iterator moving operations */

extern void  sarl_map_iterator_next_gte(
  struct Sarl_MapIterator *, 
  struct Sarl_Pair);

extern void  sarl_map_iterator_next(
  struct Sarl_MapIterator *);

extern struct Sarl_Pair sarl_map_iterator_value(
  struct Sarl_MapIterator *);

extern int   sarl_map_iterator_at_end(
  struct Sarl_MapIterator *);

extern void  sarl_map_iterator_reset(
  struct Sarl_MapIterator *);

extern void sarl_map_iterator_decr_ref(
  struct Sarl_MapIterator *);

extern void sarl_map_iterator_incr_ref(
  struct Sarl_MapIterator *);

extern struct Sarl_MapIterator* 
  sarl_map_iterator_copy(
    struct Sarl_MapIterator *
  );

extern struct Sarl_MapIterator* 
  sarl_map_iterator_obtain_ownership(
    struct Sarl_MapIterator *
  );

extern struct Sarl_MapIterator*
  sarl_map_iterator_release_ownership(
    struct Sarl_MapIterator *
  );

extern Sarl_Index 
  sarl_map_iterator_image(
    Sarl_Index index
  );

/* iterator constructive operations */

extern struct Sarl_MapIterator *sarl_map_iterator_create(
  struct Sarl_Map *
);

extern struct Sarl_RelationIterator *sarl_map_iterator_relation_iterator(
  struct Sarl_MapIterator *
);


#endif
