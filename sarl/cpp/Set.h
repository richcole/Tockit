#ifndef SARL_CPP_SET_H
#define SARL_CPP_SET_H

extern "C" {
#include <sarl/set.h>
#include <sarl/set_iterator.h>
}

#include <sarl/cpp/Index.h>

class SetIterator;

class Set {

  friend class SetIterator;

public: 
  Set() {
    mp_setRef = sarl_set_create();
  };

  inline Set(Set const& set);
  inline Set(SetIterator const& it);
  inline Set& operator=(Set const& it);
  
  virtual ~Set() {
    sarl_set_decr_ref(mp_setRef);
  };

  inline void insert(Index x) {
    sarl_set_insert(mp_setRef, x);
  };

  inline void remove(Index x) {
    sarl_set_remove(mp_setRef, x);
  };

  Set copy() {
    return Set(*this);
  };

 private:
  Sarl_Set* mp_setRef;

  Set(Sarl_Set* ap_ref) {
    mp_setRef = ap_ref;
  }
};

#include <sarl/cpp/SetIterator.h>

inline Set::Set(Set const& set) {
  SetIterator it(set);
  mp_setRef = sarl_set_copy(it.mp_itRef);
};

inline Set::Set(SetIterator const& it) {
  mp_setRef = sarl_set_copy(it.mp_itRef);
};

inline Set& Set::operator=(Set const& it) {
  if ( it.mp_setRef == mp_setRef ) {
    return *this;
  }
  if ( mp_setRef != 0 ) {
    sarl_set_decr_ref(mp_setRef);
  }
  mp_setRef = it.mp_setRef;
  if ( mp_setRef != 0 ) {
    sarl_set_incr_ref(mp_setRef);
  }
  return *this;
};

#endif
