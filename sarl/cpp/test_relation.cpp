#include <sarl/cpp/Relation.h>
#include <sarl/cpp/RelationIterator.h>
#include <sarl/test.h>
#include <iostream>

using namespace std;


int main()
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

  SARL_TEST_ASSERT_EQ(it.count_remaining(), LEN);
  
  int val = RelationIterator(s).range().count_remaining();
  
  SARL_TEST_ASSERT_EQ(val, LEN);

  Relation t;

  t = s;
  
  s.insert(LEN*2, LEN*2);

  SARL_TEST_ASSERT_EQ(
    RelationIterator(s).count(), 
    RelationIterator(t).count()
  );

  return 0;
};


