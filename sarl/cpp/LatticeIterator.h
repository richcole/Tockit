#ifndef SARL_CPP_LATTICE_ITERATOR_H
#define SARL_CPP_LATTICE_ITERATOR_H

extern "C" {
#include <sarl/lattice_iterator.h>
}

#include <sarl/cpp/Index.h>

class RelationIterator;
class SetIterator;

class LatticeIterator {

public:
  LatticeIterator(Context const& context);
  LatticeIterator(RelationIterator const& r);

  LatticeIterator& operator=(LatticeIterator const& it);

  virtual ~LatticeIterator() {
    if ( mp_itRef != 0 ) {
      sarl_lattice_iterator_decr_ref(mp_itRef);
    }
  };

  LatticeIterator copy() {
    return LatticeIterator(*this);
  }

  void next() {
    sarl_lattice_iterator_next(mp_itRef);
  };

  void next_gte(Index v) {
    sarl_lattice_iterator_next_gte(mp_itRef, v);
  };

  ConceptIterator value() {
    return ConceptIterator(sarl_lattice_iterator_value(mp_itRef));
  };

  bool at_end() {
    return sarl_lattice_iterator_at_end(mp_itRef);
  };

  void reset() {
    return sarl_lattice_iterator_reset(mp_itRef);
  };

  SetIterator intent() {
    return SetIterator(sarl_lattice_iterator_intent(mp_itRef));
  };

  SetIterator extent() {
    return SetIterator(sarl_lattice_iterator_extent(mp_itRef));
  };

private:
  Sarl_LatticeIterator* mp_itRef;

  LatticeIterator(Sarl_LatticeIterator* ap_itRef) {
    mp_itRef = ap_itRef;
  };

public: // nasty hack for SWIG
  LatticeIterator() {
    mp_itRef = 0;
  };
};


#endif
