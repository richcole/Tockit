#ifndef SARL_CPP_CONTEXT_READER_H
#define SARL_CPP_CONTEXT_READER_H

extern "C" {
#include "sarl/read_cxt_context.h"
#include "sarl/config.h"
}

#include <sarl/cpp/InputStream.h>
#include <sarl/cpp/OutputStream.h>
#include <sarl/cpp/Dictionary.h>
#include <sarl/cpp/String.h>
#include <sarl/cpp/Context.h>
#include <sarl/cpp/LatticeIterator.h>
#include <sarl/cpp/Lattice.h>

class ContextReader {
 public:
  ContextReader() {};

  int read_cxt(
      InputStream &in, 
      Lattice&    L, 
      String&     title,
      Dictionary& G, 
      Dictionary& M,
      OutputStream& err
      ) 
    {
      Context K;
      int result = sarl_read_cxt_context(
        G.mp_dictionaryRef, 
        M.mp_dictionaryRef, 
        K.mp_contextRef, 
        title.mp_stringRef, 
        in.mp_inRef, 
        err.mp_outRef
      );

      if ( result == SARL_OK ) {
        LatticeIterator it(K);
        L = Lattice(it);
      }
      
      return result;
    };
};

    

#endif
