extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/intersection_set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_intersection_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_intersection_next(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_intersection_val(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_intersection_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_intersection_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_intersection_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_intersection_copy(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_intersectionIteratorTable = 
{
  sarl_set_iterator_intersection_next_gte,
  sarl_set_iterator_intersection_next,
  sarl_set_iterator_intersection_val,
  sarl_set_iterator_intersection_at_end,
  sarl_set_iterator_intersection_reset,
  sarl_set_iterator_intersection_decr_ref,
  sarl_set_iterator_intersection_copy
};

inline void sarl_set_iterator_intersection_advance(
  Sarl_IntersectionSetIterator *it
)
{
  Sarl_Index u, v;
  while(
    ! sarl_set_iterator_at_end(it->first) &&
    ! sarl_set_iterator_at_end(it->second))
  {
    u = sarl_set_iterator_val(it->first);
    v = sarl_set_iterator_val(it->second);
    
    if ( u < v ) {
      sarl_set_iterator_next_gte(it->first, v);
    }
    else if ( v < u ) {
      sarl_set_iterator_next_gte(it->second, u);
    }
    else {
      break;
    }
  }
}

/* construct a meet operation */
struct Sarl_SetIterator *sarl_set_iterator_meet(
  struct Sarl_SetIterator *a_first, struct Sarl_SetIterator *a_second
)
{
  Sarl_IntersectionSetIterator* it  = new Sarl_IntersectionSetIterator();
  sarl_set_iterator_init(it, &s_intersectionIteratorTable);

  it->first = a_first;
  it->second = a_second;
  sarl_set_iterator_incr_ref(it->first);
  sarl_set_iterator_incr_ref(it->second);
  sarl_set_iterator_intersection_advance(it);
  return it;
}

/* set_iterator moving operations */
static void  sarl_set_iterator_intersection_next_gte(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  Sarl_IntersectionSetIterator *it = 
    static_cast<Sarl_IntersectionSetIterator*>(a_it);
  sarl_set_iterator_next_gte(it->first, value);
  sarl_set_iterator_next_gte(it->second, value);
  sarl_set_iterator_intersection_advance(it);
}

static void  sarl_set_iterator_intersection_next(struct Sarl_SetIterator *a_it)
{
  Sarl_IntersectionSetIterator *it = 
    static_cast<Sarl_IntersectionSetIterator*>(a_it);
  sarl_set_iterator_next(it->first);
  sarl_set_iterator_next(it->second);
  sarl_set_iterator_intersection_advance(it);
}

static Sarl_Index sarl_set_iterator_intersection_val(
  struct Sarl_SetIterator *a_it)
{
  Sarl_IntersectionSetIterator *it = 
    static_cast<Sarl_IntersectionSetIterator*>(a_it);
  return sarl_set_iterator_val(it->first);
}

static int sarl_set_iterator_intersection_at_end(
  struct Sarl_SetIterator *a_it)
{
  Sarl_IntersectionSetIterator *it = 
    static_cast<Sarl_IntersectionSetIterator*>(a_it);
  return sarl_set_iterator_at_end(it->first) ||
    sarl_set_iterator_at_end(it->second);
};

static void  sarl_set_iterator_intersection_reset(
  struct Sarl_SetIterator *a_it) 
{
  Sarl_IntersectionSetIterator *it = 
    static_cast<Sarl_IntersectionSetIterator*>(a_it);
  sarl_set_iterator_reset(it->first);
  sarl_set_iterator_reset(it->second);
  sarl_set_iterator_intersection_advance(it);
};

/* reference counting interface */
void sarl_set_iterator_intersection_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_IntersectionSetIterator *it = 
    static_cast<Sarl_IntersectionSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_set_iterator_decr_ref(it->first);
    sarl_set_iterator_decr_ref(it->second);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_intersection_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_IntersectionSetIterator *org_it = 
    static_cast<Sarl_IntersectionSetIterator*>(a_it);
  
  Sarl_IntersectionSetIterator* copy_it  = new Sarl_IntersectionSetIterator();
  sarl_set_iterator_init(copy_it, &s_intersectionIteratorTable);

  copy_it->first = sarl_set_iterator_copy(org_it->first);
  copy_it->second = sarl_set_iterator_copy(org_it->second);

  return copy_it;
}

