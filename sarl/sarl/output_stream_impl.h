#ifndef SARL_OUTPUT_STREAM_ITERATOR_IMPL_H
#define SARL_OUTPUT_STREAM_ITERATOR_IMPL_H

#include <sarl/iterator_impl.h>
#include <iostream>

struct Sarl_OutputStreamFunctionTable {
  int  (*write_line)(struct Sarl_OutputStream *, struct Sarl_String *);
  int  (*write_char)(Sarl_OutputStream* output, Sarl_Char ch);
  void  (*decr_ref)(struct Sarl_OutputStream *);
};

struct Sarl_OutputStream : Sarl_Iterator
{
  Sarl_OutputStreamFunctionTable* funcs;
};

struct Sarl_OutputStreamString : Sarl_OutputStream
{
  Sarl_String* s;
};

struct Sarl_OutputStreamFile : Sarl_OutputStream
{
  std::ostream* output;
};

inline void sarl_output_stream_init(
  struct Sarl_OutputStream *output,
  Sarl_OutputStreamFunctionTable *ap_funcs)
{
  output->funcs = ap_funcs;
  output->ownership = SARL_HAS_OWNER;
  sarl_ref_count_init(&output->ref_count);
}

#endif
