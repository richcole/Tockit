#ifndef SARL_READ_CXT_FORMAT_H
#define SARL_READ_CXT_FORMAT_H

struct Sarl_Dictionary;
struct Sarl_Context;
struct Sarl_String;
struct Sarl_InputStream;
struct Sarl_OutputStream;

/*! Return zero unless there is a syntax error, in which case -1 is returned
 *  and G, M, I and title have undefined values. */
int sarl_read_cxt_format(
  Sarl_Dictionary*  G, 
  Sarl_Dictionary*  M, 
  Sarl_Context*     I, 
  Sarl_String*      title,
  Sarl_InputStream* input,
  Sarl_OutputStream* output
);

#endif
