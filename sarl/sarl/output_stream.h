#ifndef SARL_OUTPUT_STREAM_H
#define SARL_OUTPUT_STREAM_H

#include <stdio.h>

#include <sarl/string.h>

struct Sarl_OutputStream;

extern int sarl_output_stream_write_line(
  Sarl_OutputStream* output, Sarl_String *s);

extern int sarl_output_stream_write_char(
  Sarl_OutputStream* output, Sarl_Char ch);

extern int sarl_output_stream_write_index(
    Sarl_OutputStream* output, Sarl_Index index);

extern Sarl_OutputStream* 
  sarl_output_stream_create_from_file(FILE *file);

extern Sarl_OutputStream* 
  sarl_output_stream_create_from_file_name(Sarl_String *file_name);

extern Sarl_OutputStream*
  sarl_output_stream_create_from_string(Sarl_String *s);

extern void
  sarl_output_stream_decr_ref(Sarl_OutputStream*);

extern void
  sarl_output_stream_incr_ref(Sarl_OutputStream*);

#endif
