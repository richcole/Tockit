extern "C" {
#include <sarl/output_stream.h>
#include <sarl/test.h>
#include <sarl/set_iterator.h>
}

#include <string>
#include <sstream>
#include <set>

using namespace std;

int main()
{
  char test_string[100] = 
    "This is a test string. The purpose is to test the use of dictionary "
    "class in sarl.";

  Sarl_String* sarl_string = 
    sarl_string_create_from_chars(test_string, strlen(test_string));

  Sarl_String* line_string = 
    sarl_string_create();

  Sarl_String* empty_string = 
    sarl_string_create();

  Sarl_OutputStream* sarl_stream_one = 
    sarl_output_stream_create_from_string(line_string);

  Sarl_OutputStream* sarl_stream_two = 
    sarl_output_stream_create_from_string(sarl_string);

  sarl_output_stream_write_line(sarl_stream_one, sarl_string);
  sarl_output_stream_write_line(sarl_stream_two, empty_string);

  SARL_TEST_ASSERT(sarl_string_eq(sarl_string, line_string));

  strcat(test_string, "\n");
  SARL_TEST_ASSERT(strcmp(test_string, sarl_string_get_chars(sarl_string))==0);
  SARL_TEST_ASSERT(strcmp(test_string, sarl_string_get_chars(line_string))==0);
  

  sarl_string_decr_ref(empty_string);
  sarl_string_decr_ref(sarl_string);
  sarl_string_decr_ref(line_string);
  sarl_output_stream_decr_ref(sarl_stream_one);
  sarl_output_stream_decr_ref(sarl_stream_two);

  return 0;
};

    
