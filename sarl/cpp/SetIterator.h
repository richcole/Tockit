#ifndef SARL_CPP_SET_ITERATOR_H
#define SARL_CPP_SET_ITERATOR_H

extern "C" {
#include <sarl/set_iterator.h>
}

// #include <sarl/cpp/Index.h>
#include <sarl/cpp/Set.h>

class RelationIterator;

class SetIterator {

 public:
  SetIterator(Set const& set) {
    mp_itRef = sarl_set_iterator_create(set.mp_setRef);
  };

  SetIterator() {
    // very bad hack to allow SWIG to work
    mp_itRef = 0;
  }

  SetIterator(SetIterator const& it) {
    mp_itRef = 0;
    *this = it;
  }

  SetIterator& operator=(SetIterator const& it) {
    if ( it.mp_itRef == mp_itRef ) {
      return *this;
    }
    if ( mp_itRef != 0 ) {
      sarl_set_iterator_decr_ref(mp_itRef);
    }
    mp_itRef = it.mp_itRef;
    if ( mp_itRef != 0 ) {
      sarl_set_iterator_incr_ref(mp_itRef);
    }
    return *this;
  }

  virtual ~SetIterator() {
    if ( mp_itRef != 0 ) {
      sarl_set_iterator_decr_ref(mp_itRef);
    }
  };

  SetIterator copy() {
    return SetIterator(sarl_set_iterator_copy(mp_itRef));
  }

  void next() {
    sarl_set_iterator_next(mp_itRef);
  };

  void next_gte(Index v) {
    sarl_set_iterator_next_gte(mp_itRef, v);
  };

  Index value() {
    return sarl_set_iterator_val(mp_itRef);
  };

  bool at_end() {
    return sarl_set_iterator_at_end(mp_itRef);
  };

  void reset() {
    return sarl_set_iterator_reset(mp_itRef);
  };

  Index count_remaining() {
    return sarl_set_iterator_count_remaining(mp_itRef);
  }

  Index count() {
    return sarl_set_iterator_count(mp_itRef);
  }

  SetIterator iterator_meet(SetIterator& a_it) {
    Sarl_SetIterator *p_it = 
      sarl_set_iterator_meet(mp_itRef, a_it.mp_itRef);
    return SetIterator(p_it);
  }

  SetIterator iterator_union(SetIterator& a_it) {
    Sarl_SetIterator *p_it = 
      sarl_set_iterator_union(mp_itRef, a_it.mp_itRef);
    return SetIterator(p_it);
  }

  SetIterator iterator_minus(SetIterator& a_it) {
    Sarl_SetIterator *p_it = 
      sarl_set_iterator_minus(mp_itRef, a_it.mp_itRef);
    return SetIterator(p_it);
  }

  bool subset(SetIterator& a_it)
  {
    return
      sarl_set_iterator_subset(mp_itRef, a_it.mp_itRef);
  };

  int lexical_compare(SetIterator& a_it)
  {
    return
      sarl_set_iterator_lexical_compare(mp_itRef, a_it.mp_itRef);
  };

 private:
  Sarl_SetIterator* mp_itRef;

  SetIterator(Sarl_SetIterator* ap_itRef) {
    mp_itRef = ap_itRef;
  }

  friend class RelationIterator;
};

#endif
