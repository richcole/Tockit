#ifndef SARL_WRITE_CXT_CONTEXT_H
#define SARL_WRITE_CXT_CONTEXT_H

struct Sarl_Dictionary;
struct Sarl_ContextIterator;
struct Sarl_String;
struct Sarl_InputStream;
struct Sarl_OutputStream;

/*! Return zero unless there is a syntax error, in which case -1 is returned
 *  and G, M, I and title have undefined values. */
int sarl_write_cxt_context(
  Sarl_Dictionary      *G, 
  Sarl_Dictionary      *M, 
  Sarl_ContextIterator *K, 
  Sarl_String          *title,
  Sarl_OutputStream    *output);

#endif
