#ifndef SARL_INPUT_STREAM_H
#define SARL_INPUT_STREAM_H

#include <stdio.h>

struct Sarl_InputStream;
struct Sarl_String;

/* return zero on error */
extern int 
  sarl_input_stream_get_line(Sarl_InputStream* input, Sarl_String *s);

extern struct Sarl_InputStream* 
  sarl_input_stream_create_from_file(FILE *file);

extern struct Sarl_InputStream* 
  sarl_input_stream_create_from_file_name(Sarl_String *file_name);

extern struct Sarl_InputStream*
  sarl_input_stream_create_from_file_string(Sarl_String *s);



#endif
