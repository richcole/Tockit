extern "C" {
#include <sarl/config.h>
#include <sarl/read_cxt_context.h>
#include <sarl/context.h>
#include <sarl/dictionary.h>
#include <sarl/string.h>
#include <sarl/input_stream.h>
#include <sarl/output_stream.h>
}

#include <fstream>

char *__file_must_start_with_a_B = "Error, file must start with a B.";
char *__title_not_found = "Error, title on line 2 not found.";



int sarl_read_cxt_format(
  Sarl_Dictionary*    G, 
  Sarl_Dictionary*    M, 
  Sarl_Context*       I,
  Sarl_String*        title,
  Sarl_InputStream*   input,
  Sarl_OutputStream*  errors
)
{
  Sarl_Index g, m, N_g, N_m;
  Sarl_String* buf = sarl_string_create();
  Sarl_String* tmp_title = 0;

  // read the first line
  if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
    if ( strcmp("B", sarl_string_get_chars(buf)) != 0 ) {
      Sarl_String* err_string = 
	sarl_string_create_from_chars(
	  __file_must_start_with_a_B, 
	  strlen(__file_must_start_with_a_B)
	);
      sarl_output_stream_write_line(errors, err_string);
      sarl_string_decr_ref(err_string);
      sarl_string_decr_ref(buf);
      return 0;
    }
  }
  else {
    Sarl_String* err_string = 
      sarl_string_create_from_chars(
	__file_must_start_with_a_B, 
	strlen(__file_must_start_with_a_B)
      );
    sarl_output_stream_write_line(errors, err_string);
    sarl_string_decr_ref(err_string);
    sarl_string_decr_ref(buf);
    return 0;
  }
    
  // read the first line
  if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
    name = buf;
  }
  else {
    Sarl_String* err_string = 
      sarl_string_create_from_chars("Error, name of file not found.");
    sarl_output_stream_write_line(errors, err_string);
    sarl_string_decr_ref(err_string);
    sarl_string_decr_ref(buf);
    return 0;
  }

  // read the number of objects
  if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
    istringstream line(buf);
    if ( (line >> N_g).fail() ) {
      err << "Error, couldn't read the number of objects";
      return 0;
    }
  }

  // read the number of attributes
  if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
    istringstream line(buf);
    if ( (line >> N_m).fail() ) {
      err << "Error, couldn't read the number of objects";
      return 0;
    }
  }

  // read the objects
  G.resize(N_g+1);
  for(g=1;g<=N_g;++g) {
    if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
      G[g] = buf;
    } 
    else {
      err << "Error reading object." << endl;
      return 0;
    }
    sarl_context_insert_object(K, g);
  }

  // read the attributes
  M.resize(N_m+1);
  for(m=1;m<=N_m;++m) {
    if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
      M[m] = buf;
    }
    else {
      err << "Error reading attribute." << endl;
      return 0;
    }
    sarl_context_insert_attribute(K, m);
  }

  // read the relation
  for(g=1;g<=N_g;++g) {
    if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
      for(m=1;m<=N_m;++m) {
        if ( buf[m-1] == 'x' ) {
          sarl_context_insert(K,g,m);
        }
        else if ( buf[m-1] != '.' ) {
          err << "Error reading object intent." << endl;
          return 0;
        }
      }
    }
  }

  return 1;

};

