#ifndef SARL_REF_COUNT_H
#define SARL_REF_COUNT_H

#include <stdio.h>  // fprintf()
#include <stdlib.h> // exit()

struct Sarl_RefCount {
  size_t count;
};

struct Sarl_RefCounter {
  Sarl_RefCounter() { this->count = 0; };
  ~Sarl_RefCounter() { 
    if ( this->count != 0 ) {
			fprintf(stderr, __FILE__ "(%d):\n", __LINE__);
      fprintf(stderr, "  Error, Reference Count=%d\n", this->count);
      exit(-1);
    }
  }
  int count;
};

extern struct Sarl_RefCounter s_refCounter;

inline void sarl_ref_count_init(struct Sarl_RefCount* ref_count)
{
  ref_count->count = 1;
  s_refCounter.count++;

#ifdef TRACE_REF_COUNT
  fprintf(stderr, "ref_count_init: %p -- %d\n", ref_count, ref_count->count);
#endif
}

inline void sarl_ref_count_incr(struct Sarl_RefCount* ref_count)
{
  ref_count->count++;
  s_refCounter.count++;

#ifdef TRACE_REF_COUNT
  fprintf(stderr, "ref_count_incr: %p -- %d\n", ref_count, ref_count->count);
#endif
}

inline bool sarl_ref_count_decr(struct Sarl_RefCount* ref_count)
{
  --ref_count->count;
  --s_refCounter.count;

#ifdef TRACE_REF_COUNT
  fprintf(stderr, "ref_count_decr: %p -- %d\n", ref_count, ref_count->count);
#endif

  return ( ref_count->count == 0 );
}

#endif
