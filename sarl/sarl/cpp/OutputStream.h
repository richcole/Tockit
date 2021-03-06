#ifndef SARL_CPP_OUTPUT_STREAM_H
#define SARL_CPP_OUTPUT_STREAM_H

extern "C" {
#include <sarl/output_stream.h>
}

#include <sarl/cpp/Index.h>

class OutputStream {

  friend class ContextReader;

public: 

  inline OutputStream(String& string);
  inline OutputStream(OutputStream const& outputstream);
  inline OutputStream& operator=(OutputStream const& it);
  
  virtual ~OutputStream() {
    sarl_output_stream_decr_ref(mp_outRef);
  };

  inline void write_line(String const& s) {
    sarl_output_stream_write_line(mp_outRef, s.mp_stringRef);
  }

 private:
  Sarl_OutputStream* mp_outRef;

  OutputStream(Sarl_OutputStream* ap_ref) {
    mp_outRef = ap_ref;
  }
};

inline OutputStream::OutputStream(String &s)
{
  mp_outRef = sarl_output_stream_create_from_string(s.mp_stringRef);
};

inline OutputStream::OutputStream(OutputStream const& out) {
  mp_outRef = out.mp_outRef;
  sarl_output_stream_incr_ref(mp_outRef);
};

inline OutputStream& OutputStream::operator=(OutputStream const& out) {
  if ( this == &out || mp_outRef == out.mp_outRef) {
    return *this;
  }
  if ( mp_outRef != 0 ) {
    sarl_output_stream_decr_ref(mp_outRef);
  }
  mp_outRef = out.mp_outRef;
  sarl_output_stream_incr_ref(mp_outRef);
  return *this;
};

#endif
