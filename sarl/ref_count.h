#ifndef SARL_REF_COUNT_H
#define SARL_REF_COUNT_H

#include <stdio.h>

struct RefCount {
  size_t count;
};

inline void sarl_ref_count_init(struct RefCount* ref_count)
{
  ref_count->count = 1;
  fprintf(stderr, "ref_count_init: %p -- %d\n", ref_count, ref_count->count);
}

inline void sarl_ref_count_incr(struct RefCount* ref_count)
{
  ref_count->count++;
  fprintf(stderr, "ref_count_incr: %p -- %d\n", ref_count, ref_count->count);
}

inline bool sarl_ref_count_decr(struct RefCount* ref_count)
{
  --ref_count->count;
  fprintf(stderr, "ref_count_decr: %p -- %d\n", ref_count, ref_count->count);
  return ( ref_count->count == 0 );
}

#endif
