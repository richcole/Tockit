extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_minus_set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_set_minus_next_gte(
  struct SetIterator *it, 
  Index value);

static void sarl_set_iterator_set_minus_next(
  struct SetIterator *it);

static Index sarl_set_iterator_set_minus_val(
  struct SetIterator *it);

static int sarl_set_iterator_set_minus_at_end(
  struct SetIterator *it);

static void sarl_set_iterator_set_minus_reset(
  struct SetIterator *it);

static void sarl_set_iterator_set_minus_decr_ref(
  struct SetIterator *it);

static struct SetIterator* sarl_set_iterator_set_minus_copy(
  struct SetIterator *it);

/* function prototypes used in function table declaired below */

struct SetIteratorFunctionTable s_set_minus_iterator_table = 
{
  sarl_set_iterator_set_minus_next_gte,
  sarl_set_iterator_set_minus_next,
  sarl_set_iterator_set_minus_val,
  sarl_set_iterator_set_minus_at_end,
  sarl_set_iterator_set_minus_reset,
  sarl_set_iterator_set_minus_decr_ref,
  sarl_set_iterator_set_minus_copy
};

inline void sarl_set_iterator_set_minus_advance(
  SetMinusSetIterator *it
)
{
  if ( ! sarl_set_iterator_at_end(it->first) ) {
    sarl_set_iterator_next_gte(
      it->second,
      sarl_set_iterator_val(it->first)
    );
  }

  while( ! sarl_set_iterator_at_end(it->first) &&
    ! sarl_set_iterator_at_end(it->second) &&
    sarl_set_iterator_val(it->first) == sarl_set_iterator_val(it->second)
  )
  {
    sarl_set_iterator_next(it->first);
    if ( ! sarl_set_iterator_at_end(it->first) ) {
      sarl_set_iterator_next_gte(
	it->second,
	sarl_set_iterator_val(it->first)
      );
    }
  }
}

/* construct a meet operation */
struct SetIterator *sarl_set_iterator_minus(
  struct SetIterator *a_first, struct SetIterator *a_second
)
{
  SetMinusSetIterator* it  = new SetMinusSetIterator();
  sarl_set_iterator_init(it, &s_set_minus_iterator_table);

  it->first = a_first;
  it->second = a_second;
  sarl_set_iterator_incr_ref(it->first);
  sarl_set_iterator_incr_ref(it->second);
  sarl_set_iterator_set_minus_advance(it);
  return it;
}

/* set_iterator moving operations */
static void  sarl_set_iterator_set_minus_next_gte(
  struct SetIterator *a_it, 
  Index value)
{
  SetMinusSetIterator *it = static_cast<SetMinusSetIterator*>(a_it);
  sarl_set_iterator_next_gte(it->first, value);
  sarl_set_iterator_set_minus_advance(it);
}

static void  sarl_set_iterator_set_minus_next(struct SetIterator *a_it)
{
  SetMinusSetIterator *it = static_cast<SetMinusSetIterator*>(a_it);
  sarl_set_iterator_next(it->first);
  sarl_set_iterator_set_minus_advance(it);
}

static Index sarl_set_iterator_set_minus_val(struct SetIterator *a_it)
{
  SetMinusSetIterator *it = static_cast<SetMinusSetIterator*>(a_it);
  return sarl_set_iterator_val(it->first);
}

static int   sarl_set_iterator_set_minus_at_end(struct SetIterator *a_it)
{
  SetMinusSetIterator *it = static_cast<SetMinusSetIterator*>(a_it);
  return sarl_set_iterator_at_end(it->first);
};

static void  sarl_set_iterator_set_minus_reset(struct SetIterator *a_it) 
{
  SetMinusSetIterator *it = static_cast<SetMinusSetIterator*>(a_it);
  sarl_set_iterator_reset(it->first);
  sarl_set_iterator_reset(it->second);
  sarl_set_iterator_set_minus_advance(it);
};

/* reference counting interface */
void sarl_set_iterator_set_minus_decr_ref(struct SetIterator *a_it)
{
  SetMinusSetIterator *it = static_cast<SetMinusSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_set_iterator_decr_ref(it->first);
    sarl_set_iterator_decr_ref(it->second);
    delete it;
  }
}

static struct SetIterator* sarl_set_iterator_set_minus_copy(
  struct SetIterator *a_it)
{
  SetMinusSetIterator *org_it = 
    static_cast<SetMinusSetIterator*>(a_it);
  
  SetMinusSetIterator* copy_it  = new SetMinusSetIterator();
  sarl_set_iterator_init(copy_it, &s_set_minus_iterator_table);

  copy_it->first = sarl_set_iterator_copy(org_it->first);
  copy_it->second = sarl_set_iterator_copy(org_it->second);

  return copy_it;
}

