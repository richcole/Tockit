extern "C" {
#include <sarl/config.h>
#include <sarl/read_cxt_context.h>
#include <sarl/context.h>
#include <sarl/dictionary.h>
#include <sarl/string.h>
#include <sarl/input_stream.h>
#include <sarl/output_stream.h>
#include <sarl/write_cxt_context.h>
#include <sarl/context_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/relation_iterator.h>
}

#include <string.h>

int sarl_write_cxt_context(
  Sarl_Dictionary      *G, 
  Sarl_Dictionary      *M, 
  Sarl_ContextIterator *K, 
  Sarl_String          *title,
  Sarl_OutputStream    *output)
{
  Sarl_String *header = sarl_string_create_from_chars("B", strlen("B"));
  Sarl_String *empty_string = sarl_string_create();
  
  sarl_output_stream_write_line(output, header);
  sarl_output_stream_write_line(output, title);
  
  Sarl_SetIterator *objects = sarl_context_iterator_objects(K);
  sarl_output_stream_write_index(
    output, 
    sarl_set_iterator_count(objects)
  );
  sarl_output_stream_write_line(output, empty_string);
  
  Sarl_SetIterator *attributes = sarl_context_iterator_attributes(K);
  sarl_output_stream_write_index(
    output, 
    sarl_set_iterator_count(attributes)
  );
  sarl_output_stream_write_line(output, empty_string);
  
  Sarl_RelationIterator *I = sarl_context_iterator_incidence(K);

  SARL_SET_ITERATOR_FOR(objects) {
    Sarl_String *s 
      = sarl_dictionary_get_string(G, sarl_set_iterator_value(objects));
    sarl_output_stream_write_line(output, s);
    sarl_string_decr_ref(s);
  };
  
  SARL_SET_ITERATOR_FOR(attributes) {
    Sarl_String *s 
      = sarl_dictionary_get_string(M, sarl_set_iterator_value(attributes));
    sarl_output_stream_write_line(output, s);
    sarl_string_decr_ref(s);
  };
  
  SARL_SET_ITERATOR_FOR(objects) {
    SARL_SET_ITERATOR_FOR(attributes) {
      if ( 
	sarl_relation_iterator_is_member(
	  I, 
	  sarl_pair(
	    sarl_set_iterator_value(objects),
	    sarl_set_iterator_value(attributes)
	  )
	)
      ) {
	sarl_output_stream_write_char(output, 'x');
      }
      else {
	sarl_output_stream_write_char(output, '.');
      }
    }
    sarl_output_stream_write_line(output, empty_string);
  };

  sarl_string_decr_ref(header);
  sarl_string_decr_ref(empty_string);
  sarl_set_iterator_decr_ref(objects);
  sarl_set_iterator_decr_ref(attributes);
  sarl_relation_iterator_decr_ref(I);

  return SARL_OK;
};
