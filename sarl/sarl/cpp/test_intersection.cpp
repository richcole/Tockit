
#include <sarl/cpp/Set.h>
#include <sarl/cpp/SetIterator.h>

extern "C" {
  #include <sarl/test.h>
}

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
  SARL_TEST_ASSERT_EQ(it.count_remaining(), LEN/6);
  
  it  = SetIteratorFunctions::meet(it_s, it_t);
  SARL_TEST_ASSERT_EQ(it.count_remaining(), LEN/6);

  SARL_TEST_ASSERT_EQ(
    SetIterator(SetIteratorFunctions::meet(it_s, it_t)).count_remaining(),
    LEN/6
  );

  return 0;
};


