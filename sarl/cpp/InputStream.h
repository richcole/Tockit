#ifndef SARL_CPP_INPUT_STREAM_H
#define SARL_CPP_INPUT_STREAM_H

extern "C" {
#include <sarl/input_stream.h>
}

#include <sarl/cpp/Index.h>

class InputStream {

public: 

  inline InputStream(String& string);
  inline InputStream(InputStream const& inputstream);
  inline InputStream& operator=(InputStream const& it);
  
  virtual ~InputStream() {
    sarl_input_stream_decr_ref(mp_inputstreamRef);
  };

 private:
  Sarl_InputStream* mp_inputstreamRef;

  InputStream(Sarl_Input_Stream* ap_ref) {
    mp_inputstreamRef = ap_ref;
  }
};

#include <sarl/cpp/InputStreamIterator.h>

inline InputStream::InputStream(String &s)
{
  mp_inputstreamRef = sarl_input_stream_create_from_string(s.mp_stringRef);
};

inline InputStream::InputStream(InputStream const& out) {
  mp_inputstreamRef = out.mp_inputstreamRef;
  sarl_input_stream_incr_ref(mp_inputstreamRef);
};

inline InputStream& InputStream::operator=(InputStream const& out) {
  if ( this == &out || mp_inputstreamRef == out.mp_inputstreamRef) {
    return *this;
  }
  if ( mp_inputstreamRef != 0 ) {
    sarl_input_stream_decr_ref(mp_inputstreamRef);
  }
  mp_inputstreamRef = out.mp_inputstreamRef;
  sarl_input_stream_incr_ref(mp_inputstreamRef);
  return *this;
};

#endif
