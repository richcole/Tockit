extern "C" {

#include <sarl/set.h>  
#include <sarl/set_iterator.h>  
#include <sarl/relation.h>
#include <sarl/map.h>
#include <sarl/map_iterator.h>
#include <sarl/ref_count.h>
}

#include "map_impl.h"
#include <algorithm>

struct Sarl_Map *
  sarl_map_create()
{
  Sarl_Map *r = new Sarl_Map();
  sarl_ref_count_init(&r->ref_count);
  return r;
}

struct Sarl_Map *
  sarl_map_copy(
    struct Sarl_MapIterator *it
  )
{
  struct Sarl_Map *s = new Sarl_Map();
  sarl_ref_count_init(&s->ref_count);
  SARL_MAP_ITERATOR_FOR(it) {
    sarl_map_insert_pair(s, sarl_map_iterator_value(it));
  }
  return s;
}

void 
  sarl_map_insert(
    struct Sarl_Map *r, Sarl_Index a, Sarl_Index b
  )
{
  Sarl_Pair p = { a, b };
  Sarl_Pair q = { b, a };
       
  r->forward.insert(p);
  r->reverse.insert(q);
}
  
void 
  sarl_map_remove(
    struct Sarl_Map *r, Sarl_Index a, Sarl_Index b
  )
{
  Sarl_Pair p = { a, b };
  Sarl_Pair q = { b, a };
       
  r->forward.erase(p);
  r->reverse.erase(q);
}

void 
  sarl_map_remove_extent(
    struct Sarl_Map *r, Sarl_Index index
  )
{
  sarl_relation_remove_extent(r, index);
};

void 
  sarl_map_remove_intent(
    struct Sarl_Map *r, Sarl_Index index
  )
{
  sarl_relation_remove_intent(r, index);
}

void sarl_map_decr_ref(struct Sarl_Map *r)
{
  if ( sarl_ref_count_decr(&r->ref_count) ) {
    delete r;
  }
};

void sarl_map_incr_ref(struct Sarl_Map *r)
{
  sarl_ref_count_incr(&r->ref_count);
}

void sarl_map_insert_pair(struct Sarl_Map *r, struct Sarl_Pair pair)
{
  sarl_relation_remove_intent(r, pair.dom);
  sarl_map_insert(r, pair.dom, pair.rng);
}


