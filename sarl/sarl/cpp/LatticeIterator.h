#ifndef SARL_CPP_LATTICE_ITERATOR_H
#define SARL_CPP_LATTICE_ITERATOR_H

extern "C" {
#include <sarl/lattice_iterator.h>
}

#include <sarl/cpp/Index.h>

class Lattice;
class SetIterator;
class RelationIterator;
class ConceptIterator;
class ContextIterator;

class LatticeIterator {

  friend class Lattice;
  friend LatticeIterator object_factor(LatticeIterator& L, SetIterator& G_s);
  friend LatticeIterator object_factor(Lattice& L, SetIterator& G_s);

public:
  LatticeIterator(ContextIterator  const& K);
  LatticeIterator(RelationIterator const& r);
  LatticeIterator(Lattice          const& L);
  LatticeIterator(LatticeIterator  const& L);

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

  inline void next_gte(ConceptIterator const& c);

  inline ConceptIterator value();

  inline bool at_end() {
    return sarl_lattice_iterator_at_end(mp_itRef);
  };

  inline void reset() {
    return sarl_lattice_iterator_reset(mp_itRef);
  };

  inline SetIterator intent();

  inline SetIterator extent();

protected:
  LatticeIterator retn() 
  {
    sarl_lattice_iterator_release_ownership(mp_itRef);
    return *this;
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

#include <sarl/cpp/ContextIterator.h>
#include <sarl/cpp/RelationIterator.h>
#include <sarl/cpp/ConceptIterator.h>
#include <sarl/cpp/SetIterator.h>
#include <sarl/cpp/Lattice.h>

inline LatticeIterator::LatticeIterator(LatticeIterator  const& L)
{
  mp_itRef = sarl_lattice_iterator_obtain_ownership(L.mp_itRef);
};

inline LatticeIterator::LatticeIterator(ContextIterator  const& K)
{
  mp_itRef = sarl_lattice_iterator_create_from_context(K.mp_itRef);
};

inline LatticeIterator::LatticeIterator(RelationIterator const& r)
{
  ContextIterator K(r);
  mp_itRef = sarl_lattice_iterator_create_from_context(K.mp_itRef);
};

inline LatticeIterator::LatticeIterator(Lattice          const& L)
{
  mp_itRef = sarl_lattice_iterator_create(L.mp_latticeRef);
};

inline void LatticeIterator::next_gte(ConceptIterator const& c) {
  sarl_lattice_iterator_next_gte(mp_itRef, c.mp_itRef);
};

ConceptIterator LatticeIterator::value() {
  return ConceptIterator(sarl_lattice_iterator_value(mp_itRef)).retn();
};

SetIterator LatticeIterator::intent() {
  return SetIterator(sarl_lattice_iterator_intent(mp_itRef)).retn();
};

SetIterator LatticeIterator::extent() {
  return SetIterator(sarl_lattice_iterator_extent(mp_itRef)).retn();
};


inline LatticeIterator object_factor(LatticeIterator& L, SetIterator& G_s)
{
  SARL_ASSERT(L.mp_itRef != 0 && G_s.mp_itRef != 0);
  return 
    LatticeIterator(
      sarl_lattice_iterator_create_object_factor(
        L.mp_itRef, 
        G_s.mp_itRef)
    ).retn();
};

#endif
