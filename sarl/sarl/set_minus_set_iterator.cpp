extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_minus_set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_set_minus_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_set_minus_next(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_set_minus_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_set_minus_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_set_minus_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_set_minus_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_set_minus_copy(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_set_minus_iterator_table = 
{
  sarl_set_iterator_set_minus_next_gte,
  sarl_set_iterator_set_minus_next,
  sarl_set_iterator_set_minus_value,
  sarl_set_iterator_set_minus_at_end,
  sarl_set_iterator_set_minus_reset,
  sarl_set_iterator_set_minus_decr_ref,
  sarl_set_iterator_set_minus_copy
};

inline void sarl_set_iterator_set_minus_advance(
  Sarl_SetMinusSetIterator *it
)
{
  if ( ! sarl_set_iterator_at_end(it->first) ) {
    sarl_set_iterator_next_gte(
      it->second,
      sarl_set_iterator_value(it->first)
    );
  }

  while( ! sarl_set_iterator_at_end(it->first) &&
    ! sarl_set_iterator_at_end(it->second) &&
    sarl_set_iterator_value(it->first) == sarl_set_iterator_value(it->second)
  )
  {
    sarl_set_iterator_next(it->first);
    if ( ! sarl_set_iterator_at_end(it->first) ) {
      sarl_set_iterator_next_gte(
	it->second,
	sarl_set_iterator_value(it->first)
      );
    }
  }
}

/* construct a meet operation */
struct Sarl_SetIterator *sarl_set_iterator_minus(
  struct Sarl_SetIterator *a_first, struct Sarl_SetIterator *a_second
)
{
  Sarl_SetMinusSetIterator* it  = new Sarl_SetMinusSetIterator();
  sarl_set_iterator_init(it, &s_set_minus_iterator_table);

  it->first = sarl_set_iterator_obtain_ownership(a_first);
  it->second = sarl_set_iterator_obtain_ownership(a_second);
  sarl_set_iterator_set_minus_advance(it);
  return it;
}

/* set_iterator moving operations */
static void  sarl_set_iterator_set_minus_next_gte(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  Sarl_SetMinusSetIterator *it = static_cast<Sarl_SetMinusSetIterator*>(a_it);
  sarl_set_iterator_next_gte(it->first, value);
  sarl_set_iterator_set_minus_advance(it);
}

static void  sarl_set_iterator_set_minus_next(struct Sarl_SetIterator *a_it)
{
  Sarl_SetMinusSetIterator *it = static_cast<Sarl_SetMinusSetIterator*>(a_it);
  sarl_set_iterator_next(it->first);
  sarl_set_iterator_set_minus_advance(it);
}

static Sarl_Index sarl_set_iterator_set_minus_value(struct Sarl_SetIterator *a_it)
{
  Sarl_SetMinusSetIterator *it = static_cast<Sarl_SetMinusSetIterator*>(a_it);
  return sarl_set_iterator_value(it->first);
}

static int   sarl_set_iterator_set_minus_at_end(struct Sarl_SetIterator *a_it)
{
  Sarl_SetMinusSetIterator *it = static_cast<Sarl_SetMinusSetIterator*>(a_it);
  return sarl_set_iterator_at_end(it->first);
};

static void  sarl_set_iterator_set_minus_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_SetMinusSetIterator *it = static_cast<Sarl_SetMinusSetIterator*>(a_it);
  sarl_set_iterator_reset(it->first);
  sarl_set_iterator_reset(it->second);
  sarl_set_iterator_set_minus_advance(it);
};

/* reference counting interface */
void sarl_set_iterator_set_minus_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_SetMinusSetIterator *it = static_cast<Sarl_SetMinusSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_set_iterator_release_ownership(it->first);
    sarl_set_iterator_release_ownership(it->second);
    sarl_set_iterator_decr_ref(it->first);
    sarl_set_iterator_decr_ref(it->second);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_set_minus_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_SetMinusSetIterator *org_it = 
    static_cast<Sarl_SetMinusSetIterator*>(a_it);
  
  Sarl_SetMinusSetIterator* copy_it  = new Sarl_SetMinusSetIterator();
  sarl_set_iterator_init(copy_it, &s_set_minus_iterator_table);

  copy_it->first = sarl_set_iterator_copy(org_it->first);
  copy_it->second = sarl_set_iterator_copy(org_it->second);

  return copy_it;
}

