extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

#include "relation_impl.h"

struct Relation *sarl_relation_create()
{
  Relation *r = new Relation();
  sarl_ref_count_init(&r->m_ref_count);
  return r;
}

struct Relation *sarl_relation_copy(struct RelationIterator *it)
{
  struct Relation *s = new Relation();
  sarl_ref_count_init(&s->m_ref_count);
  SARL_RELATION_ITERATOR_FOR(it) {
    sarl_relation_insert_pair(s, sarl_relation_iterator_val(it));
  }
  return s;
}

void sarl_relation_insert(struct Relation *r, Index a, Index b)
{
  Pair p = { a, b };
  Pair q = { b, a };
       
  r->forward.insert(p);
  r->reverse.insert(q);
}
  
void sarl_relation_remove(struct Relation *r, Index a, Index b)
{
  Pair p = { a, b };
  Pair q = { b, a };
       
  r->forward.erase(p);
  r->reverse.erase(q);
}

void sarl_relation_decr_ref(struct Relation *r)
{
  if ( sarl_ref_count_decr(&r->m_ref_count) ) {
    delete r;
  };
}

void sarl_relation_incr_ref(struct Relation *r)
{
  sarl_ref_count_incr(&r->m_ref_count);
}

void sarl_relation_insert_pair(struct Relation *r, struct Pair pair)
{
  sarl_relation_insert(r, pair.dom, pair.rng);
}

void sarl_relation_remove_pair(struct Relation *r, Pair pair)
{
  sarl_relation_remove(r, pair.dom, pair.rng);
}

