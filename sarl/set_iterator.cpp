extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_plain_next_gte(
  struct SetIterator *it, 
  Index value);

static void sarl_set_iterator_plain_next(
  struct SetIterator *it);

static Index sarl_set_iterator_plain_val(
  struct SetIterator *it);

static int sarl_set_iterator_plain_at_end(
  struct SetIterator *it);

static void sarl_set_iterator_plain_reset(
  struct SetIterator *it);

static void sarl_set_iterator_plain_decr_ref(
  struct SetIterator *it);

/* function prototypes used in function table declaired below */

struct SetIteratorFunctionTable s_plainIteratorTable = 
{
  sarl_set_iterator_plain_next_gte,
  sarl_set_iterator_plain_next,
  sarl_set_iterator_plain_val,
  sarl_set_iterator_plain_at_end,
  sarl_set_iterator_plain_reset,
  sarl_set_iterator_plain_decr_ref
};



/* constructive operations */
struct SetIterator *sarl_set_iterator_create(struct Set *S)
{
  PlainSetIterator* it  = new PlainSetIterator();

  sarl_ref_count_init(&it->m_ref_count);

  it->mp_funcs = &s_plainIteratorTable;
  it->mp_set = S;
  sarl_set_incr_ref(it->mp_set);
  
  it->m_it = S->m_set.begin();

  return it;
};

int sarl_set_iterator_is_empty(
  struct SetIterator *it
)
{
  return sarl_set_iterator_at_end(it);
};


/* set_iterator moving operations */
static void  sarl_set_iterator_plain_next_gte(
  struct SetIterator *it, 
  Index value)
{
  while( ! sarl_set_iterator_at_end(it) && 
    sarl_set_iterator_val(it) < value ) 
  {
    sarl_set_iterator_next(it);
  }
}

static void  sarl_set_iterator_plain_next(struct SetIterator *a_it)
{
  PlainSetIterator *it = static_cast<PlainSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    it->m_it++;
  }
}

static Index sarl_set_iterator_plain_val(struct SetIterator *a_it)
{
  PlainSetIterator *it = static_cast<PlainSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    return *it->m_it;
  }
}

static int   sarl_set_iterator_plain_at_end(struct SetIterator *a_it)
{
  PlainSetIterator *it = static_cast<PlainSetIterator*>(a_it);
  return it->m_it == it->mp_set->m_set.end();
};

static void  sarl_set_iterator_plain_reset(struct SetIterator *a_it) 
{
  PlainSetIterator *it = static_cast<PlainSetIterator*>(a_it);
  it->m_it = it->mp_set->m_set.begin();
};

/* reference counting interface */
void sarl_set_iterator_plain_decr_ref(struct SetIterator *a_it)
{
  PlainSetIterator *it = static_cast<PlainSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_set_decr_ref(it->mp_set);
    delete it;
  }
}

void  sarl_set_iterator_next_gte(
  struct SetIterator *it, 
  Index value)
{
  it->mp_funcs->next_gte(it, value);
}

void  sarl_set_iterator_next(struct SetIterator *it)
{
  it->mp_funcs->next(it);
}

Index sarl_set_iterator_val(struct SetIterator *it)
{
  return it->mp_funcs->val(it);
}

int   sarl_set_iterator_at_end(struct SetIterator *it)
{
  return it->mp_funcs->at_end(it);
};

void  sarl_set_iterator_reset(struct SetIterator *it) 
{
  it->mp_funcs->reset(it);
};

void sarl_set_iterator_decr_ref(struct SetIterator *it)
{
  it->mp_funcs->decr_ref(it);
}

void sarl_set_iterator_incr_ref(struct SetIterator *it)
{
  sarl_ref_count_incr(&it->m_ref_count);
};





