#include <sarl/cpp/Map.h>
#include <sarl/test.h>
#include <string.h>

main()
{
  Map m;

  m.insert(1,2);
  SARL_TEST_ASSERT_EQ(m.image(1), 2);

  m.insert(1,3);
  SARL_TEST_ASSERT_EQ(m.image(1), 3);
  
  return 0;
};
