#ifndef SARL_CPP_SET_ITERATOR_H
#define SARL_CPP_SET_ITERATOR_H

extern "C" {
#include <sarl/set_iterator.h>
}

#include <sarl/cpp/Index.h>

class RelationIterator;
class Set;

class SetIterator {

  friend class RelationIterator;
  friend class Set;

  friend SetIterator meet(SetIterator& a_it, SetIterator& b_it);
  friend SetIterator join(SetIterator& a_it, SetIterator& b_it);
  friend SetIterator minus(SetIterator& a_it, SetIterator& b_it);

public:

  SetIterator(Set const& set);
  SetIterator(SetIterator const& it);
  SetIterator& operator=(SetIterator const& it);

  virtual ~SetIterator() {
    if ( mp_itRef != 0 ) {
      sarl_set_iterator_decr_ref(mp_itRef);
    }
  };

  SetIterator copy() {
    return SetIterator(*this);
  }

  void next() {
    sarl_set_iterator_next(mp_itRef);
  };

  void next_gte(Index v) {
    sarl_set_iterator_next_gte(mp_itRef, v);
  };

  Index value() {
    return sarl_set_iterator_value(mp_itRef);
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
  };

public: // nasty hack for SWIG
  SetIterator() {
    mp_itRef = 0;
  };

};

#include <sarl/cpp/Set.h>

inline SetIterator::SetIterator(Set const& set) {
  if ( set.mp_setRef != 0 ) {
    mp_itRef = sarl_set_iterator_create(set.mp_setRef);
  }
  else {
    mp_itRef = 0;
  };
};

inline SetIterator::SetIterator(SetIterator const& it) {
  if ( it.mp_itRef != 0 ) {
    mp_itRef = sarl_set_iterator_copy(it.mp_itRef);
  }
  else {
    mp_itRef = 0;
  };
};

inline SetIterator& SetIterator::operator=(SetIterator const& it) {
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
};

inline SetIterator meet(SetIterator& a_it, SetIterator& b_it) 
{
  if ( a_it.mp_itRef != 0 && b_it.mp_itRef != 0 ) {
    Sarl_SetIterator *p_it = 
      sarl_set_iterator_meet(a_it.mp_itRef, b_it.mp_itRef);
    return SetIterator(p_it);
  }
  else {
    return SetIterator();
  }
};

inline SetIterator join(SetIterator& a_it, SetIterator& b_it) 
{
  if ( a_it.mp_itRef != 0 && b_it.mp_itRef != 0 ) {
    Sarl_SetIterator *p_it = 
      sarl_set_iterator_union(a_it.mp_itRef, b_it.mp_itRef);
    return SetIterator(p_it);
  }
  else {
    return SetIterator();
  };
};

inline SetIterator minus(SetIterator& a_it, SetIterator& b_it) {
  if ( a_it.mp_itRef != 0 && b_it.mp_itRef != 0 ) {
    Sarl_SetIterator *p_it = 
      sarl_set_iterator_minus(a_it.mp_itRef, b_it.mp_itRef);
    return SetIterator(p_it);
  }
  else {
    return SetIterator();
  }
};

class SetIteratorFunctions {
public:
  inline static 
  SetIterator meet(SetIterator& a_it, SetIterator& b_it) 
  {
    ::minus(a_it, b_it);
  }

  inline static 
  SetIterator join(SetIterator& a_it, SetIterator& b_it) 
  {
    ::minus(a_it, b_it);
  }

  inline static 
  SetIterator minus(SetIterator& a_it, SetIterator& b_it) 
  {
    ::minus(a_it, b_it);
  }
};


#endif
