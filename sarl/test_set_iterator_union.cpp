extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
  
}

#include <iostream>
using namespace std;


int main()
{
  int          LEN = 100;

  Index        i;
  Set          *A, *B;
  SetIterator  *x, *y, *z;

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

  sarl_set_iterator_reset(z);
  for(i=1;i<=LEN;i++) {
    if ( i % 2 == 0 || i % 3 == 0 ) {
      if ( sarl_set_iterator_at_end(z) ) {
	cerr << "i=" << i << ", z.at_end() == true" << endl;
	return -1;
      }
      if ( sarl_set_iterator_val(z) != i ) {
	cerr << "Mismatch i=" << i << ", z=" << sarl_set_iterator_val(z);
	cerr << endl;
	return -1;
      }
      sarl_set_iterator_next(z);
    }
  }
  
  sarl_set_iterator_decr_ref(x);
  sarl_set_iterator_decr_ref(y);

  if ( sarl_set_iterator_count(z) != LEN/3 + LEN/2 - LEN/6 ) {
    cerr << "Error, count value is incorrect, i=";
    cerr << sarl_set_iterator_count(z) << ", expected=";
    cerr << LEN/3 + LEN/2 - LEN/6 << "." << endl;
    return -1;
  }

  if ( sarl_set_iterator_count_remaining(z) != 0 ) {
    cerr << "Error, count remaining is incorrect." 
	 << sarl_set_iterator_count(z);
    cerr << endl;
    return -1;
  }

  sarl_set_iterator_decr_ref(z);
};
