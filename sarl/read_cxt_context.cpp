extern "C" {
#include <sarl/global.h>
#include <sarl/read_cxt_context.h>
#include <sarl/context.h>
#include <sarl/dictionary.h>
#include <sarl/string.h>
#include <sarl/input_stream.h>
#include <sarl/output_stream.h>
}

#include <fstream>

char *__file_must_start_with_a_B 
  = "Error, file must start with a B.";

char *__title_not_found 
  = "Error, title on line 2 not found.";

char *__unable_to_read_number_of_objects 
  = "Error, unable to read the number of objects.";

char *__unable_to_read_number_of_attributes 
  = "Error, unable to read the number of attributes.";

char *__unable_to_read_object 
  = "Error, unable to read object label";

char *__unable_to_read_attribute
  = "Error, unable to read attribute label";

char *__expected_dot_or_cross
  = "Error, expected either a dot or a cross at this point in the file.";

char *__not_enough_rows_in_context
  = "Error, not enough rows in context.";


static void report_error(Sarl_OutputStream* errors, char const* err_str) 
{
  Sarl_String* err_string = 
    sarl_string_create_from_chars(
      err_str, 
      strlen(err_str)
    );
  sarl_output_stream_write_line(errors, err_string);
  sarl_string_decr_ref(err_string);
};



int sarl_read_cxt_context(
  Sarl_Dictionary*    G, 
  Sarl_Dictionary*    M, 
  Sarl_Context*       I,
  Sarl_String*        title,
  Sarl_InputStream*   input,
  Sarl_OutputStream*  errors
)
{
  Sarl_Index i, g, m, N_g, N_m;
  Sarl_String* buf = sarl_string_create();
  Sarl_Index *M_map = 0, *G_map = 0;

  bool have_error = false;

  // read the first line
  if ( ! have_error ) {
    if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
      if ( strcmp("B", sarl_string_get_chars(buf)) != 0 ) {
	report_error(errors, __file_must_start_with_a_B);
	have_error = true;
      }
    }
    else {
      report_error(errors, __file_must_start_with_a_B);
      have_error = true;
    }
  }

  // read the first line
  if ( ! have_error ) {
    if ( sarl_input_stream_read_line(input, title) == SARL_ERROR ) {
      report_error(errors, __title_not_found);
      have_error = true;
    }
  }

  // read the number of objects
  if ( ! have_error ) {
    if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
      Sarl_InputStream* input_line = 
	sarl_input_stream_create_from_string(buf);
      if ( sarl_input_stream_read_index(input_line, &N_g) == SARL_ERROR ) {
	report_error(errors, __unable_to_read_number_of_objects);
	have_error = true;
      };
      sarl_input_stream_decr_ref(input_line);
    }
    else {
      report_error(errors, __unable_to_read_number_of_objects);
      have_error = true;
    };
  };
  
  // read the number of attributes
  if ( ! have_error ) {
    if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
      Sarl_InputStream* input_line = 
	sarl_input_stream_create_from_string(buf);
      if ( sarl_input_stream_read_index(input_line, &N_m) == SARL_ERROR ) {
	report_error(errors, __unable_to_read_number_of_attributes);
	have_error = true;
      };
      sarl_input_stream_decr_ref(input_line);
    }
  }
  else {
    report_error(errors, __unable_to_read_number_of_attributes);
    have_error = true;
  };
  
  // read the objects
  if ( ! have_error ) {
    G_map = new Sarl_Index[N_g];

    for(i=0;i<N_g;++i) {
      if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
	G_map[i] = sarl_dictionary_get_index(G, buf);
	sarl_context_insert_object(I, G_map[i]);
      }
      else {
	report_error(errors, __unable_to_read_object);
	have_error = true;
	break;
      }
    }
  };

  // read the attributes
  if ( ! have_error ) {
    M_map = new Sarl_Index[N_m];

    for(i=0;i<N_m;++i) {
      if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
	M_map[i] = sarl_dictionary_get_index(M, buf);
	sarl_context_insert_attribute(I, M_map[i]);
      }
      else {
	report_error(errors, __unable_to_read_attribute);
	have_error = true;
	break;
      }
    }
  };

  // read the relation
  for(g=0;!have_error && g<N_g;++g) {
    if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
      for(m=0;!have_error&&m<N_m;++m) {
	if ( m < sarl_string_length(buf) ) {
	  if ( sarl_string_get_chars(buf)[m] == 'x' ) {
	    sarl_context_insert(I, G_map[g], M_map[m]);
	  }
	  else if ( sarl_string_get_chars(buf)[m] != '.' ) {
	    report_error(errors, __expected_dot_or_cross);
	    have_error = true;
	  }
	}
	else {
	  report_error(errors, __expected_dot_or_cross);
	  have_error = true;
	}
      }
    }
    else {
      report_error(errors, __not_enough_rows_in_context);
      have_error = true;
    }
  }

  sarl_string_decr_ref(buf);

  if ( G_map != 0 ) {
    delete[] G_map;
  }
  if ( M_map != 0 ) { 
    delete[] M_map;
  };

  return (have_error ? SARL_ERROR : SARL_OK);
};

