extern "C" {
#include <sarl/input_stream.h>
#include <sarl/test.h>
#include <sarl/set_iterator.h>
}

#include <string>
#include <sstream>
#include <set>

using namespace std;

int main()
{
  char const* test_string = 
    "This is a test string. The purpose is to test the use of dictionary "
    "class in sarl.";

  Sarl_String* sarl_string = 
    sarl_string_create_from_chars(test_string, strlen(test_string));

  Sarl_String* line_string = 
    sarl_string_create();

  Sarl_InputStream* sarl_stream = 
    sarl_input_stream_create_from_string(sarl_string);

  istringstream input(test_string);
  string word;
  set<string> word_set;

  sarl_input_stream_read_line(sarl_stream, line_string);

  SARL_TEST_ASSERT(sarl_string_eq(sarl_string, line_string));

  sarl_string_decr_ref(sarl_string);
  sarl_string_decr_ref(line_string);
  sarl_input_stream_decr_ref(sarl_stream);

  return 0;
};

    
