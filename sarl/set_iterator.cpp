extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>

}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>
#include <sarl/test.h>

class EmptySetManager 
{
public:
  ~EmptySetManager()
  {
    if ( s_empty != 0 ) {
      sarl_set_decr_ref(s_empty);
    }
    s_empty = 0;
  };

  static Sarl_Set* empty_set() 
  {
    if ( s_empty == 0 ) {
      s_empty = sarl_set_create();
    }
    return s_empty;
  }
  

  static Sarl_Set* s_empty;
};

Sarl_Set* EmptySetManager::s_empty;

static EmptySetManager empty_set_manager;

struct Sarl_SetIterator *
sarl_set_iterator_create_empty()
{
  return sarl_set_iterator_create(EmptySetManager::empty_set());
};

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
  Sarl_SetIterator *w;
  Sarl_SetIterator *c;
  int ret_val;

  w = sarl_set_iterator_minus(x, y);
  c = sarl_set_iterator_minus(y, x);
  
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

  sarl_set_iterator_decr_ref(w);
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
  return sarl_iterator_obtain_ownership(it, sarl_set_iterator_copy);
};

struct Sarl_SetIterator *
  sarl_set_iterator_release_ownership(
    struct Sarl_SetIterator *it
  )
{
  return sarl_iterator_release_ownership(it);
};

Sarl_Index sarl_set_iterator_last(struct Sarl_SetIterator *A)
{
  Sarl_Index result = 0;
  SARL_SET_ITERATOR_FOR(A) {
    result = sarl_set_iterator_value(A);
  }
  return result;
};

bool sarl_set_iterator_is_member(
  struct Sarl_SetIterator *it, Sarl_Index v)
{
  bool result;
  
  it = sarl_set_iterator_obtain_ownership(it);
  sarl_set_iterator_reset(it);
  sarl_set_iterator_next_gte(it, v);
  result = 
    ( ! sarl_set_iterator_at_end(it) ) && sarl_set_iterator_value(it) == v;
  sarl_set_iterator_release_ownership(it);
  return result;
};

struct Sarl_SetIterator *
  sarl_set_iterator_cache_and_decr_ref(
    Sarl_SetIterator *it
  )
{
  Sarl_Set* S;
  Sarl_SetIterator* result;

  sarl_set_iterator_release_ownership(it);
  S = sarl_set_copy(it);
  result = sarl_set_iterator_create(S);
  sarl_set_decr_ref(S);
  sarl_set_iterator_decr_ref(it);
  return result;
};


  
