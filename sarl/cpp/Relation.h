#ifndef SARL_CPP_RELATION_H
#define SARL_CPP_RELATION_H

extern "C" {
#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
}

typedef unsigned int Index;
// #include <sarl/cpp/Index.h>
#include <sarl/cpp/Pair.h>

class RelationIterator;

class Relation {
  friend class RelationIterator;
 public: 
  Relation() {
    mp_relationRef = sarl_relation_create();
  };

  Relation(Relation const& it) {
    mp_relationRef = 0;
    *this = it;
  }

  Relation& operator=(Relation const& it) {
    if ( it.mp_relationRef == mp_relationRef ) {
      return *this;
    }
    if ( mp_relationRef != 0 ) {
      sarl_relation_decr_ref(mp_relationRef);
    }
    mp_relationRef = it.mp_relationRef;
    if ( mp_relationRef != 0 ) {
      sarl_relation_incr_ref(mp_relationRef);
    }
    return *this;
  }

  virtual ~Relation() {
    sarl_relation_decr_ref(mp_relationRef);
  };

  inline void insert(Index dom, Index rng) {
    sarl_relation_insert(mp_relationRef, dom, rng);
  };

  inline void remove(Index dom, Index rng) {
    sarl_relation_remove(mp_relationRef, dom, rng);
  };

  inline void insert(Pair const& p) {
    sarl_relation_insert(mp_relationRef, p.dom, p.rng);
  };

  inline void remove(Pair const& p) {
    sarl_relation_remove(mp_relationRef, p.dom, p.rng);
  };

  inline Relation copy() {
    Sarl_Relation* p_ref;
    p_ref = sarl_relation_copy(
      sarl_relation_iterator_create(mp_relationRef)
    );
    return Relation(p_ref);
  }

 private:
  Sarl_Relation* mp_relationRef;

  Relation(Sarl_Relation* ap_ref) {
    mp_relationRef = ap_ref;
  }
};

#endif
