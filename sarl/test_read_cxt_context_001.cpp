extern "C" {
#include <sarl/global.h>
#include <sarl/read_cxt_context.h>
#include <sarl/dictionary.h>
#include <sarl/string.h>
#include <sarl/test.h>
#include <sarl/set_iterator.h>
#include <sarl/context.h>
#include <sarl/output_stream.h>
#include <sarl/input_stream.h>
#include <sarl/write_cxt_context.h>
#include <sarl/context_iterator.h>
}

#include <iostream>

using namespace std;

int main()
{
  Sarl_Dictionary* d = sarl_dictionary_create();
  Sarl_Context*    I = sarl_context_create();

  Sarl_String* err_string   
      = sarl_string_create();

  Sarl_OutputStream *errors 
      = sarl_output_stream_create_from_string(err_string);

  Sarl_String *input_a_file_name 
      = sarl_string_create_from_chars(
	  "test_read_context_001_a.cxt",
	  strlen("test_read_context_001_a.cxt")
      );
  
  Sarl_String *input_b_file_name 
      = sarl_string_create_from_chars(
	  "test_read_context_001_b.cxt",
	  strlen("test_read_context_001_b.cxt")
      );

  Sarl_String *output_file_name 
      = sarl_string_create_from_chars(
	  "test_read_context_001_c.cxt",
	  strlen("test_read_context_001_c.cxt")
      );

  Sarl_String *title = sarl_string_create();

  Sarl_InputStream* input_a = 
      sarl_input_stream_create_from_file_name(input_a_file_name);

  Sarl_InputStream* input_b = 
      sarl_input_stream_create_from_file_name(input_b_file_name);
  
  Sarl_OutputStream* output = 
      sarl_output_stream_create_from_file_name(output_file_name);
  
  if ( sarl_read_cxt_context(d, d, I, title, input_a, errors) != SARL_OK ) {
      cerr << "Error loading context" << endl;
  };

  if ( sarl_read_cxt_context(d, d, I, title, input_b, errors) != SARL_OK ) {
      cerr << "Error loading context" << endl;
  };

  Sarl_ContextIterator *K_it = sarl_context_iterator_create(I);
  sarl_write_cxt_context(d, d, K_it, title, output);

  sarl_input_stream_decr_ref(input_a);
  sarl_input_stream_decr_ref(input_b);
  sarl_output_stream_decr_ref(errors);
  sarl_string_decr_ref(err_string);
  sarl_string_decr_ref(output_file_name);
  sarl_string_decr_ref(input_a_file_name);
  sarl_string_decr_ref(input_b_file_name);
  sarl_string_decr_ref(title);
  sarl_output_stream_decr_ref(output);
  sarl_dictionary_decr_ref(d);
  sarl_context_decr_ref(I);
  sarl_context_iterator_decr_ref(K_it);

  return 0;
};

    
