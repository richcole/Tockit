#ifndef SARL_CPP_LATTICE_H
#define SARL_CPP_LATTICE_H

extern "C" {
#include <sarl/lattice.h>
#include <sarl/lattice_iterator.h>
}

#include <sarl/cpp/Index.h>
#include <sarl/cpp/SetIterator.h>
#include <sarl/cpp/RelationIterator.h>

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

  inline SetIterator concepts() const {
    SetIterator result(
      sarl_lattice_concepts(mp_latticeRef)
    );
    return result.retn();
  }

  inline RelationIterator covering() const {
    RelationIterator result(
      sarl_lattice_covering(mp_latticeRef)
    );
    return result.retn();
  }

  inline RelationIterator ordering() const {
    RelationIterator result(
      sarl_lattice_ordering(mp_latticeRef)
    );
    return result.retn();
  }

  inline RelationIterator object_contingent() const {
    RelationIterator result(
      sarl_lattice_object_contingent(mp_latticeRef)
    );
    return result.retn();
  }

  inline RelationIterator attribute_contingent() const {
    RelationIterator result(
      sarl_lattice_object_contingent(mp_latticeRef)
    );
    return result.retn();
  }

  inline RelationIterator intent() const {
    RelationIterator result(
      sarl_lattice_object_contingent(mp_latticeRef)
    );
    return result.retn();
  }

  inline RelationIterator extent() const {
    RelationIterator result(
      sarl_lattice_object_contingent(mp_latticeRef)
    );
    return result.retn();
  }

  inline SetIterator concept_intent(Index c) const
  {
    SetIterator result(intent().intent(c));
    return result.retn();
  };

  inline SetIterator concept_extent(Index c) const
  {
    SetIterator result(intent().intent(c));
    return result.retn();
  };

  inline SetIterator meet_irreducibles() const
  {
    SetIterator      C = concepts();
    RelationIterator R_cover = covering();
    Set              irr;
    
    SARL_FOR(C) {
      if ( R_cover.intent(C.value()).count() == 1 ) {
        irr.insert(C.value());
      }
    }
    
    SetIterator result(irr);
    return result.retn();
  };

  inline SetIterator join_irreducibles() const 
  {
    SetIterator      C = concepts();
    RelationIterator R_cover = covering();
    Set              irr;
    
    SARL_FOR(C) {
      if ( R_cover.extent(C.value()).count() == 1 ) {
        irr.insert(C.value());
      }
    }
    
    SetIterator result(irr);
    return result.retn();
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
