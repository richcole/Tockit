#ifndef SARL_CPP_CONTEXT_READER_H
#define SARL_CPP_CONTEXT_READER_H

extern "C" {
#include "sarl/read_cxt_context.h"
#include "sarl/global.h"
}

#include <sarl/cpp/InputStream.h>
#include <sarl/cpp/OutputStream.h>
#include <sarl/cpp/Dictionary.h>
#include <sarl/cpp/String.h>
#include <sarl/cpp/Context.h>
#include <sarl/cpp/LatticeIterator.h>
#include <sarl/cpp/Lattice.h>
#include <sarl/cpp/InputFileStream.h>
#include <sarl/cpp/OutputStream.h>

class ContextReader {
 public:
  ContextReader() {};

  static int read_cxt(
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

  static int read_cxt(
      InputStream &in, 
      Context&    K, 
      String&     title,
      Dictionary& G, 
      Dictionary& M,
      OutputStream& err
      ) 
  {
    int result = sarl_read_cxt_context(
      G.mp_dictionaryRef, 
      M.mp_dictionaryRef, 
      K.mp_contextRef, 
      title.mp_stringRef, 
      in.mp_inRef, 
      err.mp_outRef
    );

    return result;
  };

  static Lattice read_cxt_lattice_from_file(String& s) 
  {
    InputFileStream in = s;
    String title;
    Dictionary D;
    String err_string;
    OutputStream err(err_string);

    Lattice L;
    
    read_cxt(in, L, title, D, D, err);
    return L;
  };
};

    

#endif
