#ifndef SARL_INPUT_STREAM_H
#define SARL_INPUT_STREAM_H

#include <stdio.h>

#include <sarl/index.h>
#include <sarl/string.h>

struct Sarl_InputStream;
struct Sarl_String;

#define SARL_INPUT_STREAM_FOR(x) \
  for(;! sarl_input_stream_at_end(x);sarl_input_stream_next(x))

/* return zero on error */
extern int 
  sarl_input_stream_read_line(Sarl_InputStream* input, Sarl_String *s);

extern int
  sarl_input_stream_read_index(Sarl_InputStream* input, Sarl_Index* index_ptr);

extern Sarl_Char
  sarl_input_stream_char_value(Sarl_InputStream* input);

extern Sarl_Short
  sarl_input_stream_short_value(Sarl_InputStream* input);

extern void
  sarl_input_stream_next(Sarl_InputStream* input);

extern bool
  sarl_input_stream_at_end(Sarl_InputStream* input);

extern struct Sarl_InputStream* 
  sarl_input_stream_create_from_file(FILE *file);

extern struct Sarl_InputStream* 
  sarl_input_stream_create_from_file_name(Sarl_String *file_name);

extern struct Sarl_InputStream*
  sarl_input_stream_create_from_string(Sarl_String *s);

extern void
  sarl_input_stream_decr_ref(Sarl_InputStream*);

#endif
