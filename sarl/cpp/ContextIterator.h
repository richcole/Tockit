#ifndef SARL_CPP_CONTEXT_ITERATOR_H
#define SARL_CPP_CONTEXT_ITERATOR_H

extern "C" {
#include <sarl/context_iterator.h>
}

#include <sarl/cpp/Index.h>

class RelationIterator;
class SetIterator;
class Context;

class ContextIterator {

  friend class Context;

public:
  inline ContextIterator(Context const& context) ;
  inline ContextIterator::ContextIterator(ContextIterator const& it);
  inline ContextIterator(RelationIterator const& r) ;

  inline ContextIterator(
    SetIterator const& G,
    SetIterator const& M,
    RelationIterator const& I
  );

  inline ContextIterator& operator=(ContextIterator const& it);

  virtual ~ContextIterator() {
    if ( mp_itRef != 0 ) {
      sarl_context_iterator_decr_ref(mp_itRef);
    }
  };

  ContextIterator copy() {
    return ContextIterator(*this);
  }

  inline SetIterator objects();
  inline SetIterator attributes();

  inline SetIterator extent(SetIterator const& A);
  inline SetIterator intent(SetIterator const& B);

  inline SetIterator next_extent(SetIterator const& A);
  inline SetIterator next_extent_superseteq(
    SetIterator const& A, SetIterator const& P);

private:
  Sarl_ContextIterator* mp_itRef;

  ContextIterator(Sarl_ContextIterator* ap_itRef) {
    mp_itRef = ap_itRef;
  };

public: // nasty hack for SWIG
  ContextIterator() {
    mp_itRef = 0;
  };
};

#include <sarl/cpp/RelationIterator.h>
#include <sarl/cpp/SetIterator.h>
#include <sarl/cpp/Set.h>

inline ContextIterator::ContextIterator(Context const& context) {
  if ( context.mp_contextRef != 0 ) {
    mp_itRef = sarl_context_iterator_create(context.mp_contextRef);
  }
  else {
    mp_itRef = 0;
  };
};

inline ContextIterator::ContextIterator(RelationIterator const& r) {
  if ( r.mp_itRef != 0 ) {
    mp_itRef = sarl_context_iterator_create_from_relation(r.mp_itRef);
  }
  else {
    mp_itRef = 0;
  };
};

inline ContextIterator::ContextIterator(
  SetIterator const& G,
  SetIterator const& M,
  RelationIterator const& I) 
{
  if ( I.mp_itRef != 0 && G.mp_itRef != 0 && M.mp_itRef != 0) {
    mp_itRef = sarl_context_iterator_create_from_gmi(
      G.mp_itRef, M.mp_itRef, I.mp_itRef
    );
  }
  else {
    mp_itRef = 0;
  };
};

inline ContextIterator::ContextIterator(ContextIterator const& it) {
  if ( it.mp_itRef != 0 ) {
    mp_itRef = sarl_context_iterator_copy(it.mp_itRef);
  }
  else {
    mp_itRef = 0;
  };
};

inline ContextIterator& ContextIterator::operator=(ContextIterator const& it) {
  if ( it.mp_itRef == mp_itRef ) {
    return *this;
  }
  if ( mp_itRef != 0 ) {
    sarl_context_iterator_decr_ref(mp_itRef);
  }
  mp_itRef = it.mp_itRef;
  if ( mp_itRef != 0 ) {
    sarl_context_iterator_incr_ref(mp_itRef);
  }
  return *this;
};


inline SetIterator ContextIterator::objects() {
  return SetIterator(sarl_context_iterator_objects(mp_itRef));
};

inline SetIterator ContextIterator::attributes() {
  return SetIterator(sarl_context_iterator_attributes(mp_itRef));
};

inline SetIterator ContextIterator::extent(SetIterator const& B)
{
  return SetIterator(sarl_context_iterator_extent_set(mp_itRef, B.mp_itRef));
};

inline SetIterator ContextIterator::intent(SetIterator const& A)
{
  return SetIterator(sarl_context_iterator_intent_set(mp_itRef, A.mp_itRef));
};

inline SetIterator ContextIterator::next_extent(SetIterator const& A)
{
  Sarl_SetIterator *result = 
    sarl_context_iterator_next_extent(mp_itRef, A.mp_itRef);
  return result ? SetIterator(result) : SetIterator(Set());
};

inline SetIterator ContextIterator::next_extent_superseteq(
  SetIterator const& A, SetIterator const& P)
{
  return SetIterator(
    sarl_context_iterator_next_extent_superseteq(
      mp_itRef, A.mp_itRef, P.mp_itRef
    )
  );
};



#endif
