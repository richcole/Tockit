extern "C" {

#include <sarl/relation.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/relation_impl.h>
#include <sarl/relation_iterator_impl.h>
#include <sarl/plain_relation_iterator.h>
#include <iostream>

/* function prototypes used in function table declaired below */

static void sarl_relation_iterator_inverse_reset(
  struct Sarl_RelationIterator *it);

static struct Sarl_RelationIterator* sarl_relation_iterator_inverse_copy(
  struct Sarl_RelationIterator *it);

static struct Sarl_RelationIterator* sarl_relation_iterator_inverse_inverse(
  struct Sarl_RelationIterator *it);

static int sarl_relation_iterator_inverse_at_end(
  struct Sarl_RelationIterator *a_it);


/* function prototypes used in function table declaired below */

struct Sarl_RelationIteratorFunctionTable s_inverse_relation_iterator_table = 
{
  sarl_relation_iterator_plain_next_gte,
  sarl_relation_iterator_plain_next,
  sarl_relation_iterator_plain_value,
  sarl_relation_iterator_inverse_at_end,
  sarl_relation_iterator_inverse_reset,
  sarl_relation_iterator_plain_decr_ref,
  sarl_relation_iterator_plain_copy,
  sarl_relation_iterator_inverse_inverse
};

struct Sarl_RelationIterator* sarl_relation_iterator_plain_inverse(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator *plain_it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  Sarl_PlainRelationIterator* it  = new Sarl_PlainRelationIterator();
  sarl_relation_iterator_init(it, &s_inverse_relation_iterator_table);

  it->relation = plain_it->relation;
  sarl_relation_incr_ref(it->relation);
  
  it->it = plain_it->relation->reverse.begin();
  return it;
};

struct Sarl_RelationIterator *sarl_relation_iterator_inverse_inverse(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator* org_it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);

  return sarl_relation_iterator_create(org_it->relation);
};

static void  sarl_relation_iterator_inverse_reset(
  struct Sarl_RelationIterator *a_it) 
{
  Sarl_PlainRelationIterator *it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  it->it = it->relation->reverse.begin();
};

static int sarl_relation_iterator_inverse_at_end(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_PlainRelationIterator *it = 
    static_cast<Sarl_PlainRelationIterator*>(a_it);
  return it->it == it->relation->reverse.end();
};

