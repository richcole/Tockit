#ifndef SARL_CPP_CONTEXT_ITERATOR_H
#define SARL_CPP_CONTEXT_ITERATOR_H

extern "C" {
#include <sarl/context_iterator.h>
}

#include <sarl/cpp/Config.h>
#include <sarl/cpp/Index.h>

class RelationIterator;
class SetIterator;
class Context;

class ContextIterator {

  friend class Context;
  friend class LatticeIterator;

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

  inline SetIterator extent(Index m);
  inline SetIterator intent(Index g);

  inline SetIterator next_extent(SetIterator const& A);
  inline SetIterator next_extent_superseteq(
    SetIterator const& A, SetIterator const& P);

private:
  ContextIterator retn() 
  {
    sarl_context_iterator_release_ownership(mp_itRef);
    return *this;
  };

private:
  Sarl_ContextIterator* mp_itRef;

public:
  ContextIterator(
    Sarl_ContextIterator* ap_itRef, 
    bool incr_ref = SARL_DONT_INCR_REF) 
  {
    if ( incr_ref != SARL_DONT_INCR_REF ) {
      sarl_context_iterator_incr_ref(ap_itRef);
    }
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
#include <sarl/cpp/Context.h>

inline ContextIterator::ContextIterator(Context const& context) {
  SARL_ASSERT(context.mp_contextRef != 0);
  mp_itRef = sarl_context_iterator_create(context.mp_contextRef);
};

inline ContextIterator::ContextIterator(RelationIterator const& r) {
  SARL_ASSERT(r.mp_itRef != 0);
  mp_itRef = sarl_context_iterator_create_from_relation(r.mp_itRef);
};

inline ContextIterator::ContextIterator(
  SetIterator const& G,
  SetIterator const& M,
  RelationIterator const& I) 
{
  SARL_ASSERT(I.mp_itRef != 0 && G.mp_itRef != 0 && M.mp_itRef != 0);

  mp_itRef = sarl_context_iterator_create_from_gmi(
    G.mp_itRef, 
    M.mp_itRef, 
    I.mp_itRef
  );
};

inline ContextIterator::ContextIterator(ContextIterator const& it) 
{
  SARL_ASSERT(it.mp_itRef != 0);
  mp_itRef = sarl_context_iterator_obtain_ownership(it.mp_itRef);
};

inline ContextIterator& ContextIterator::operator=(ContextIterator const& it) {
  if ( this == &it ) {
    return *this;
  }
  if ( mp_itRef != 0 ) {
    sarl_context_iterator_decr_ref(mp_itRef);
  }
  mp_itRef = sarl_context_iterator_obtain_ownership(it.mp_itRef);
  if ( mp_itRef != 0 ) {
    sarl_context_iterator_incr_ref(mp_itRef);
  }
  return *this;
};


inline SetIterator ContextIterator::objects() {
  return SetIterator(sarl_context_iterator_objects(mp_itRef)).retn();
};

inline SetIterator ContextIterator::attributes() {
  return SetIterator(sarl_context_iterator_attributes(mp_itRef)).retn();
};

inline SetIterator ContextIterator::extent(SetIterator const& B)
{
  return SetIterator(
    sarl_context_iterator_extent_set(mp_itRef, B.mp_itRef)
  ).retn();
};

inline SetIterator ContextIterator::intent(SetIterator const& A)
{
  return SetIterator(
    sarl_context_iterator_intent_set(mp_itRef, A.mp_itRef)
  ).retn();
};

inline SetIterator ContextIterator::extent(Index m)
{
  return SetIterator(
    sarl_context_iterator_extent(mp_itRef, m)
  );
};

inline SetIterator ContextIterator::intent(Index g)
{
  return SetIterator(
    sarl_context_iterator_intent(mp_itRef, g)
  );
};

inline SetIterator ContextIterator::next_extent(SetIterator const& A)
{
  Sarl_SetIterator *result = 
    sarl_context_iterator_next_extent(mp_itRef, A.mp_itRef);
  if ( result == 0 ) {
    result = sarl_set_iterator_create_empty();
  }
  return SetIterator(result).retn();
};

inline SetIterator ContextIterator::next_extent_superseteq(
  SetIterator const& A, SetIterator const& P)
{
  Sarl_SetIterator *result = 
    sarl_context_iterator_next_extent_superseteq(
      mp_itRef, A.mp_itRef, P.mp_itRef
    );
  if ( result == 0 ) {
    result = sarl_set_iterator_create_empty();
  }
  return SetIterator(result);
};



#endif

