#include <sarl/cpp/Context.h>
#include <sarl/cpp/SetIterator.h>

extern "C" {
#include <sarl/test.h>
}

#include <iostream>

using namespace std;

int main()
{
  Context K;
  int i, j, LEN=30;
  
  for(i=1;i<=LEN;i++) {
    for(j=1;j<=LEN;j++) {
      if ( i % j == 0 ) {
        K.insert(i, j);
      }
    }
  }

  ContextIterator K_it(K);
  SetIterator A = K_it.extent(K_it.attributes());

  int concept_count = 0;
  
  do { 
    A = K_it.next_extent(A);
    ++concept_count;
  }
  while ( A.count() != 0 );

  SARL_TEST_ASSERT_EQ(concept_count, LEN + 1);
};


