#include <sarl/cpp/Context.h>
#include <sarl/cpp/SetIterator.h>
#include <iostream>

using namespace std;

int main()
{
  Context K;
  int i, j, LEN=20;
  
  for(i=1;i<=LEN;i++) {
    for(j=1;j<=LEN;j++) {
      if ( i % j == 0 ) {
        K.insert(i, j);
      }
    }
  }

  ContextIterator K_it(K);
  SetIterator A = K_it.extent(K_it.attributes());
  
  do { 
    cout << "Extent: ";
    for(A.reset(); ! A.at_end(); A.next()) {
      cout << A.value() << " ";
    }
    cout << endl;
    A = K_it.next_extent(A);
  }
  while ( A.count() != 0 );

};


