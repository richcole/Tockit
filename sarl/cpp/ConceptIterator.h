#ifndef SARL_CPP_CONCEPT_ITERATOR_H
#define SARL_CPP_CONCEPT_ITERATOR_H

extern "C" {
#include <sarl/concept_iterator.h>
}

#include <sarl/cpp/Index.h>

class RelationIterator;
class SetIterator;
class LatticeIterator;

class ConceptIterator {
  friend class LatticeIterator;

public:
  ConceptIterator(SetIterator const& A, SetIterator const& B); 
  ConceptIterator(RelationIterator const& r);

  ConceptIterator& operator=(ConceptIterator const& it);

  virtual ~ConceptIterator() {
    if ( mp_itRef != 0 ) {
      sarl_concept_iterator_decr_ref(mp_itRef);
    }
  };

private:
  ConceptIterator retn() 
  {
    sarl_concept_iterator_release_ownership(mp_itRef);
    return *this;
  };

private:
  Sarl_ConceptIterator* mp_itRef;

  ConceptIterator(Sarl_ConceptIterator* ap_itRef) {
    mp_itRef = ap_itRef;
  };

public: // nasty hack for SWIG
  ConceptIterator() {
    mp_itRef = 0;
  };
};

#endif
