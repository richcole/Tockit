extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_plain_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_plain_next(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_plain_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_plain_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_plain_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_plain_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_plain_copy(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_plainIteratorTable = 
{
  sarl_set_iterator_plain_next_gte,
  sarl_set_iterator_plain_next,
  sarl_set_iterator_plain_value,
  sarl_set_iterator_plain_at_end,
  sarl_set_iterator_plain_reset,
  sarl_set_iterator_plain_decr_ref,
  sarl_set_iterator_plain_copy
};

/* constructive operations */
struct Sarl_SetIterator *sarl_set_iterator_create(struct Sarl_Set *S)
{
  Sarl_PlainSetIterator* it  = new Sarl_PlainSetIterator();
  sarl_set_iterator_init(it, &s_plainIteratorTable);

  it->set = S;
  sarl_set_incr_ref(it->set);
  
  it->it = S->set.begin();
  return it;
};

/* set_iterator moving operations */
static void  sarl_set_iterator_plain_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value)
{
  while( ! sarl_set_iterator_at_end(it) && 
    sarl_set_iterator_value(it) < value ) 
  {
    sarl_set_iterator_next(it);
  }
}

static void  sarl_set_iterator_plain_next(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    it->it++;
  }
}

static Sarl_Index sarl_set_iterator_plain_value(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    return *it->it;
  }
  else {
    return 0;
  }
}

static int   sarl_set_iterator_plain_at_end(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  return it->it == it->set->set.end();
};

static void  sarl_set_iterator_plain_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  it->it = it->set->set.begin();
};

/* reference counting interface */
void sarl_set_iterator_plain_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_set_decr_ref(it->set);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_plain_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *org_it = static_cast<Sarl_PlainSetIterator*>(a_it);
  
  Sarl_PlainSetIterator* copy_it  = new Sarl_PlainSetIterator();
  sarl_set_iterator_init(copy_it, &s_plainIteratorTable);

  copy_it->set = org_it->set;
  sarl_set_incr_ref(copy_it->set);
  
  copy_it->it = org_it->it;
  return copy_it;
}




