#ifndef SARL_ITERATOR_IMPL_H
#define SARL_ITERATOR_IMPL_H

extern "C" {
  #include <sarl/ownership.h>
  #include <sarl/ref_count.h>
}

#include <sarl/test.h>

struct Sarl_Iterator
{
  Sarl_RefCount                  ref_count;
  Sarl_Ownership                 ownership;
};

inline void sarl_iterator_init(Sarl_Iterator* it)
{
  it->ownership = SARL_HAS_OWNER;
  sarl_ref_count_init(&it->ref_count);
};

inline void sarl_iterator_incr_ref(Sarl_Iterator* it)
{
  sarl_ref_count_incr(&it->ref_count);
};

template<class T>
  inline T* sarl_iterator_obtain_ownership(
    T *it, 
    T* (*copy)(T*)
  )
{
  T *result;
  
  if ( it->ownership == SARL_HAS_NO_OWNER ) {
    result = it;
    sarl_iterator_incr_ref(it);
  }
  else {
    result = (*copy)(it);
    sarl_iterator_release_ownership(result);
  };
  
  SARL_TEST_ASSERT_EQ(result->ownership, SARL_HAS_NO_OWNER);
  result->ownership = SARL_HAS_OWNER;
  return result;
};

template<class T>
inline T*
  sarl_iterator_release_ownership(
    T* it
  )
{
  SARL_TEST_ASSERT_EQ(it->ownership, SARL_HAS_OWNER);
  it->ownership = SARL_HAS_NO_OWNER;
  return it;
};

#endif
