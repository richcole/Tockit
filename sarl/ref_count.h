#ifndef SARL_REF_COUNT_H
#define SARL_REF_COUNT_H

#include <stdio.h>  // fprintf()
#include <stdlib.h> // exit()

#include <sarl/config.h>

#define SARL_ASSIGN(x,y) \
  if ( (x) != 0 ) sarl_ref_count_decr((x)->ref_count); \
  (x) = (y); \
  sarl_ref_count_incr((x)->ref_count); 

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
  if ( ref_count->count == 0 ) {
    SARL_FATAL_ERROR(
      "sarl_ref_count_decr called when ref count is already zero."
    );
  };

  --ref_count->count;
  --s_refCounter.count;

#ifdef TRACE_REF_COUNT
  fprintf(stderr, "ref_count_decr: %p -- %d\n", ref_count, ref_count->count);
#endif

  return ( ref_count->count == 0 );
}

#endif
