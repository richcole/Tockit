extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/lectic.h>

#include <sarl/context_iterator.h>
  
}

struct Sarl_SetIterator*
  sarl_set_iterator_lectic_next(
    struct Sarl_SetIterator *A,
    struct Sarl_SetIterator *G
    )
{
  Sarl_SetIterator *result;

  Sarl_SetIterator *rem = sarl_set_iterator_minus(G,A);

  if ( sarl_set_iterator_at_end(rem) ) {
    Sarl_Set *empty_set = sarl_set_create();
    result = sarl_set_iterator_create(empty_set);
    sarl_set_decr_ref(empty_set);
  }
  else {
    Sarl_Index       first;
    Sarl_SetIterator *m;
    Sarl_SetIterator *i;
    Sarl_SetIterator *interval;
    
    first = sarl_set_iterator_value(rem);

    interval = sarl_set_iterator_interval(1, first-1);
    sarl_set_iterator_release_ownership(interval);
    
    m = sarl_set_iterator_minus(A, interval);
    sarl_set_iterator_release_ownership(m);

    i = sarl_set_iterator_interval(first, first);
    sarl_set_iterator_release_ownership(i);
    
    result = sarl_set_iterator_union(m, i);
    
    sarl_set_iterator_decr_ref(m);
    sarl_set_iterator_decr_ref(i);
    sarl_set_iterator_decr_ref(interval);
  };

  sarl_set_iterator_decr_ref(rem);
  return result;
};

struct Sarl_SetIterator*
  sarl_set_iterator_lectic_next_gte(
    struct Sarl_SetIterator *A,
    Sarl_Index               i,
    struct Sarl_SetIterator *G
    )
{
  Sarl_SetIterator *result;
  Sarl_SetIterator *rem = sarl_set_iterator_minus(G,A);

  sarl_set_iterator_next_gte(rem, i);
  if ( sarl_set_iterator_at_end(rem) ) {
    Sarl_Set *empty_set = sarl_set_create();
    result = sarl_set_iterator_create(empty_set);
    sarl_set_decr_ref(empty_set);
  }
  else {
    Sarl_Index       first;
    Sarl_SetIterator *m;
    Sarl_SetIterator *i;
    Sarl_SetIterator *interval;

    first = sarl_set_iterator_value(rem);

    interval = sarl_set_iterator_interval(1, first-1);
    sarl_set_iterator_release_ownership(interval);
    
    m = sarl_set_iterator_minus(A, interval);
    sarl_set_iterator_release_ownership(m);

    i = sarl_set_iterator_interval(first, first);
    sarl_set_iterator_release_ownership(i);
    
    result = sarl_set_iterator_union(m, i);
    
    sarl_set_iterator_decr_ref(m);
    sarl_set_iterator_decr_ref(i);
    sarl_set_iterator_decr_ref(interval);
  };

  sarl_set_iterator_decr_ref(rem);
  return result;
};


extern bool
  sarl_set_iterator_lectic_is_leq(
    struct Sarl_SetIterator *A,
    struct Sarl_SetIterator *B
  )
{
  Sarl_SetIterator *a_minus_b = sarl_set_iterator_minus(A, B);
  Sarl_SetIterator *b_minus_a = sarl_set_iterator_minus(B, A);
  bool result = false;
  
  if ( 
    sarl_set_iterator_at_end(a_minus_b) && 
    sarl_set_iterator_at_end(b_minus_a) 
  ) {
    result = true;
  }

  if ( 
    sarl_set_iterator_at_end(a_minus_b) && 
    ! sarl_set_iterator_at_end(b_minus_a) 
  ) {
    result = true;
  }

  if ( 
    ! sarl_set_iterator_at_end(a_minus_b) && 
    ! sarl_set_iterator_at_end(b_minus_a) 
  ) {
    result = sarl_set_iterator_value(a_minus_b) <
      sarl_set_iterator_value(b_minus_a);
  }

  sarl_set_iterator_decr_ref(a_minus_b);
  sarl_set_iterator_decr_ref(b_minus_a);

  return result;
};

extern bool
  sarl_set_iterator_lectic_is_le(
    struct Sarl_SetIterator *A,
    struct Sarl_SetIterator *B
  )
{
  Sarl_SetIterator *a_minus_b = sarl_set_iterator_minus(A, B);
  Sarl_SetIterator *b_minus_a = sarl_set_iterator_minus(B, A);
  bool result = false;
  
  if ( 
    sarl_set_iterator_at_end(a_minus_b) && 
    ! sarl_set_iterator_at_end(b_minus_a) 
  ) {
    result = true;
  }

  if ( 
    ! sarl_set_iterator_at_end(a_minus_b) && 
    ! sarl_set_iterator_at_end(b_minus_a) 
  ) {
    result = sarl_set_iterator_value(a_minus_b) <
      sarl_set_iterator_value(b_minus_a);
  }

  sarl_set_iterator_decr_ref(a_minus_b);
  sarl_set_iterator_decr_ref(b_minus_a);

  return result;
};

extern bool
  sarl_set_iterator_lectic_is_eq(
    struct Sarl_SetIterator *A,
    struct Sarl_SetIterator *B
  )
{
  Sarl_SetIterator *a_minus_b = sarl_set_iterator_minus(A, B);
  Sarl_SetIterator *b_minus_a = sarl_set_iterator_minus(B, A);
  bool result = false;
  
  if ( 
    sarl_set_iterator_at_end(a_minus_b) && 
    sarl_set_iterator_at_end(b_minus_a) 
  ) {
    result = true;
  }

  if ( 
    sarl_set_iterator_at_end(a_minus_b) && 
    sarl_set_iterator_at_end(b_minus_a) 
  ) {
    result = true;
  }

  sarl_set_iterator_decr_ref(a_minus_b);
  sarl_set_iterator_decr_ref(b_minus_a);

  return result;
};

  

