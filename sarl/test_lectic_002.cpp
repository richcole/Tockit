extern "C" {

  #include <sarl/relation.h>
  #include <sarl/relation_iterator.h>
  #include <sarl/set_iterator.h>
  #include <sarl/set.h>
  #include <sarl/context_iterator.h>
  #include <sarl/lectic.h>
  #include <sarl/test.h>
  
}
#include <sys/time.h>
#include <sarl/test.h>
#include <iostream>
#include "args.cpp"

using namespace std;


void print_extent(Sarl_SetIterator *curr) 
{
  fprintf(stdout, "Extent: ");
  SARL_SET_ITERATOR_FOR(curr) {
    fprintf(stdout, "%d ", sarl_set_iterator_value(curr));
  };
  fprintf(stdout, "\n", sarl_set_iterator_value(curr));
};

int main(int num_args, char **args)
{
  Sarl_Relation         *r;
  Sarl_RelationIterator *r_it;
  
  Sarl_ContextIterator  *K;
  Sarl_Set              *empty;
  Sarl_SetIterator      *curr, *next;

  Sarl_SetIterator      *M;

  int            i, j;
  int            LEN = 50;
  int            concept_count;

  r     = sarl_relation_create();

  for(i=1;i<=LEN;i++) {
    for(j=1;j<=LEN;j++) {
      if ( j % i == 0 ) {
        sarl_relation_insert(r, i, j);
      }
    }
  }

  r_it = sarl_relation_iterator_create(r);
  K    = sarl_context_iterator_create_from_relation(r_it);
  M    = sarl_context_iterator_attributes(K);
  sarl_set_iterator_release_ownership(M);
  curr = sarl_context_iterator_extent_set(K, M);

  concept_count = 0;
  timeval t1, t2;
  gettimeofday(&t1, 0);
  do {
    ++concept_count;
    sarl_set_iterator_release_ownership(curr);
    next = sarl_context_iterator_next_extent(K, curr);
    sarl_set_iterator_decr_ref(curr);
    curr = next;
    
  } while ( curr != 0 );
  gettimeofday(&t2, 0);
  
  cerr << "Time: " << (((t2.tv_sec - t1.tv_sec) * 1000.0 * 1000.0)
    + (t2.tv_usec - t1.tv_usec))/(1000.0*10000.0) << endl;

  sarl_relation_decr_ref(r);
  sarl_relation_iterator_decr_ref(r_it);
  sarl_context_iterator_decr_ref(K);
  sarl_set_iterator_decr_ref(M);

  SARL_TEST_ASSERT_EQ(concept_count, LEN+1);
};
