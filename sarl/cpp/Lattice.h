#ifndef SARL_CPP_LATTICE_H
#define SARL_CPP_LATTICE_H

extern "C" {
#include <sarl/lattice.h>
#include <sarl/lattice_iterator.h>
}

#include <sarl/cpp/Index.h>

class Set;
class Relation;
class RelationIterator;
class ContextIterator;
class Context;
class LatticeIterator;
class LatticeIterator;

class Lattice {

  friend class LatticeIterator;

public: 

  inline Lattice(LatticeIterator const& it);
  inline Lattice(Lattice const& lattice);
  inline Lattice& operator=(Lattice const& it);
  
  virtual ~Lattice() {
    sarl_lattice_decr_ref(mp_latticeRef);
  };

  Lattice copy() {
    return Lattice(*this);
  };

private:
  Sarl_Lattice* mp_latticeRef;

  Lattice(Sarl_Lattice* ap_ref) {
    mp_latticeRef = ap_ref;
  }
};

#include <sarl/cpp/LatticeIterator.h>

inline Lattice::Lattice(Lattice const& lattice) {
  SARL_ASSERT(lattice.mp_latticeRef);
  LatticeIterator it(lattice);
  mp_latticeRef = sarl_lattice_copy(it.mp_itRef);
};

inline Lattice::Lattice(LatticeIterator const& it) {
  mp_latticeRef = sarl_lattice_copy(it.mp_itRef);
};

inline Lattice& Lattice::operator=(Lattice const& L) {
  if ( this == &L ) {
    return *this;
  }
  if ( mp_latticeRef != 0 ) {
    sarl_lattice_decr_ref(mp_latticeRef);
  }
  LatticeIterator it(L);
  mp_latticeRef = sarl_lattice_copy(it.mp_itRef);
  return *this;
};

#endif
