extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_interval_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_interval_prev_leq(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_interval_next(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_interval_prev(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_interval_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_interval_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_interval_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_interval_reset_last(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_interval_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_interval_copy(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_intervalIteratorTable = 
{
  sarl_set_iterator_interval_next_gte,
  sarl_set_iterator_interval_prev_leq,
  sarl_set_iterator_interval_next,
  sarl_set_iterator_interval_prev,
  sarl_set_iterator_interval_value,
  sarl_set_iterator_interval_at_end,
  sarl_set_iterator_interval_reset,
  sarl_set_iterator_interval_reset_last,
  sarl_set_iterator_interval_decr_ref,
  sarl_set_iterator_interval_copy
};

/* constructive operations */
struct Sarl_SetIterator *
  sarl_set_iterator_interval(
      Sarl_Index a_lower,
      Sarl_Index a_upper)
{
  Sarl_IntervalSetIterator* it  = new Sarl_IntervalSetIterator();
  sarl_set_iterator_init(it, &s_intervalIteratorTable);

  it->lower = a_lower;
  it->upper = a_upper;
  it->curr  = a_lower;

  return it;
};

/* set_iterator moving operations */
static void  sarl_set_iterator_interval_next_gte(
    struct Sarl_SetIterator *a_it, 
    Sarl_Index value)
{
    Sarl_IntervalSetIterator *it = 
        static_cast<Sarl_IntervalSetIterator*>(a_it);

    if ( it->curr < value ) {
        it->curr = value;
    };
}

static void  sarl_set_iterator_interval_prev_leq(
    struct Sarl_SetIterator *a_it, 
    Sarl_Index value)
{
  SARL_NOT_IMPLEMENTED;
};

static void  
sarl_set_iterator_interval_next(
    struct Sarl_SetIterator *a_it)
{
    Sarl_IntervalSetIterator *it = 
        static_cast<Sarl_IntervalSetIterator*>(a_it);

    if ( it->curr <= it->upper ) {
        ++(it->curr);
    }
}

static void  
sarl_set_iterator_interval_prev(
    struct Sarl_SetIterator *a_it)
{
  SARL_NOT_IMPLEMENTED;
};

static Sarl_Index 
sarl_set_iterator_interval_value(
    struct Sarl_SetIterator *a_it)
{
    Sarl_IntervalSetIterator *it = 
        static_cast<Sarl_IntervalSetIterator*>(a_it);

    if ( ! sarl_set_iterator_at_end(it) ) {
        return it->curr;
    }
    else {
        return 0;
    }
}

static int
sarl_set_iterator_interval_at_end(struct Sarl_SetIterator *a_it)
{
  Sarl_IntervalSetIterator *it = static_cast<Sarl_IntervalSetIterator*>(a_it);
  return ! (it->curr >= it->lower && it->curr <= it->upper);
};

static void  
sarl_set_iterator_interval_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_IntervalSetIterator *it = static_cast<Sarl_IntervalSetIterator*>(a_it);
  it->curr = it->lower;
};

static void  
sarl_set_iterator_interval_reset_last(struct Sarl_SetIterator *a_it) 
{
  SARL_NOT_IMPLEMENTED;
};

/* reference counting interface */
void sarl_set_iterator_interval_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_IntervalSetIterator *it = static_cast<Sarl_IntervalSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_interval_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_IntervalSetIterator *org_it = 
      static_cast<Sarl_IntervalSetIterator*>(a_it);
  
  Sarl_IntervalSetIterator* copy_it  = new Sarl_IntervalSetIterator();
  sarl_set_iterator_init(copy_it, &s_intervalIteratorTable);

  copy_it->upper = org_it->upper;
  copy_it->lower = org_it->lower;
  copy_it->curr  = org_it->curr;

  return copy_it;
}




