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
    mp_relationRef = sarl_map_create();
  };

  Map(Map const& it) {
    mp_relationRef = 0;
    *this = it;
  }

  Map& operator=(Map const& it) {
    if ( it.mp_relationRef == mp_relationRef ) {
      return *this;
    }
    if ( mp_relationRef != 0 ) {
      sarl_map_decr_ref(mp_relationRef);
    }
    mp_relationRef = it.mp_relationRef;
    if ( mp_relationRef != 0 ) {
      sarl_map_incr_ref(mp_relationRef);
    }
    return *this;
  }

  virtual ~Map() {
    sarl_map_decr_ref(mp_relationRef);
  };

  inline void insert(Index dom, Index rng) {
    sarl_map_insert(mp_relationRef, dom, rng);
  };

  inline void remove(Index dom) {
    sarl_map_remove(mp_relationRef, dom);
  };

  inline void insert(Pair const& p) {
    sarl_map_insert_pair(mp_relationRef, p);
  };

  inline void remove(Pair const& p) {
    sarl_map_remove(mp_relationRef, p.dom);
  };

  inline Sarl_Index image(Index dom) {
    return sarl_map_image(mp_relationRef, dom);
  };

  inline Map copy() {
    Sarl_Map* p_ref;
    p_ref = sarl_map_copy(
      sarl_map_iterator_create(mp_relationRef)
    );
    return Map(p_ref);
  }

 private:
  Sarl_Map* mp_relationRef;

  Map(Sarl_Map* ap_ref) {
    mp_relationRef = ap_ref;
  }
};

#endif
