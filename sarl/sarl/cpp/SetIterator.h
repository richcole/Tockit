#ifndef SARL_CPP_SET_ITERATOR_H
#define SARL_CPP_SET_ITERATOR_H

extern "C" {
#include <sarl/set_iterator.h>
#include <sarl/assert.h>
}

#include <sarl/cpp/Config.h>
#include <sarl/cpp/Index.h>

class Set;
class Relation;
class RelationIterator;
class ConceptIterator;
class ContextIterator;
class LatticeIterator;
class Dictionary;

#define SARL_FOR(i) for((i).reset(); !(i).at_end(); (i).next())

class SetIterator {

public:
  friend class Set;
  friend class Relation;
  friend class RelationIterator;
  friend class ContextIterator;
  friend class ConceptIterator;
  friend class LatticeIterator;
  friend class Dictionary;
  friend class Lattice;

  friend SetIterator meet(SetIterator& a_it, SetIterator& b_it);
  friend SetIterator join(SetIterator& a_it, SetIterator& b_it);
  friend SetIterator minus(SetIterator& a_it, SetIterator& b_it);
  friend LatticeIterator object_factor(LatticeIterator& L, SetIterator& G_s);
  friend LatticeIterator object_factor(Lattice& L, SetIterator& G_s);

public:

  SetIterator(Set const& set);
  SetIterator(SetIterator const& it);
  SetIterator& operator=(SetIterator const& it);

  virtual ~SetIterator() {
    SARL_ASSERT(mp_itRef != 0);
    if ( mp_itRef != 0 ) {
      sarl_set_iterator_decr_ref(mp_itRef);
    }
  };

  SetIterator copy() {
    SARL_ASSERT(mp_itRef != 0);
    return SetIterator(*this);
  }

  void next() {
    SARL_ASSERT(mp_itRef != 0);
    sarl_set_iterator_next(mp_itRef);
  };

  void next_gte(Index v) {
    SARL_ASSERT(mp_itRef != 0);
    sarl_set_iterator_next_gte(mp_itRef, v);
  };

  Index value() {
    SARL_ASSERT(mp_itRef != 0);
    return sarl_set_iterator_value(mp_itRef);
  };

  bool at_end() {
    SARL_ASSERT(mp_itRef != 0);
    return sarl_set_iterator_at_end(mp_itRef);
  };

  void reset() {
    SARL_ASSERT(mp_itRef != 0);
    return sarl_set_iterator_reset(mp_itRef);
  };

  Index count_remaining() {
    SARL_ASSERT(mp_itRef != 0);
    return sarl_set_iterator_count_remaining(mp_itRef);
  }

  Index count() {
    SARL_ASSERT(mp_itRef != 0);
    return sarl_set_iterator_count(mp_itRef);
  }

  bool subseteq(SetIterator& a_it)
  {
    SARL_ASSERT(mp_itRef != 0);
    return
      sarl_set_iterator_subseteq(mp_itRef, a_it.mp_itRef);
  };

  bool eq(SetIterator& a_it)
  {
    SARL_ASSERT(mp_itRef != 0);
    return
      sarl_set_iterator_eq(mp_itRef, a_it.mp_itRef);
  };

  int lexical_compare(SetIterator& a_it)
  {
    SARL_ASSERT(mp_itRef != 0);
    return
      sarl_set_iterator_lexical_compare(mp_itRef, a_it.mp_itRef);
  };

protected:
  SetIterator retn() 
  {
    sarl_set_iterator_release_ownership(mp_itRef);
    return *this;
  };

private:
  Sarl_SetIterator* mp_itRef;

public:
  SetIterator(
    Sarl_SetIterator* ap_itRef, 
    bool incr_ref = SARL_DONT_INCR_REF) 
  {
    if ( incr_ref != SARL_DONT_INCR_REF ) {
      sarl_set_iterator_incr_ref(ap_itRef);
    }
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

inline SetIterator::SetIterator(SetIterator const& it) 
{
  SARL_ASSERT(it.mp_itRef != 0);
  mp_itRef = sarl_set_iterator_obtain_ownership(it.mp_itRef);
};

inline SetIterator& SetIterator::operator=(SetIterator const& it) 
{
  if ( it.mp_itRef == mp_itRef ) {
    return *this;
  }
  // this is the only function that tollerates mp_itRef being zero
  if ( mp_itRef != 0 ) {
    sarl_set_iterator_decr_ref(mp_itRef);
  }
  mp_itRef = sarl_set_iterator_obtain_ownership(it.mp_itRef);
  return *this;
};

inline SetIterator meet(SetIterator& a_it, SetIterator& b_it) 
{
  SARL_ASSERT(a_it.mp_itRef != 0 && b_it.mp_itRef != 0);

  return 
    SetIterator(
      sarl_set_iterator_meet(a_it.mp_itRef, b_it.mp_itRef)
    ).retn();
};

inline SetIterator join(SetIterator& a_it, SetIterator& b_it) 
{
  SARL_ASSERT(a_it.mp_itRef != 0 && b_it.mp_itRef != 0);

  return 
    SetIterator(
      sarl_set_iterator_union(a_it.mp_itRef, b_it.mp_itRef)
    ).retn();
};

inline SetIterator minus(SetIterator& a_it, SetIterator& b_it) 
{
  SARL_ASSERT(a_it.mp_itRef != 0 && b_it.mp_itRef != 0);

  return 
    SetIterator(
      sarl_set_iterator_minus(a_it.mp_itRef, b_it.mp_itRef)
    ).retn();
};

class SetIteratorFunctions {
public:
  inline static 
  SetIterator meet(SetIterator& a_it, SetIterator& b_it) 
  {
    return ::meet(a_it, b_it).retn();
  }

  inline static 
  SetIterator join(SetIterator& a_it, SetIterator& b_it) 
  {
    return ::join(a_it, b_it).retn();
  }

  inline static 
  SetIterator minus(SetIterator& a_it, SetIterator& b_it) 
  {
    return ::minus(a_it, b_it).retn();
  }
};


#endif
