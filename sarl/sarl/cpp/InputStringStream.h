#ifndef SARL_CPP_INPUT_STRING_STREAM_H
#define SARL_CPP_INPUT_STRING_STREAM_H

extern "C" {
#include <sarl/input_stream.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/InputStream.h>

class InputStringStream : public InputStream {

public: 

  inline InputStringStream(String& string);
  inline InputStringStream(InputStringStream const& inputstringstream);
  inline InputStringStream& operator=(InputStringStream const& it);
  
  virtual ~InputStringStream() {
  };

 private:
  InputStringStream(Sarl_InputStream* ap_ref) : InputStream(ap_ref) 
  {
  };
};

inline InputStringStream::InputStringStream(String &s)
    : InputStream(
        sarl_input_stream_create_from_string(s.mp_stringRef)
    )
{
};

inline InputStringStream::InputStringStream(InputStringStream const& out) 
    : InputStream(out)
{
};

inline InputStringStream& InputStringStream::operator=(InputStringStream const& out) {
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
