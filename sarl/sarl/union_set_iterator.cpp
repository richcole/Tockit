extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/union_set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_union_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_union_prev_leq(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_union_next(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_union_prev(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_union_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_union_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_union_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_union_reset_last(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_union_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_union_copy(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_unionIteratorTable = 
{
  sarl_set_iterator_union_next_gte,
  sarl_set_iterator_union_prev_leq,
  sarl_set_iterator_union_next,
  sarl_set_iterator_union_prev,
  sarl_set_iterator_union_value,
  sarl_set_iterator_union_at_end,
  sarl_set_iterator_union_reset,
  sarl_set_iterator_union_reset_last,
  sarl_set_iterator_union_decr_ref,
  sarl_set_iterator_union_copy
};

/* construct a meet operation */
struct Sarl_SetIterator *sarl_set_iterator_union(
  struct Sarl_SetIterator *a_first, struct Sarl_SetIterator *a_second
)
{
  Sarl_UnionSetIterator* it  = new Sarl_UnionSetIterator();
  sarl_set_iterator_init(it, &s_unionIteratorTable);

  it->first = sarl_set_iterator_obtain_ownership(a_first);
  it->second = sarl_set_iterator_obtain_ownership(a_second);

  return it;
}

/* set_iterator moving operations */
static void  sarl_set_iterator_union_next_gte(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  Sarl_UnionSetIterator *it = static_cast<Sarl_UnionSetIterator*>(a_it);
  sarl_set_iterator_next_gte(it->first, value);
  sarl_set_iterator_next_gte(it->second, value);
}

static void  sarl_set_iterator_union_prev_leq(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  SARL_NOT_IMPLEMENTED;
};

static void  sarl_set_iterator_union_next(struct Sarl_SetIterator *a_it)
{
  Sarl_UnionSetIterator *it = static_cast<Sarl_UnionSetIterator*>(a_it);

  int finished_u = sarl_set_iterator_at_end(it->first);
  int finished_v = sarl_set_iterator_at_end(it->second);

  if ( finished_u && ! finished_v ) {
    sarl_set_iterator_next(it->second);
    return;
  }
  
  if ( ! finished_u && finished_v ) {
    sarl_set_iterator_next(it->first);
    return;
  }

  if ( ! finished_u && ! finished_v ) {
    Sarl_Index u = sarl_set_iterator_value(it->first);
    Sarl_Index v = sarl_set_iterator_value(it->second);

    if ( u < v ) {
      sarl_set_iterator_next(it->first);
    }
    else if ( v < u ) {
      sarl_set_iterator_next(it->second);
    }
    else {
      sarl_set_iterator_next(it->first);
      sarl_set_iterator_next(it->second);
    }
  }
}

static void  sarl_set_iterator_union_prev(struct Sarl_SetIterator *a_it)
{
  SARL_NOT_IMPLEMENTED;
};

static Sarl_Index sarl_set_iterator_union_value(struct Sarl_SetIterator *a_it)
{
  Sarl_UnionSetIterator *it = static_cast<Sarl_UnionSetIterator*>(a_it);

  int finished_u = sarl_set_iterator_at_end(it->first);
  int finished_v = sarl_set_iterator_at_end(it->second);

  if ( finished_u && ! finished_v ) {
    return sarl_set_iterator_value(it->second);
  }
  
  if ( ! finished_u && finished_v ) {
    return sarl_set_iterator_value(it->first);
  }

  if ( ! finished_u && ! finished_v ) {
    Sarl_Index u = sarl_set_iterator_value(it->first);
    Sarl_Index v = sarl_set_iterator_value(it->second);
    return u < v ? u : v;
  }
  
  return 0;
}

static int   sarl_set_iterator_union_at_end(struct Sarl_SetIterator *a_it)
{
  Sarl_UnionSetIterator *it = static_cast<Sarl_UnionSetIterator*>(a_it);

  return sarl_set_iterator_at_end(it->first) &&
    sarl_set_iterator_at_end(it->second);
};

static void  sarl_set_iterator_union_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_UnionSetIterator *it = static_cast<Sarl_UnionSetIterator*>(a_it);
  sarl_set_iterator_reset(it->first);
  sarl_set_iterator_reset(it->second);
};

static void  sarl_set_iterator_union_reset_last(struct Sarl_SetIterator *a_it) 
{
  SARL_NOT_IMPLEMENTED;
};

/* reference counting interface */
void sarl_set_iterator_union_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_UnionSetIterator *it = static_cast<Sarl_UnionSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_set_iterator_release_ownership(it->first);
    sarl_set_iterator_release_ownership(it->second);
    sarl_set_iterator_decr_ref(it->first);
    sarl_set_iterator_decr_ref(it->second);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_union_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_UnionSetIterator *org_it = 
    static_cast<Sarl_UnionSetIterator*>(a_it);
  
  Sarl_UnionSetIterator* copy_it  = new Sarl_UnionSetIterator();
  sarl_set_iterator_init(copy_it, &s_unionIteratorTable);

  copy_it->first = sarl_set_iterator_copy(org_it->first);
  copy_it->second = sarl_set_iterator_copy(org_it->second);

  return copy_it;
}

