extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>
#include <sarl/test.h>

int sarl_set_iterator_is_empty(
  struct Sarl_SetIterator *it
)
{
  Sarl_SetIterator *tmp = sarl_set_iterator_obtain_ownership(it);
  bool             result;
  
  sarl_set_iterator_reset(tmp);
  result = sarl_set_iterator_at_end(tmp);
  sarl_set_iterator_decr_ref(tmp);

  return result;
};


/* functions delegating to the function table */
void  sarl_set_iterator_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value)
{
  it->funcs->next_gte(it, value);
}

void  sarl_set_iterator_next(struct Sarl_SetIterator *it)
{
  it->funcs->next(it);
}

Sarl_Index sarl_set_iterator_value(struct Sarl_SetIterator *it)
{
  return it->funcs->value(it);
}

int   sarl_set_iterator_at_end(struct Sarl_SetIterator *it)
{
  return it->funcs->at_end(it);
};

void  sarl_set_iterator_reset(struct Sarl_SetIterator *it) 
{
  it->funcs->reset(it);
};

void sarl_set_iterator_decr_ref(struct Sarl_SetIterator *it)
{
  it->funcs->decr_ref(it);
}

void sarl_set_iterator_incr_ref(struct Sarl_SetIterator *it)
{
  sarl_ref_count_incr(&it->ref_count);
};

struct Sarl_SetIterator* sarl_set_iterator_copy(
  struct Sarl_SetIterator *a_it)
{
  return a_it->funcs->copy(a_it);
};

Sarl_Index  sarl_set_iterator_count(struct Sarl_SetIterator *a_it)
{
  Sarl_SetIterator *it_copy = sarl_set_iterator_copy(a_it);
  Sarl_Index count = 0;
  
  SARL_SET_ITERATOR_FOR(it_copy) {
    ++count;
  }
  
  sarl_set_iterator_decr_ref(it_copy);
  return count;
};

Sarl_Index  sarl_set_iterator_count_remaining(
  struct Sarl_SetIterator *a_it)
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
      ret_val = sarl_set_iterator_value(w) - 
	sarl_set_iterator_value(c);
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
    Sarl_Index val = sarl_set_iterator_value(u);
    sarl_set_iterator_next_gte(v, val);
    if ( sarl_set_iterator_value(v) != val ) {
      ret_val = 0;
      break;
    }
  }
  
  sarl_set_iterator_decr_ref(u);
  sarl_set_iterator_decr_ref(v);
  return ret_val;
};

struct Sarl_SetIterator *
  sarl_set_iterator_obtain_ownership(
    struct Sarl_SetIterator *it
  )
{
  Sarl_SetIterator *result;
  
  if ( it->ownership == SARL_HAS_NO_OWNER ) {
    result = it;
    sarl_set_iterator_incr_ref(it);
  }
  else {
    result = sarl_set_iterator_copy(it);
    sarl_set_iterator_release_ownership(result);
  };
  
  SARL_TEST_ASSERT_EQ(result->ownership, SARL_HAS_NO_OWNER);
  result->ownership = SARL_HAS_OWNER;
  return result;
};


struct Sarl_SetIterator *
  sarl_set_iterator_release_ownership(
    struct Sarl_SetIterator *it
  )
{
  SARL_TEST_ASSERT_EQ(it->ownership, SARL_HAS_OWNER);
  it->ownership = SARL_HAS_NO_OWNER;
  return it;
};

Sarl_Index sarl_set_iterator_last(struct Sarl_SetIterator *A)
{
  Sarl_Index result = 0;
  SARL_SET_ITERATOR_FOR(A) {
    result = sarl_set_iterator_value(A);
  }
  return result;
};

