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

void usage()
{
    cerr << "Usage: context_sum input_a input_b output" << endl;
    exit(-1);
};

int main(int num_args, char **args)
{
  if ( num_args != 4 ) {
    usage();
  };

  Sarl_Dictionary* d = sarl_dictionary_create();
  Sarl_Context*    I = sarl_context_create();

  Sarl_String* err_string   
      = sarl_string_create();

  Sarl_OutputStream *errors 
      = sarl_output_stream_create_from_string(err_string);

  Sarl_String *input_a_file_name 
      = sarl_string_create_from_chars(
	  args[1],
	  strlen(args[1])
      );
  
  Sarl_String *input_b_file_name 
      = sarl_string_create_from_chars(
	  args[2],
	  strlen(args[2])
      );

  Sarl_String *output_file_name 
      = sarl_string_create_from_chars(
	  args[3],
	  strlen(args[3])
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

    
