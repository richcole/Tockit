#include <sarl/cpp/Dictionary.h>
#include <sarl/test.h>
#include <string.h>

main()
{
  Dictionary d;

  SARL_TEST_ASSERT_EQ(d.get_index("One"), d.get_index(String("One")));
  
  return 0;
};
