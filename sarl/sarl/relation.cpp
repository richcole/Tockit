extern "C" {

#include <sarl/set.h>  
#include <sarl/set_iterator.h>  
#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
}

#include "relation_impl.h"
#include <algorithm>

struct Sarl_Relation *
  sarl_relation_create()
{
  Sarl_Relation *r = new Sarl_Relation();
  sarl_ref_count_init(&r->ref_count);
  return r;
}

struct Sarl_Relation *
  sarl_relation_copy(
    struct Sarl_RelationIterator *it
  )
{
  struct Sarl_Relation *s = new Sarl_Relation();
  sarl_ref_count_init(&s->ref_count);
  SARL_RELATION_ITERATOR_FOR(it) {
    sarl_relation_insert_pair(s, sarl_relation_iterator_value(it));
  }
  return s;
}

void 
  sarl_relation_insert(
    struct Sarl_Relation *r, Sarl_Index a, Sarl_Index b
  )
{
  Sarl_Pair p = { a, b };
  Sarl_Pair q = { b, a };
       
  r->forward.insert(p);
  r->reverse.insert(q);
}
  
void 
  sarl_relation_remove(
    struct Sarl_Relation *r, Sarl_Index a, Sarl_Index b
  )
{
  Sarl_Pair p = { a, b };
  Sarl_Pair q = { b, a };
       
  r->forward.erase(p);
  r->reverse.erase(q);
}

void 
  sarl_relation_remove_extent(
    struct Sarl_Relation *r, Sarl_Index index
  )
{
  Sarl_RelationIterator* r_it = sarl_relation_iterator_create(r);
  Sarl_SetIterator*      e_it = sarl_relation_iterator_extent(r_it, index);
  Sarl_Set*              tmp  = sarl_set_copy(e_it);
  Sarl_SetIterator*      tmp_it = sarl_set_iterator_create(tmp);
	
  SARL_SET_ITERATOR_FOR(tmp_it) {
    sarl_relation_remove_pair(
      r, 
      sarl_pair(sarl_set_iterator_value(tmp_it), index)
    );
  }

  sarl_relation_iterator_decr_ref(r_it);
  sarl_set_iterator_decr_ref(e_it);
  sarl_set_iterator_decr_ref(tmp_it);
  sarl_set_decr_ref(tmp);
};

void 
  sarl_relation_remove_intent(
    struct Sarl_Relation *r, Sarl_Index index
  )
{
  Sarl_RelationIterator* r_it = sarl_relation_iterator_create(r);
  Sarl_SetIterator*      i_it = sarl_relation_iterator_intent(r_it, index);
  Sarl_Set*              tmp  = sarl_set_copy(i_it);
  Sarl_SetIterator*      tmp_it = sarl_set_iterator_create(tmp);
	
  SARL_SET_ITERATOR_FOR(tmp_it) {
    sarl_relation_remove_pair(
      r, 
      sarl_pair(index, sarl_set_iterator_value(tmp_it))
    );
  }

  sarl_relation_iterator_decr_ref(r_it);
  sarl_set_iterator_decr_ref(i_it);
  sarl_set_iterator_decr_ref(tmp_it);
  sarl_set_decr_ref(tmp);
}

void sarl_relation_decr_ref(struct Sarl_Relation *r)
{
  if ( sarl_ref_count_decr(&r->ref_count) ) {
    delete r;
  }
};

void sarl_relation_incr_ref(struct Sarl_Relation *r)
{
  sarl_ref_count_incr(&r->ref_count);
}

void sarl_relation_insert_pair(struct Sarl_Relation *r, struct Sarl_Pair pair)
{
  sarl_relation_insert(r, pair.dom, pair.rng);
}

void sarl_relation_remove_pair(struct Sarl_Relation *r, Sarl_Pair pair)
{
  sarl_relation_remove(r, pair.dom, pair.rng);
}

bool 
sarl_relation_is_member(
  Sarl_Relation *r, Sarl_Pair p
)
{
  bool result;
  Sarl_SetIterator* intent;
  Sarl_RelationIterator *it;
  
  it = sarl_relation_iterator_create(r);
  sarl_relation_iterator_release_ownership(it);
  intent  = sarl_relation_iterator_intent(it, p.dom);
  sarl_set_iterator_release_ownership(intent);
  result = sarl_set_iterator_is_member(intent, p.rng);

  sarl_set_iterator_decr_ref(intent);
  sarl_relation_iterator_decr_ref(it);

  return result;
};

