extern "C" {

#include <sarl/map.h>
#include <sarl/map_iterator.h>
#include <sarl/ref_count.h>
#include <sarl/relation_iterator.h>  
#include <sarl/set_iterator.h>

}

#include <iostream>

#include <sarl/map_impl.h>
#include <sarl/map_iterator_impl.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

/* constructive operations */

struct Sarl_MapIterator *sarl_map_iterator_create(
  struct Sarl_Map *m)
{
  return static_cast<Sarl_MapIterator*>(
    sarl_relation_iterator_create(m)
  );
};

int sarl_map_iterator_is_empty(
  struct Sarl_MapIterator *it
)
{
  return sarl_map_iterator_at_end(it);
};

/* functions delegating to the function table */

void  sarl_map_iterator_next_gte(
  struct Sarl_MapIterator *it, 
  Sarl_Pair value)
{
  it->funcs->next_gte(it, value);
}

void  sarl_map_iterator_next(
  struct Sarl_MapIterator *it)
{
  it->funcs->next(it);
}

struct Sarl_Pair sarl_map_iterator_value(
  struct Sarl_MapIterator *it)
{
  return it->funcs->value(it);
}

int sarl_map_iterator_at_end(
  struct Sarl_MapIterator *it)
{
  return it->funcs->at_end(it);
};

void  sarl_map_iterator_reset(
  struct Sarl_MapIterator *it) 
{
  it->funcs->reset(it);
};

void sarl_map_iterator_decr_ref(
  struct Sarl_MapIterator *it)
{
  it->funcs->decr_ref(it);
}

void sarl_map_iterator_incr_ref(
  struct Sarl_MapIterator *it)
{
  sarl_ref_count_incr(&it->ref_count);
};

struct Sarl_MapIterator* sarl_map_iterator_copy(
  struct Sarl_MapIterator *a_it)
{
  return static_cast<Sarl_MapIterator*>(
    a_it->funcs->copy(a_it)
  );
};

struct Sarl_RelationIterator* sarl_map_iterator_create_relation_iterator(
  struct Sarl_MapIterator *a_it)
{
  return a_it;
};

Sarl_Index sarl_map_iterator_image(Sarl_MapIterator *m, Sarl_Index x)
{
  /* warning: this operation could fail */
  Sarl_Index result = 0;
  
  Sarl_SetIterator* intent = sarl_relation_iterator_intent(m, x);
  sarl_set_iterator_release_ownership(intent);
  if ( ! sarl_set_iterator_at_end(intent) ) {
    result = sarl_set_iterator_value(intent);
  }
  sarl_set_iterator_decr_ref(intent);

  return result;
};









