#ifndef SARL_CPP_SET_H
#define SARL_CPP_SET_H

extern "C" {
#include <sarl/set.h>
#include <sarl/set_iterator.h>
}

typedef unsigned int Index;
// #include <sarl/cpp/Index.h>

class SetIterator;

class Set {
  friend class SetIterator;
 public: 
  Set() {
    mp_setRef = sarl_set_create();
  };

  Set(Set const& it) {
    mp_setRef = 0;
    *this = it;
  }

  Set& operator=(Set const& it) {
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
  }

  virtual ~Set() {
    sarl_set_decr_ref(mp_setRef);
  };

  inline void insert(Index x) {
    sarl_set_insert(mp_setRef, x);
  };

  inline void remove(Index x) {
    sarl_set_remove(mp_setRef, x);
  };

  inline Set copy() {
    Sarl_Set* p_ref;
    p_ref = sarl_set_copy(sarl_set_iterator_create(mp_setRef));
    return Set(p_ref);
  }

 private:
  Sarl_Set* mp_setRef;

  Set(Sarl_Set* ap_ref) {
    mp_setRef = ap_ref;
  }
};

#endif
