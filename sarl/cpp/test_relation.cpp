#include <sarl/cpp/Relation.h>
#include <sarl/cpp/RelationIterator.h>
#include <iostream>

using namespace std;


void main()
{
  Relation s;
  int i, j, LEN=100;
  
  for(i=1;i<=LEN;i++) {
    for(j=1;j<=LEN;j++) {
      if ( i % j == 0 ) {
	s.insert(i, j);
      }
    }
  }
  
  SetIterator it = RelationIterator(s).domain();

  if ( it.count_remaining() != LEN ) {
    cerr << "Error, expected count_remaining=" << LEN/6 << ", but got ";
    cerr << it.count_remaining();
  }
  
};


