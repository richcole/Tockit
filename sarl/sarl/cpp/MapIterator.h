#ifndef SARL_CPP_MAP_ITERATOR_H
#define SARL_CPP_MAP_ITERATOR_H

extern "C" {
#include <sarl/map_iterator.h>
#include <sarl/assert.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/SetIterator.h>
#include <sarl/cpp/Map.h>
#include <sarl/cpp/RelationIterator.h>

class ContextIterator;
class SetIterator;

class MapIterator {
public:
  friend class ContextIterator;
  friend class SetIterator;
  friend class Lattice;

  MapIterator(Map const& map) {
    mp_itRef = sarl_map_iterator_create(map.mp_mapRef);
  };

  MapIterator() {
    // very bad hack to allow SWIG to work
    mp_itRef = 0;
  }

  MapIterator(MapIterator const& it) {
    mp_itRef = 0;
    *this = it;
  }

  MapIterator& operator=(MapIterator const& it) {
    if ( it.mp_itRef == mp_itRef ) {
      return *this;
    }
    if ( mp_itRef != 0 ) {
      sarl_map_iterator_decr_ref(mp_itRef);
    }
    SARL_ASSERT(it.mp_itRef != 0);
    mp_itRef = sarl_map_iterator_obtain_ownership(it.mp_itRef);
    return *this;
  }

  virtual ~MapIterator() {
    if ( mp_itRef != 0 ) {
      sarl_map_iterator_decr_ref(mp_itRef);
    }
  };

  MapIterator copy() {
    return MapIterator(sarl_map_iterator_copy(mp_itRef)).retn();
  }

  void next() {
    sarl_map_iterator_next(mp_itRef);
  };

  void next_gte(Pair const& p) {
    sarl_map_iterator_next_gte(mp_itRef, p);
  };

  Pair value() {
    return sarl_map_iterator_value(mp_itRef);
  };

  bool at_end() {
    return sarl_map_iterator_at_end(mp_itRef);
  };

  void reset() {
    sarl_map_iterator_reset(mp_itRef);
  };

  RelationIterator relation_iterator() {
    RelationIterator it = sarl_map_iterator_create_relation_iterator(mp_itRef);
    return it.retn();
  }

  SetIterator domain() {
    RelationIterator it(relation_iterator());
    return it.domain();
  }
  
  SetIterator range() {
    RelationIterator it(relation_iterator());
    return it.range().retn();
  }

  SetIterator extent(Sarl_Index m) {
    RelationIterator it(relation_iterator());
    return it.extent(m).retn();
  }
  
  SetIterator intent(Sarl_Index g) {
    RelationIterator it(relation_iterator());
    return it.intent(g).retn();
  }

  //! @todo: join for maps should be composition right?
  //  MapIterator join(MapIterator& A) {
  //    RelationIterator this_it(relation_iterator());
  //    RelationIterator A_it(A.relation_iterator());
  //    return it.join(it).retn();
  //  }

  Index count_remaining() {
    RelationIterator it(relation_iterator());
    return it.count_remaining();
  }

  Index count() {
    RelationIterator it(relation_iterator());
    return it.count();
  }

  bool is_member(Index dom, Index rng) {
    RelationIterator it(relation_iterator());
    return it.is_member(dom, rng);
  };
  
  bool is_member(Pair const& p) {
    RelationIterator it(relation_iterator());
    return it.is_member(p);
  };

  Index image(Index dom) {
    return sarl_map_iterator_image(mp_itRef, dom);
  };
  
  SetIterator coimage(Index rng) {
    return extent(rng).retn();
  };

private:
  MapIterator retn() 
  {
    sarl_map_iterator_release_ownership(mp_itRef);
    return *this;
  };

private:
  Sarl_MapIterator* mp_itRef;

  MapIterator(Sarl_MapIterator* ap_itRef) {
    mp_itRef = ap_itRef;
  }
};

#endif
