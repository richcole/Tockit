#ifndef SARL_CPP_INPUT_FILE_STREAM_H
#define SARL_CPP_INPUT_FILE_STREAM_H

extern "C" {
#include <sarl/input_stream.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/InputStream.h>

class InputFileStream : public InputStream {

public: 

  inline InputFileStream(String& string);
  inline InputFileStream(InputFileStream const& inputfilestream);
  inline InputFileStream& operator=(InputFileStream const& it);
  
  virtual ~InputFileStream() {
    sarl_input_stream_decr_ref(mp_inRef);
  };

 private:
  Sarl_InputFileStream* mp_inRef;

  InputFileStream(Sarl_Input_Stream* ap_ref) {
    mp_inRef = ap_ref;
  }
};

#include <sarl/cpp/InputFileStreamIterator.h>

inline InputFileStream::InputFileStream(String &s)
{
  mp_inRef = sarl_input_stream_create_from_file_name(s.mp_stringRef);
};

inline InputFileStream::InputFileStream(InputFileStream const& out) {
  mp_inRef = out.mp_inRef;
  sarl_input_stream_incr_ref(mp_inRef);
};

inline InputFileStream& InputFileStream::operator=(InputFileStream const& out) {
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
