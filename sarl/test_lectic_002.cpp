extern "C" {

  #include <sarl/relation.h>
  #include <sarl/relation_iterator.h>
  #include <sarl/set_iterator.h>
  #include <sarl/set.h>
  #include <sarl/context_iterator.h>
  #include <sarl/lectic.h>
  
}

#include <sarl/test.h>
#include "args.cpp"

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

  int            i, j;
  int            LEN = 10;

  empty = sarl_set_create();
  r     = sarl_relation_create();

  for(i=1;i<=LEN;i++) {
    for(j=1;j<=LEN;j++) {
      if ( i % j == 0 ) {
        sarl_relation_insert(r, i, j);
      }
    }
  }

  r_it = sarl_relation_iterator_create(r);
  K    = sarl_context_iterator_create_from_relation(r_it);
  curr = sarl_set_iterator_create(empty);

  do {
    sarl_set_iterator_release_ownership(curr);
    next = sarl_context_iterator_next_extent(curr, K);
    sarl_set_iterator_decr_ref(curr);
    curr = next;
    
    print_extent(curr);
  } while ( sarl_set_iterator_count(curr) != LEN );

  sarl_relation_decr_ref(r);
  sarl_relation_iterator_decr_ref(r_it);
  sarl_context_iterator_decr_ref(K);
  sarl_set_decr_ref(empty);
  
  sarl_set_iterator_decr_ref(curr);
};