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

public:
  LatticeIterator(ContextIterator  const& K);
  LatticeIterator(RelationIterator const& r);
  LatticeIterator(Lattice          const& L);

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

  void prev() {
    sarl_lattice_iterator_prev(mp_itRef);
  };

  inline void next_gte(ConceptIterator const& c);
  inline void prev_leq(ConceptIterator const& c);

  inline ConceptIterator value();

  inline bool at_end() {
    return sarl_lattice_iterator_at_end(mp_itRef);
  };

  inline void reset() {
    return sarl_lattice_iterator_reset(mp_itRef);
  };

  inline void reset_last() {
    return sarl_lattice_iterator_reset_last(mp_itRef);
  };

  inline SetIterator intent();

  inline SetIterator extent();

  inline int count();

  inline LatticeIterator object_factor(SetIterator G_s);
  

  inline LatticeIterator attribute_factor(SetIterator M_s);

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

inline void LatticeIterator::prev_leq(ConceptIterator const& c) {
  sarl_lattice_iterator_prev_leq(mp_itRef, c.mp_itRef);
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

inline int LatticeIterator::count() {
  LatticeIterator tmp_it = *this;
  int count = 0;
  SARL_FOR(tmp_it) {
    ++count;
  }
  return count;
};

  inline LatticeIterator LatticeIterator::object_factor(SetIterator A) {
    return 
      LatticeIterator(
	sarl_lattice_iterator_create_object_factor(
	  mp_itRef, A.mp_itRef
	)
      );
  };

  inline LatticeIterator LatticeIterator::attribute_factor(SetIterator B) {
    return 
      LatticeIterator(
	sarl_lattice_iterator_create_attribute_factor(
	  mp_itRef, B.mp_itRef
	)
      );
  };


#endif
