#ifndef SARL_OUTPUT_STREAM_H
#define SARL_OUTPUT_STREAM_H

struct Sarl_OutputStream;

int sarl_output_stream_get_line(
  Sarl_OutputStream* output, Sarl_String *s);

Sarl_OutputStream* 
  sarl_output_stream_create_from_file(FILE *file);

Sarl_OutputStream* 
  sarl_output_stream_create_from_file_name(Sarl_String *file_name);

Sarl_OutputStream*
  sarl_output_stream_create_from_string(Sarl_String *s)


#endif
