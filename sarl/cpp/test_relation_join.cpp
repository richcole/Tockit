#include <sarl/cpp/Relation.h>
#include <sarl/cpp/RelationIterator.h>
#include <sarl/test.h>

#include <iostream>

using namespace std;

int main()
{
  Relation s;

  int      i, j, LEN=10;
  
  for(i=1;i<LEN;++i) {
    for(j=1;j<LEN;++j) {
      if ( i % j == 0 ) {
        s.insert(i,j);
      }
    }
  }
  
  RelationIterator t1 = RelationIterator(s);
  RelationIterator t2 = RelationIterator(s);
  RelationIterator it = t1.join(t2);

  SetIterator it_intent_8 = it.intent(8);
  SetIterator t1_intent_8 = t1.intent(8);
  SARL_TEST_ASSERT(t1_intent_8.subset(it_intent_8));

  for(
    it.reset(), t1.reset(); 
    (! it.at_end()) && (! t1.at_end()); 
    it.next(), t1.next()
  )
  {
    SARL_TEST_ASSERT(it.value() == t1.value());
  }

  SARL_TEST_ASSERT_EQ(it.at_end(), t1.at_end());
  return 0;
};


