#include <sarl/cpp/String.h>
#include <sarl/test.h>
#include <string.h>

main()
{
  String s("one");
  String t;
  
  t = s;

  SARL_TEST_ASSERT_EQ(
    strcmp(t.get_chars(), s.get_chars()),
    0
  );
  
  return 0;
};
