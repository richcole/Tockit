extern "C" {

#include <sarl/config.h>
#include <sarl/set.h>  
#include <sarl/set_iterator.h>  
#include <sarl/relation.h>
#include <sarl/transitive_relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
}

#include "relation_impl.h"
#include "transitive_relation_impl.h"

#include <algorithm>

struct Sarl_TransitiveRelation *sarl_transitive_relation_create()
{
  Sarl_TransitiveRelation *r = new Sarl_TransitiveRelation();
  sarl_ref_count_init(&r->ref_count);
  r->ordering = sarl_relation_create();
  r->covering = sarl_relation_create();
  return r;
}

struct Sarl_TransitiveRelation *sarl_transitive_relation_copy(
  struct Sarl_RelationIterator *it)
{
  struct Sarl_TransitiveRelation *s = sarl_transitive_relation_create();
  SARL_RELATION_ITERATOR_FOR(it) {
    sarl_transitive_relation_insert_pair(
      s, sarl_relation_iterator_value(it)
    );
  }
  return s;
}

void sarl_transitive_relation_insert(struct Sarl_TransitiveRelation *r, 
  Sarl_Index a, Sarl_Index b)
{
  Sarl_Pair p = { a, b };
  Sarl_Pair q = { b, a };

  if ( a == b ) {
    // if a == b then the insert is ignored
    return;
  }

  Sarl_RelationIterator* ordering_it = 
    sarl_relation_iterator_create(r->ordering);

  // test for membership
  if ( sarl_relation_iterator_is_member(ordering_it, p) ) {
    sarl_relation_iterator_decr_ref(ordering_it);
    return;
  };
  
  // test for inverse membership
  if ( sarl_relation_iterator_is_member(ordering_it, q) ) {
    // if b < a then insert(a,b) is ignored
    sarl_relation_iterator_decr_ref(ordering_it);
    return; 
  };

  Sarl_SetIterator* upper_it =
    sarl_set_iterator_cache_and_decr_ref(
      sarl_transitive_relation_up_set(r, b)
    );

  Sarl_SetIterator* lower_it =
    sarl_set_iterator_cache_and_decr_ref(
      sarl_transitive_relation_down_set(r, a)
    );

  // update the ordering relation
  SARL_SET_ITERATOR_FOR(upper_it) {
    SARL_SET_ITERATOR_FOR(lower_it) {
      sarl_relation_insert(
        r->ordering, 
        sarl_set_iterator_value(lower_it),
        sarl_set_iterator_value(upper_it)
      );
    }
  }

  // update the covering relation
  sarl_relation_insert(r->covering, a, b);

  Sarl_SetIterator* upper_cover_it =
    sarl_set_iterator_cache_and_decr_ref(
      sarl_transitive_relation_upper_covers(r, a)
    );

  Sarl_SetIterator* lower_cover_it =
    sarl_set_iterator_cache_and_decr_ref(
      sarl_transitive_relation_lower_covers(r, b)
    );

  sarl_set_iterator_reset(upper_it);
  sarl_set_iterator_reset(lower_it);
  sarl_set_iterator_release_ownership(upper_it);
  sarl_set_iterator_release_ownership(lower_it);
  sarl_set_iterator_release_ownership(upper_cover_it);
  sarl_set_iterator_release_ownership(lower_cover_it);
  
  Sarl_SetIterator* upper_ferrels =
    sarl_set_iterator_minus(upper_cover_it, upper_it);
  
  Sarl_SetIterator* lower_ferrels =
    sarl_set_iterator_minus(lower_cover_it, lower_it);
  
  SARL_SET_ITERATOR_FOR(upper_ferrels) {
    sarl_relation_remove(
      r->covering, a, sarl_set_iterator_value(upper_ferrels)
    );
  };

  SARL_SET_ITERATOR_FOR(lower_ferrels) {
    sarl_relation_remove(
      r->covering, a, sarl_set_iterator_value(lower_ferrels)
    );
  };

  sarl_set_iterator_decr_ref(upper_ferrels);
  sarl_set_iterator_decr_ref(lower_ferrels);
  sarl_set_iterator_decr_ref(upper_it);
  sarl_set_iterator_decr_ref(lower_it);
  sarl_set_iterator_decr_ref(upper_cover_it);
  sarl_set_iterator_decr_ref(lower_cover_it);
  
}
  
void sarl_transitive_relation_remove(
  struct Sarl_Relation *r, Sarl_Index a, Sarl_Index b)
{
  SARL_NOT_IMPLEMENTED;
}

void sarl_transitive_relation_remove_extent(
  struct Sarl_Relation *r, Sarl_Index index)
{
  SARL_NOT_IMPLEMENTED;
};

void sarl_transitive_relation_remove_intent(
  struct Sarl_Relation *r, Sarl_Index index)
{
  SARL_NOT_IMPLEMENTED;
}

void sarl_transitive_relation_decr_ref(struct Sarl_TransitiveRelation *r)
{
  if ( sarl_ref_count_decr(&r->ref_count) ) {
    sarl_relation_decr_ref(r->ordering);
    sarl_relation_decr_ref(r->covering);
    delete r;
  }
};

void sarl_transitive_relation_incr_ref(struct Sarl_Relation *r)
{
  sarl_ref_count_incr(&r->ref_count);
}

void sarl_transitive_relation_insert_pair(
  struct Sarl_TransitiveRelation *r, struct Sarl_Pair pair)
{
  sarl_transitive_relation_insert(r, pair.dom, pair.rng);
}

void sarl_transitive_relation_remove_pair(
  struct Sarl_Relation *r, Sarl_Pair pair)
{
  sarl_transitive_relation_remove(r, pair.dom, pair.rng);
}

struct Sarl_SetIterator*
  sarl_transitive_relation_upper_covers(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index)
{
  Sarl_RelationIterator* it = sarl_relation_iterator_create(r->covering);
  return sarl_relation_iterator_intent(it, index);
};


struct Sarl_SetIterator*
  sarl_transitive_relation_lower_covers(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index)
{
  Sarl_RelationIterator* it = sarl_relation_iterator_create(r->covering);
  return sarl_relation_iterator_extent(it, index);
};


struct Sarl_SetIterator*
  sarl_transitive_relation_up_set(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index)
{
  Sarl_RelationIterator* it = sarl_relation_iterator_create(r->covering);
  return sarl_relation_iterator_intent(it, index);
};


struct Sarl_SetIterator*
  sarl_transitive_relation_down_set(
    struct Sarl_TransitiveRelation* r, 
    Sarl_Index index)
{
  Sarl_RelationIterator* it = sarl_relation_iterator_create(r->covering);
  return sarl_relation_iterator_extent(it, index);
};


struct Sarl_SetIterator*
  sarl_transitive_relation_upper_bounds(
    struct Sarl_TransitiveRelation* r, 
    Sarl_SetIterator* S)
{
  Sarl_RelationIterator* it = sarl_relation_iterator_create(r->covering);
  return sarl_relation_iterator_intent_set(it, S);
};


struct Sarl_SetIterator*
  sarl_transitive_relation_lower_bounds(
    struct Sarl_TransitiveRelation* r, 
    Sarl_SetIterator* S)
{
  Sarl_RelationIterator* it = sarl_relation_iterator_create(r->covering);
  return sarl_relation_iterator_extent_set(it, S);
};



