#ifndef SARL_SET_ITERATOR_H
#define SARL_SET_ITERATOR_H

#include <sarl/index.h>

struct Aggregate SetIterator;
struct Set;

/* iterator macro */
#define SARL_SET_ITERATOR_FOR(x) \
  for( \
    sarl_set_iterator_reset(x);   \
    !sarl_set_iterator_at_end(x); \
    sarl_set_iterator_next(x) \
  )

