#ifndef SARL_MAP_H
#define SARL_MAP_H

#include <sarl/index.h>
#include <sarl/pair.h>

struct Sarl_MapIterator;
struct Sarl_Map;

/* construction of map objects */
extern struct Sarl_Map *sarl_map_create();
extern struct Sarl_Map *sarl_map_copy(
  struct Sarl_MapIterator *);

extern void sarl_map_insert(
  struct Sarl_Map *, Sarl_Index, Sarl_Index);

extern void sarl_map_insert_pair(
  struct Sarl_Map *, Sarl_Pair);

extern void sarl_map_remove(
  struct Sarl_Map *, Sarl_Index);

extern void sarl_map_remove_extent(
	struct Sarl_Map *r, Sarl_Index index);

extern void sarl_map_remove_intent(
	struct Sarl_Map *r, Sarl_Index index);

extern Sarl_Index sarl_map_image(
	struct Sarl_Map *r, Sarl_Index index);

extern Sarl_Index sarl_map_coimage(
	struct Sarl_Map *r, Sarl_Index index);


/* reference counting interface */
extern void sarl_map_decr_ref(struct Sarl_Map *);
extern void sarl_map_incr_ref(struct Sarl_Map *);

#endif
