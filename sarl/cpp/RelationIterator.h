#ifndef SARL_CPP_RELATION_ITERATOR_H
#define SARL_CPP_RELATION_ITERATOR_H

extern "C" {
#include <sarl/relation_iterator.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/SetIterator.h>
#include <sarl/cpp/Relation.h>

class ContextIterator;
class SetIterator;

class RelationIterator {

  friend class ContextIterator;
  friend class SetIterator;

public:
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
    mp_itRef = it.mp_itRef;
    if ( mp_itRef != 0 ) {
      sarl_relation_iterator_incr_ref(mp_itRef);
    }
    return *this;
  }

  virtual ~RelationIterator() {
    if ( mp_itRef != 0 ) {
      sarl_relation_iterator_decr_ref(mp_itRef);
    }
  };

  RelationIterator copy() {
    return RelationIterator(sarl_relation_iterator_copy(mp_itRef));
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
    return sarl_relation_iterator_reset(mp_itRef);
  };

  SetIterator domain() {
    return SetIterator(sarl_relation_iterator_domain(mp_itRef));
  }

  SetIterator range() {
    return SetIterator(sarl_relation_iterator_range(mp_itRef));
  }

	SetIterator extent(Sarl_Index m) {
		return SetIterator(
			sarl_relation_iterator_extent(mp_itRef, m)
		);
	}

	SetIterator intent(Sarl_Index g) {
		return SetIterator(
			sarl_relation_iterator_intent(mp_itRef, g)
		);
	}

  RelationIterator join(RelationIterator& it) {
    return RelationIterator(
      sarl_relation_iterator_join(
	mp_itRef, it.mp_itRef
      )
    );
  }

 private:
  Sarl_RelationIterator* mp_itRef;

  RelationIterator(Sarl_RelationIterator* ap_itRef) {
    mp_itRef = ap_itRef;
  }
};

#endif
