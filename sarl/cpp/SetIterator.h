#ifndef SARL_CPP_SET_ITERATOR_H
#define SARL_CPP_SET_ITERATOR_H

extern "C" {
#include <sarl/set_iterator.h>
}

#include <sarl/cpp/Index.h>

class Set;
class Relation;
class RelationIterator;
class ConceptIterator;
class ContextIterator;
class LatticeIterator;

class SetIterator {

  class Temporary;

  friend class Set;
  friend class Relation;
  friend class RelationIterator;
  friend class ContextIterator;
  friend class ConceptIterator;
  friend class LatticeIterator;

  friend SetIterator::Temporary meet(SetIterator& a_it, SetIterator& b_it);
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

  class Temporary {
  public:
    Sarl_SetIterator* mp_itRef;
    Temporary(Sarl_SetIterator *itRef) {
      sarl_set_iterator_incr_ref(itRef);
      mp_itRef = itRef;
      sarl_set_iterator_release_ownership(itRef);
    }
    Temporary(Temporary const& t) {
      sarl_set_iterator_incr_ref(t.mp_itRef);
      mp_itRef = t.mp_itRef;
    }
    ~Temporary() {
      sarl_set_iterator_decr_ref(mp_itRef);
    };
  };

public:
  SetIterator(Temporary const& t) {
    mp_itRef = sarl_set_iterator_obtain_ownership(t.mp_itRef);
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
    mp_itRef = sarl_set_iterator_obtain_ownership(it.mp_itRef);
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
  mp_itRef = sarl_set_iterator_obtain_ownership(it.mp_itRef);
  return *this;
};

inline SetIterator::Temporary meet(SetIterator& a_it, SetIterator& b_it) 
{
  Sarl_SetIterator *p_it;
  
  if ( a_it.mp_itRef != 0 && b_it.mp_itRef != 0 ) {
    p_it = sarl_set_iterator_meet(a_it.mp_itRef, b_it.mp_itRef);
  }
  else {
    Sarl_Set* empty = sarl_set_create();
    p_it = sarl_set_iterator_create(empty);
    sarl_set_decr_ref(empty);
  }
  return SetIterator::Temporary(p_it);
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
    return ::meet(a_it, b_it);
  }

  inline static 
  SetIterator join(SetIterator& a_it, SetIterator& b_it) 
  {
    return ::join(a_it, b_it);
  }

  inline static 
  SetIterator minus(SetIterator& a_it, SetIterator& b_it) 
  {
    return ::minus(a_it, b_it);
  }
};


#endif
