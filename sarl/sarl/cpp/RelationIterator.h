#ifndef SARL_CPP_RELATION_ITERATOR_H
#define SARL_CPP_RELATION_ITERATOR_H

extern "C" {
#include <sarl/relation_iterator.h>
#include <sarl/assert.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/SetIterator.h>
#include <sarl/cpp/Relation.h>

class ContextIterator;
class SetIterator;
class MapIterator;

class RelationIterator {
public:
  friend class ContextIterator;
  friend class SetIterator;
  friend class MapIterator;
  friend class Lattice;

  RelationIterator(Relation const& relation) {
    mp_itRef = sarl_relation_iterator_create(relation.mp_relationRef);
  };

  RelationIterator() {
    // very bad hack to allow SWIG to work
    mp_itRef = 0;
  }

  RelationIterator(RelationIterator const& it) {
    mp_itRef = 0;
    *this = it;
  }

  RelationIterator& operator=(RelationIterator const& it) {
    if ( it.mp_itRef == mp_itRef ) {
      return *this;
    }
    if ( mp_itRef != 0 ) {
      sarl_relation_iterator_decr_ref(mp_itRef);
    }
    SARL_ASSERT(it.mp_itRef != 0);
    mp_itRef = sarl_relation_iterator_obtain_ownership(it.mp_itRef);
    return *this;
  }

  virtual ~RelationIterator() {
    if ( mp_itRef != 0 ) {
      sarl_relation_iterator_decr_ref(mp_itRef);
    }
  };

  RelationIterator copy() {
    return RelationIterator(sarl_relation_iterator_copy(mp_itRef)).retn();
  }

  void next() {
    sarl_relation_iterator_next(mp_itRef);
  };

  void next_gte(Pair const& p) {
    sarl_relation_iterator_next_gte(mp_itRef, p);
  };

  Pair value() {
    return sarl_relation_iterator_value(mp_itRef);
  };

  bool at_end() {
    return sarl_relation_iterator_at_end(mp_itRef);
  };

  void reset() {
    sarl_relation_iterator_reset(mp_itRef);
  };

  SetIterator domain() {
    return SetIterator(sarl_relation_iterator_domain(mp_itRef)).retn();
  }

  SetIterator range() {
    return SetIterator(sarl_relation_iterator_range(mp_itRef)).retn();
  }

  SetIterator extent(Sarl_Index m) {
    return SetIterator(
      sarl_relation_iterator_extent(mp_itRef, m)
    ).retn();
  }
  
  SetIterator intent(Sarl_Index g) {
    return SetIterator(
      sarl_relation_iterator_intent(mp_itRef, g)
    ).retn();
  }

  RelationIterator join(RelationIterator& it) {
    return RelationIterator(
      sarl_relation_iterator_join(
	mp_itRef, it.mp_itRef
      )
    ).retn();
  }

  Index count_remaining() {
    return sarl_relation_iterator_count_remaining(mp_itRef);
  }

  Index count() {
    return sarl_relation_iterator_count(mp_itRef);
  }

  bool is_member(Index dom, Index rng) {
    return sarl_relation_iterator_is_member(mp_itRef, sarl_pair(dom,rng));
  };
  
  bool is_member(Pair const& p) {
    return sarl_relation_iterator_is_member(mp_itRef, p);
  };
  

private:
  RelationIterator retn() 
  {
    sarl_relation_iterator_release_ownership(mp_itRef);
    return *this;
  };

private:
  Sarl_RelationIterator* mp_itRef;

  RelationIterator(Sarl_RelationIterator* ap_itRef) {
    mp_itRef = ap_itRef;
  }
};

#endif
