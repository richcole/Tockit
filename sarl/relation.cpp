extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

#include "relation_impl.h"

struct Sarl_Relation *sarl_relation_create()
{
  Sarl_Relation *r = new Sarl_Relation();
  sarl_ref_count_init(&r->m_ref_count);
  return r;
}

struct Sarl_Relation *sarl_relation_copy(struct Sarl_RelationIterator *it)
{
  struct Sarl_Relation *s = new Sarl_Relation();
  sarl_ref_count_init(&s->m_ref_count);
  SARL_RELATION_ITERATOR_FOR(it) {
    sarl_relation_insert_pair(s, sarl_relation_iterator_val(it));
  }
  return s;
}

void sarl_relation_insert(struct Sarl_Relation *r, Sarl_Index a, Sarl_Index b)
{
  Sarl_Pair p = { a, b };
  Sarl_Pair q = { b, a };
       
  r->forward.insert(p);
  r->reverse.insert(q);
}
  
void sarl_relation_remove(struct Sarl_Relation *r, Sarl_Index a, Sarl_Index b)
{
  Sarl_Pair p = { a, b };
  Sarl_Pair q = { b, a };
       
  r->forward.erase(p);
  r->reverse.erase(q);
}

void sarl_relation_decr_ref(struct Sarl_Relation *r)
{
  if ( sarl_ref_count_decr(&r->m_ref_count) ) {
    delete r;
  };
}

void sarl_relation_incr_ref(struct Sarl_Relation *r)
{
  sarl_ref_count_incr(&r->m_ref_count);
}

void sarl_relation_insert_pair(struct Sarl_Relation *r, struct Sarl_Pair pair)
{
  sarl_relation_insert(r, pair.dom, pair.rng);
}

void sarl_relation_remove_pair(struct Sarl_Relation *r, Sarl_Pair pair)
{
  sarl_relation_remove(r, pair.dom, pair.rng);
}

