extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_plain_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_plain_next(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_plain_val(
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
  sarl_set_iterator_plain_val,
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

  it->mp_set = S;
  sarl_set_incr_ref(it->mp_set);
  
  it->m_it = S->m_set.begin();
  return it;
};

int sarl_set_iterator_is_empty(
  struct Sarl_SetIterator *it
)
{
  return sarl_set_iterator_at_end(it);
};


/* set_iterator moving operations */
static void  sarl_set_iterator_plain_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value)
{
  while( ! sarl_set_iterator_at_end(it) && 
    sarl_set_iterator_val(it) < value ) 
  {
    sarl_set_iterator_next(it);
  }
}

static void  sarl_set_iterator_plain_next(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    it->m_it++;
  }
}

static Sarl_Index sarl_set_iterator_plain_val(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    return *it->m_it;
  }
}

static int   sarl_set_iterator_plain_at_end(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  return it->m_it == it->mp_set->m_set.end();
};

static void  sarl_set_iterator_plain_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  it->m_it = it->mp_set->m_set.begin();
};

/* reference counting interface */
void sarl_set_iterator_plain_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *it = static_cast<Sarl_PlainSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->m_ref_count) ) {
    sarl_set_decr_ref(it->mp_set);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_plain_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_PlainSetIterator *org_it = static_cast<Sarl_PlainSetIterator*>(a_it);
  
  Sarl_PlainSetIterator* copy_it  = new Sarl_PlainSetIterator();
  sarl_set_iterator_init(copy_it, &s_plainIteratorTable);

  copy_it->mp_set = org_it->mp_set;
  sarl_set_incr_ref(copy_it->mp_set);
  
  copy_it->m_it = org_it->m_it;
  return copy_it;
}

/* functions delegating to the function table */
void  sarl_set_iterator_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value)
{
  it->mp_funcs->next_gte(it, value);
}

void  sarl_set_iterator_next(struct Sarl_SetIterator *it)
{
  it->mp_funcs->next(it);
}

Sarl_Index sarl_set_iterator_val(struct Sarl_SetIterator *it)
{
  return it->mp_funcs->val(it);
}

int   sarl_set_iterator_at_end(struct Sarl_SetIterator *it)
{
  return it->mp_funcs->at_end(it);
};

void  sarl_set_iterator_reset(struct Sarl_SetIterator *it) 
{
  it->mp_funcs->reset(it);
};

void sarl_set_iterator_decr_ref(struct Sarl_SetIterator *it)
{
  it->mp_funcs->decr_ref(it);
}

void sarl_set_iterator_incr_ref(struct Sarl_SetIterator *it)
{
  sarl_ref_count_incr(&it->m_ref_count);
};

struct Sarl_SetIterator* sarl_set_iterator_copy(
  struct Sarl_SetIterator *a_it)
{
  return a_it->mp_funcs->copy(a_it);
};

extern Sarl_Index  sarl_set_iterator_count(struct Sarl_SetIterator *a_it)
{
  Sarl_SetIterator *it_copy = sarl_set_iterator_copy(a_it);
  Sarl_Index count = 0;
  
  SARL_SET_ITERATOR_FOR(it_copy) {
    ++count;
  }
  
  sarl_set_iterator_decr_ref(it_copy);
  return count;
};

extern Sarl_Index  sarl_set_iterator_count_remaining(struct Sarl_SetIterator *a_it)
{
  Sarl_SetIterator *it_copy = sarl_set_iterator_copy(a_it);
  Sarl_Index count = 0;
  
  while(! sarl_set_iterator_at_end(it_copy) ) {
    ++count;
    sarl_set_iterator_next(it_copy);
  }
  
  sarl_set_iterator_decr_ref(it_copy);
  return count;
};

/*
 * X < Y iff first(X - Y) < first(Y - X)
 *
 * ret < 0 iff X < Y
 * ret = 0 iff X = Y
 * ret > 0 iff X > Y
 */
int sarl_set_iterator_lexical_compare(
  struct Sarl_SetIterator *x, struct Sarl_SetIterator *y
)
{
  Sarl_SetIterator *u, *v, *w;
  Sarl_SetIterator *a, *b, *c;
  int ret_val;

  w = sarl_set_iterator_minus(
    u = sarl_set_iterator_copy(x),
    v = sarl_set_iterator_copy(y));

  c = sarl_set_iterator_minus(
    a = sarl_set_iterator_copy(y),
    b = sarl_set_iterator_copy(x));

  sarl_set_iterator_reset(c);
  sarl_set_iterator_reset(w);
  
  if ( ! sarl_set_iterator_at_end(w) ) {
    if ( ! sarl_set_iterator_at_end(c) ) {
      ret_val = sarl_set_iterator_val(w) - 
	sarl_set_iterator_val(c);
    }
    else {
      ret_val = -1;
    }
  }
  else {
    if ( ! sarl_set_iterator_at_end(c) ) {
      ret_val = 1;
    }
    else {
      ret_val = 0;
    }
  }

  sarl_set_iterator_decr_ref(u);
  sarl_set_iterator_decr_ref(v);
  sarl_set_iterator_decr_ref(w);

  sarl_set_iterator_decr_ref(a);
  sarl_set_iterator_decr_ref(b);
  sarl_set_iterator_decr_ref(c);

  return ret_val;
};

int sarl_set_iterator_subset(
  struct Sarl_SetIterator *x, struct Sarl_SetIterator *y
)
{
  int ret_val = 1;
  Sarl_SetIterator *u = sarl_set_iterator_copy(x);
  Sarl_SetIterator *v = sarl_set_iterator_copy(y);

  sarl_set_iterator_reset(u);
  sarl_set_iterator_reset(v);

  SARL_SET_ITERATOR_FOR(u) {
    Sarl_Index val = sarl_set_iterator_val(u);
    sarl_set_iterator_next_gte(v, val);
    if ( sarl_set_iterator_val(v) != val ) {
      ret_val = 0;
      break;
    }
  }
  
  sarl_set_iterator_decr_ref(u);
  sarl_set_iterator_decr_ref(v);
  return ret_val;
};

  
  
  

  





