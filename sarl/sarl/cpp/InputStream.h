#ifndef SARL_CPP_INPUT_STREAM_H
#define SARL_CPP_INPUT_STREAM_H

extern "C" {
#include <sarl/input_stream.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/String.h>

class InputStream {

  friend class ContextReader;

public: 

  inline InputStream(String const& string);
  inline InputStream(InputStream const& inputstream);
  inline InputStream& operator=(InputStream const& it);
  
  virtual ~InputStream() {
    sarl_input_stream_decr_ref(mp_inRef);
  };

  int read_index(Index& value) {
    return sarl_input_stream_read_index(mp_inRef, &value);
  };

 protected:
  Sarl_InputStream* mp_inRef;

  InputStream(Sarl_InputStream* ap_ref) {
    mp_inRef = ap_ref;
  }
};

inline InputStream::InputStream(String const& s)
{
  mp_inRef = sarl_input_stream_create_from_string(s.mp_stringRef);
};

inline InputStream::InputStream(InputStream const& out) {
  mp_inRef = out.mp_inRef;
  sarl_input_stream_incr_ref(mp_inRef);
};

inline InputStream& InputStream::operator=(InputStream const& out) {
  if ( this == &out || mp_inRef == out.mp_inRef) {
    return *this;
  }
  if ( mp_inRef != 0 ) {
    sarl_input_stream_decr_ref(mp_inRef);
  }
  mp_inRef = out.mp_inRef;
  sarl_input_stream_incr_ref(mp_inRef);
  return *this;
};

#endif
