#include <sarl/cpp/Set.h>
#include <sarl/cpp/SetIterator.h>
#include <iostream>

using namespace std;


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
  
  SetIterator it_s = s;
  SetIterator it_t = t;
  
  SetIterator it = it_s.iterator_meet(it_t);
  SetIterator tmp;
  /*
  tmp = it;
  SetIterator *p_it = new SetIterator(tmp);
  
  if ( it.count_remaining() != LEN/6 ) {
    cerr << "Error, expected count_remaining=" << LEN/6 << ", but got ";
    cerr << it.count_remaining() << endl;
  }
  */

  return 0;
};


