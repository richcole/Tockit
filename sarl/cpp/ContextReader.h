#ifndef SARL_CPP_CONTEXT_READER_H
#define SARL_CPP_CONTEXT_READER_H

class ContextReader {
 public:
  static void read(InputStream &in, Lattice& L, Dictionary& d) {
    Context K;
    sarl_read_cxt_context(

#endif
