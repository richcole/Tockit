#ifndef SARL_REF_COUNT_H
#define SARL_REF_COUNT_H

#include <stdio.h>  // fprintf()
#include <stdlib.h> // exit()

struct RefCount {
  size_t count;
};

struct RefCounter {
  RefCounter() { this->count = 0; };
  ~RefCounter() { 
    if ( this->count != 0 ) {
      fprintf(stderr, "Error, Reference Count=%d\n", this->count);
      exit(-1);
    }
  }
  int count;
};

extern struct RefCounter s_refCounter;

inline void sarl_ref_count_init(struct RefCount* ref_count)
{
  ref_count->count = 1;
  s_refCounter.count++;

#ifdef TRACE_REF_COUNT
  fprintf(stderr, "ref_count_init: %p -- %d\n", ref_count, ref_count->count);
#endif
}

inline void sarl_ref_count_incr(struct RefCount* ref_count)
{
  ref_count->count++;
  s_refCounter.count++;

#ifdef TRACE_REF_COUNT
  fprintf(stderr, "ref_count_incr: %p -- %d\n", ref_count, ref_count->count);
#endif
}

inline bool sarl_ref_count_decr(struct RefCount* ref_count)
{
  --ref_count->count;
  --s_refCounter.count;

#ifdef TRACE_REF_COUNT
  fprintf(stderr, "ref_count_decr: %p -- %d\n", ref_count, ref_count->count);
#endif

  return ( ref_count->count == 0 );
}

#endif
