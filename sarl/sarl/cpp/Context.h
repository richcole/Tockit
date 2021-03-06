#ifndef SARL_CPP_CONTEXT_H
#define SARL_CPP_CONTEXT_H

extern "C" {
#include <sarl/context.h>
#include <sarl/context_iterator.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/SetIterator.h>
#include <sarl/cpp/RelationIterator.h>

class Set;
class Relation;
class RelationIterator;
class ConceptIterator;
class ContextIterator;
class LatticeIterator;

class Context {

  friend class ContextIterator;
  friend class ContextReader;

public: 

  inline Context();
  inline Context(ContextIterator const& it);
  inline Context(Context const& context);
  inline Context& operator=(Context const& it);
  
  virtual ~Context() {
    sarl_context_decr_ref(mp_contextRef);
  };

  inline void insert(Index g, Index m) {
    sarl_context_insert_pair(mp_contextRef, sarl_pair(g, m));
  };

  inline void remove(Index g, Index m) {
    sarl_context_remove_pair(mp_contextRef, sarl_pair(g, m));
  };

  inline void insert_object(Index g) {
    sarl_context_insert_object(mp_contextRef, g);
  };
  
  inline void insert_attribute(Index m) {
    sarl_context_insert_attribute(mp_contextRef, m);
  };

  inline SetIterator objects() {
    return SetIterator(
      sarl_context_objects(mp_contextRef)
    ).retn();
  };

  inline SetIterator attributes() {
    return SetIterator(
      sarl_context_attributes(mp_contextRef)
    ).retn();
  };

  inline RelationIterator incidence() {
    return RelationIterator(
      sarl_context_incidence(mp_contextRef)
    ).retn();
  };

  Context copy() {
    return Context(*this);
  };

 private:
  Sarl_Context* mp_contextRef;

  Context(Sarl_Context* ap_ref) {
    mp_contextRef = ap_ref;
  }
};

#include <sarl/cpp/ContextIterator.h>

inline Context::Context()
{
  mp_contextRef = sarl_context_create();
};

inline Context::Context(Context const& context) {
  ContextIterator it(context);
  mp_contextRef = sarl_context_copy(it.mp_itRef);
};

inline Context::Context(ContextIterator const& it) {
  mp_contextRef = sarl_context_copy(it.mp_itRef);
};

inline Context& Context::operator=(Context const& K) {
  if ( this == &K ) {
    return *this;
  }
  if ( mp_contextRef != 0 ) {
    sarl_context_decr_ref(mp_contextRef);
  }
  ContextIterator it(K);
  mp_contextRef = sarl_context_copy(it.mp_itRef);
  return *this;
};

#endif
