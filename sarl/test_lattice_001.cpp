extern "C" {

  #include <sarl/relation.h>
  #include <sarl/relation_iterator.h>
  #include <sarl/set_iterator.h>
  #include <sarl/set.h>
  #include <sarl/context_iterator.h>
  #include <sarl/lattice_iterator.h>
}

#include <sarl/test.h>
#include "args.cpp"

#include <string>

void print_set(std::string const& s, Sarl_SetIterator *curr) 
{
  fprintf(stdout, s.c_str());
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
  Sarl_SetIterator      *A, *B;

  Sarl_LatticeIterator  *it;

  int            i, j;
  int            LEN = 10;

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
  it   = sarl_lattice_iterator_create(K);

  SARL_LATTICE_ITERATOR_FOR(it) {
    A = sarl_lattice_iterator_extent(it);
    B = sarl_lattice_iterator_intent(it);
    print_set("Extent: ", A);
    print_set("Intent: ", B);
    sarl_set_iterator_decr_ref(A);
    sarl_set_iterator_decr_ref(B);
  };
  
  sarl_relation_decr_ref(r);
  sarl_relation_iterator_decr_ref(r_it);
  sarl_context_iterator_decr_ref(K);
  sarl_lattice_iterator_decr_ref(it);
};
