#ifndef SARL_CPP_STRING_H
#define SARL_CPP_STRING_H

extern "C" {
#include <sarl/string.h>
#include <sarl/string_iterator.h>
}

#include <sarl/cpp/Index.h>

class Set;
class Relation;
class RelationIterator;
class ConceptIterator;
class StringIterator;
class LatticeIterator;
class Dictionary;

class String {

  friend class Dictionary;
  friend class OutputStream;
  friend class InputStream;
  friend class InputFileStream;
  friend class InputStringStream;
  friend class ContextReader;

protected:
  inline Sarl_Index string_length(Sarl_Char const* s) 
  {
    Sarl_Index result = 0;
    while( *s ) ++result, ++s;
    return result;
  };

  inline Sarl_Index string_length(Sarl_Short const* s) 
  {
    Sarl_Index result = 0;
    while( *s ) ++result, ++s;
    return result;
  };

public: 

  inline String();
  inline String(String const& s);
  inline String(Sarl_Char  const* s);
  inline String(Sarl_Short const* s);
  inline String& operator=(String const& it);

  virtual ~String() {
    sarl_string_decr_ref(mp_stringRef);
  };

  inline void set_chars(Sarl_Char const* s) {
    sarl_string_set_chars(mp_stringRef, s, string_length(s));
  };

  inline void set_shorts(Sarl_Short const* s) {
    sarl_string_set_shorts(mp_stringRef, s, string_length(s));
  };

  inline Sarl_Char const* get_chars() {
    return sarl_string_get_chars(mp_stringRef);
  };

  inline Sarl_Short const* get_shorts() {
    return sarl_string_get_shorts(mp_stringRef);
  };

  inline Sarl_Index length() {
    return sarl_string_length(mp_stringRef);
  }

  inline void append_chars(Sarl_Char *t) {
    return sarl_string_append_chars(mp_stringRef, t);
  };

  String copy() {
    return String(*this);
  };

 private:
  Sarl_String* mp_stringRef;

  String(Sarl_String* ap_ref) {
    mp_stringRef = ap_ref;
  }
};

inline String::String()
{
  mp_stringRef = sarl_string_create();
};

inline String::String(String const& s) {
  mp_stringRef = sarl_string_copy(s.mp_stringRef);
};

inline String::String(Sarl_Char  const* s) {
  mp_stringRef = sarl_string_create_from_chars(s, string_length(s));
};

inline String::String(Sarl_Short const* s) {
  mp_stringRef = sarl_string_create_from_shorts(s, string_length(s));
};

inline String& String::operator=(String const& K) {
  if ( this == &K ) {
    return *this;
  }
  if ( mp_stringRef != 0 ) {
    sarl_string_decr_ref(mp_stringRef);
  }
  mp_stringRef = sarl_string_copy(K.mp_stringRef);
  return *this;
};

#endif
