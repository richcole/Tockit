
#include <sarl/cpp/Set.h>
#include <sarl/cpp/SetIterator.h>

#include <iostream>
#include <stdlib.h> // exit()

using namespace std;

class SetIterator;

int main()
{
  Set s, t;
  int i, LEN=100;
  
  for(i=1;i<=LEN;i++) {
    if ( i % 2 == 0 ) {
      s.insert(i);
    }
    if ( i % 3 == 0 ) {
      t.insert(i);
    }
  }

  SetIterator it_s(s);
  SetIterator it_t(t);
  
  SetIterator it  = meet(it_s, it_t);
  
  it  = SetIteratorFunctions::meet(it_s, it_t);
  if ( it.count_remaining() != LEN/6 ) {
    cerr << "Error, expected count_remaining=" << LEN/6 << ", but got ";
    cerr << it.count_remaining() << endl;
    exit(-1);
  }

  return 0;
};


