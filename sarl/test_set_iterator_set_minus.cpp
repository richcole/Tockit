extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
  
}

#include <iostream>
using namespace std;


int main()
{
  int          LEN = 100;

  Sarl_Index        i;
  Sarl_Set          *A, *B, *C;
  Sarl_SetIterator  *x, *y, *z;
  Sarl_SetIterator  *u, *v, *w;

  A = sarl_set_create();
  B = sarl_set_create();

  for(i=1;i<=LEN;i++) {
    if (i % 3 == 0)
      sarl_set_insert(A, i);
    if (i % 2 == 0)
      sarl_set_insert(B, i);
  }

  x = sarl_set_iterator_create(A);
  y = sarl_set_iterator_create(B);
  sarl_set_decr_ref(A);
  sarl_set_decr_ref(B);

  z = sarl_set_iterator_union(x, y);
  C = sarl_set_copy(z);
  
  w = sarl_set_iterator_create(A);
  u = sarl_set_iterator_create(C);
  v = sarl_set_iterator_minus(u, w);

  if ( sarl_set_iterator_lexical_compare(z, w) >= 0 ) {
    cerr << "Error, lexical_compare(z,w)=";
    cerr << sarl_set_iterator_lexical_compare(z, w);
    cerr << ", expected -1" << endl;
  };

  if ( sarl_set_iterator_lexical_compare(u, z) != 0 ) {
    cerr << "Error, lexical_compare(u,z)=";
    cerr << sarl_set_iterator_lexical_compare(u, z);
    cerr << ", expected 0" << endl;
  };

  if ( sarl_set_iterator_lexical_compare(y, v) >= 0 ) {
    cerr << "Error, lexical_compare(y,v)=";
    cerr << sarl_set_iterator_lexical_compare(y, v);
    cerr << ", expected 0" << endl;
  };
  

  for(i=1;i<=LEN;i++) {
    if ( i % 2 == 0 && i % 3 != 0) {
      if ( sarl_set_iterator_at_end(v) ) {
	cerr << "i=" << i << ", v.at_end() == true" << endl;
	return -1;
      }
      if ( sarl_set_iterator_val(v) != i ) {
	cerr << "Mismatch i=" << i << ", v=" << sarl_set_iterator_val(v);
	cerr << endl;
	return -1;
      }
      sarl_set_iterator_next(v);
    }
  }
  
  sarl_set_iterator_decr_ref(x);
  sarl_set_iterator_decr_ref(y);
  sarl_set_iterator_decr_ref(u);
  sarl_set_iterator_decr_ref(w);
  sarl_set_decr_ref(C);

  if ( sarl_set_iterator_count(v) != LEN/2 - LEN/6 ) {
    cerr << "Error, count value is incorrect, i=";
    cerr << sarl_set_iterator_count(v) << ", expected=";
    cerr << LEN/3 + LEN/2 - LEN/6 << "." << endl;
    return -1;
  }

  if ( sarl_set_iterator_count_remaining(v) != 0 ) {
    cerr << "Error, count remaining is incorrect." 
	 << sarl_set_iterator_count(v);
    cerr << endl;
    return -1;
  }

  sarl_set_iterator_decr_ref(v);
  sarl_set_iterator_decr_ref(z);

  return 0;
};
