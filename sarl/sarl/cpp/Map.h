#ifndef SARL_CPP_MAP_H
#define SARL_CPP_MAP_H

extern "C" {
#include <sarl/map.h>
#include <sarl/map_iterator.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/Pair.h>

class MapIterator;

class Map {
  friend class MapIterator;
 public: 
  Map() {
    mp_mapRef = sarl_map_create();
  };

  Map(Map const& it) {
    mp_mapRef = 0;
    *this = it;
  }

  Map& operator=(Map const& it) {
    if ( it.mp_mapRef == mp_mapRef ) {
      return *this;
    }
    if ( mp_mapRef != 0 ) {
      sarl_map_decr_ref(mp_mapRef);
    }
    mp_mapRef = it.mp_mapRef;
    if ( mp_mapRef != 0 ) {
      sarl_map_incr_ref(mp_mapRef);
    }
    return *this;
  }

  virtual ~Map() {
    sarl_map_decr_ref(mp_mapRef);
  };

  inline void insert(Index dom, Index rng) {
    sarl_map_insert(mp_mapRef, dom, rng);
  };

  inline void remove(Index dom, Index rng) {
    sarl_map_remove(mp_mapRef, dom, rng);
  };

  inline void insert(Pair const& p) {
    sarl_map_insert_pair(mp_mapRef, p);
  };

  inline void remove(Pair const& p) {
    sarl_map_remove(mp_mapRef, p.dom, p.rng);
  };

  inline Sarl_Index image(Index dom) const {
    return sarl_map_image(mp_mapRef, dom);
  };

  inline Map copy() const {
    Sarl_Map* p_ref;
    p_ref = sarl_map_copy(
      sarl_map_iterator_create(mp_mapRef)
    );
    return Map(p_ref);
  }

 private:
  Sarl_Map* mp_mapRef;

  Map(Sarl_Map* ap_ref) {
    mp_mapRef = ap_ref;
  }
};

#endif
