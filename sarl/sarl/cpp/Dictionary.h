#ifndef SARL_CPP_DICTIONARY_H
#define SARL_CPP_DICTIONARY_H

extern "C" {
#include <sarl/dictionary.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/String.h>
#include <sarl/cpp/SetIterator.h>

class Dictionary {
  friend class ContextReader;
  

public: 

  inline Dictionary();
  inline Dictionary(Dictionary const& s);
  inline Dictionary& operator=(Dictionary const& it);

  virtual ~Dictionary() {
    sarl_dictionary_decr_ref(mp_dictionaryRef);
  };

  Index get_index(String const& s) {
    return sarl_dictionary_get_index(mp_dictionaryRef, s.mp_stringRef);
  };

  String get_string(Index index) const {
    return String(
      sarl_dictionary_get_string(mp_dictionaryRef, index)
    );
  };

  SetIterator get_indexes() const {
    return SetIterator(
      sarl_dictionary_get_indexes(mp_dictionaryRef)
    );
  };

  inline Dictionary copy() const;

 private:
  Sarl_Dictionary* mp_dictionaryRef;

  Dictionary(Sarl_Dictionary* ap_ref) {
    mp_dictionaryRef = ap_ref;
  }
};

inline Dictionary::Dictionary()
{
  mp_dictionaryRef = sarl_dictionary_create();
};

inline Dictionary::Dictionary(Dictionary const& s) {
  sarl_dictionary_incr_ref(s.mp_dictionaryRef);
  mp_dictionaryRef = s.mp_dictionaryRef;
};

inline Dictionary Dictionary::copy() const {
  return Dictionary(sarl_dictionary_copy(mp_dictionaryRef));
};

inline Dictionary& Dictionary::operator=(Dictionary const& d) {
    if ( d.mp_dictionaryRef == mp_dictionaryRef ) {
      return *this;
    }
    if ( mp_dictionaryRef != 0 ) {
      sarl_dictionary_decr_ref(mp_dictionaryRef);
    }
    mp_dictionaryRef = d.mp_dictionaryRef;
    if ( mp_dictionaryRef != 0 ) {
      sarl_dictionary_incr_ref(mp_dictionaryRef);
    }
    return *this;
};

#endif
