extern "C" {
#include <sarl/set.h>  
#include <sarl/set_iterator.h>  
#include <sarl/map.h>  
#include <sarl/map_iterator.h>  
#include <sarl/nary_relation.h>
#include <sarl/nary_relation_iterator.h>
#include <sarl/vector.h>
#include <sarl/ref_count.h>
}

#include "nary_relation_impl.h"
#include <algorithm>

struct Sarl_NaryRelation *
  sarl_nary_relation_create(Sarl_Index arity)
{
  Sarl_NaryRelation *r = new Sarl_NaryRelation();
  sarl_ref_count_init(&r->ref_count);

  Sarl_Index i;
  r->maps.resize(arity, 0);

  for(i=0;i<arity;++i) {
    r->maps[i] = sarl_map_create();
  };
  r->count = 0;

  return r;
}

struct Sarl_NaryRelation *
  sarl_nary_relation_copy(
    struct Sarl_NaryRelation *r
  )
{
  struct Sarl_NaryRelation *s = new Sarl_NaryRelation();
  sarl_ref_count_init(&s->ref_count);

  Sarl_Index i;
  s->maps.resize(r->arity, 0);

  for(i=0;i<r->arity;++i) {
    s->maps[i] = sarl_map_create();
  };
  s->count = r->count;

  return s;
}

void 
  sarl_nary_relation_insert(
    struct Sarl_NaryRelation *r, Sarl_Vector *v
  )
{
  // test for arity match
  if ( sarl_vector_arity(v) != r->arity ) {
    SARL_REPORT_ERROR("arity mismatch between vector and relation.");
    return;
  };
  
  // test for uniqueness
  if ( sarl_nary_relation_is_member(r, v) ) {
    return;
  }
  else {
    Sarl_Index i;
    ++r->count;
    for(i=0;i<r->arity;++i) {
      sarl_map_insert(r->maps[i], r->count, sarl_vector_get(v, i));
    };
  };
}

Sarl_SetIterator *
  sarl_nary_relation_find_index(
    struct Sarl_NaryRelation *r, Sarl_Vector *v
  )
{
  // test for arity match
  if ( sarl_vector_arity(v) != r->arity ) {
    SARL_REPORT_ERROR("arity mismatch between vector and relation.");
    return 0;
  };

  Sarl_Index i;
  Sarl_SetIterator *agg = 0;
  
  for(i=0; i<r->arity; ++i) {
    Sarl_SetIterator *it = sarl_map_coimage(r->maps[i], sarl_vector_get(v, i));
    if ( it == 0 ) {
      agg = it;
    }
    else {
      Sarl_SetIterator *temp = agg;
      sarl_set_iterator_release_ownership(agg);
      sarl_set_iterator_release_ownership(it);
      temp = sarl_set_iterator_meet(agg, it);
      sarl_set_iterator_decr_ref(it);
      sarl_set_iterator_decr_ref(agg);
      temp = agg;
    }
  }

  return agg;
};

void 
  sarl_nary_relation_remove(
    struct Sarl_NaryRelation *r, Sarl_Vector *v
  )
{
  Sarl_SetIterator *agg = sarl_nary_relation_find_index(r, v);

  // test for error
  if ( agg == 0 ) {
    return;
  };

  // check for non membership
  sarl_set_iterator_reset(agg);
  if ( sarl_set_iterator_at_end(agg) ) {
    SARL_REPORT_ERROR("Removal of vector that was not a member.");
    return;
  }

  Sarl_Index val = sarl_set_iterator_value(agg);
  Sarl_Index i;
  for(i=0;i<r->arity;++i) {
    sarl_map_remove_extent(r->maps[i], val);
  }

  sarl_set_iterator_next(agg);
  if ( ! sarl_set_iterator_at_end(agg) ) {
    SARL_FATAL_ERROR("agg should contain a single entry, internal error.");
  };

  sarl_set_iterator_decr_ref(agg);
};

void sarl_nary_relation_decr_ref(struct Sarl_NaryRelation *r)
{
  Sarl_Index i;
  if ( sarl_ref_count_decr(&r->ref_count) ) {
    for(i=0;i<r->arity;++i) {
      sarl_map_decr_ref(r->maps[i]);
    }
    delete r;
  };
};

void sarl_nary_relation_incr_ref(struct Sarl_NaryRelation *r)
{
  sarl_ref_count_incr(&r->ref_count);
}

bool 
sarl_nary_relation_is_member(
  Sarl_NaryRelation *r, Sarl_Vector *v
)
{
  bool result;
  
  Sarl_SetIterator *agg = sarl_nary_relation_find_index(r, v);

  // check for error
  if ( agg == 0 ) {
    return false;
  };
  
  result = ! sarl_set_iterator_at_end(agg);
  sarl_set_iterator_decr_ref(agg);

  return result;
};

extern Sarl_Index sarl_nary_relation_count(
  struct Sarl_NaryRelation *r)
{
  return r->count;
};



