 extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>
#include <sarl/test.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_nary_relation_index_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_nary_relation_index_prev_leq(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_nary_relation_index_next(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_nary_relation_index_prev(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_nary_relation_index_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_nary_relation_index_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_nary_relation_index_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_nary_relation_index_reset_last(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_nary_relation_index_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_nary_relation_index_copy(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_naryRelationIndexIteratorTable = 
{
  sarl_set_iterator_nary_relation_index_next_gte,
  sarl_set_iterator_nary_relation_index_prev_leq,
  sarl_set_iterator_nary_relation_index_next,
  sarl_set_iterator_nary_relation_index_prev,
  sarl_set_iterator_nary_relation_index_value,
  sarl_set_iterator_nary_relation_index_at_end,
  sarl_set_iterator_nary_relation_index_reset,
  sarl_set_iterator_nary_relation_index_reset_last,
  sarl_set_iterator_nary_relation_index_decr_ref,
  sarl_set_iterator_nary_relation_index_copy
};

/* constructive operations */
struct Sarl_SetIterator *sarl_nary_relation_index_set_iterator_create(
  struct Sarl_NaryRelation *r)
{
  Sarl_NaryRelationIndexSetIterator* it = 
    new Sarl_NaryRelationIndexSetIterator();
  sarl_set_iterator_init(it, &s_naryRelationIndexIteratorTable);

  it->r = r;
  it->i = 1;

  sarl_nary_relation_incr_ref(it->r);

  return it;
};

/* set_iterator moving operations */
static void  sarl_set_iterator_nary_relation_index_next_gte(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  Sarl_NaryRelationIndexSetIterator *it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);
	
  if ( value > it->i ) {
    it->i = value;
  }
}

static void  sarl_set_iterator_nary_relation_index_prev_leq(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  if ( value < it->i ) {
    it->i = value;
  };
};

static void  sarl_set_iterator_nary_relation_index_next(
  struct Sarl_SetIterator *a_it)
{
  Sarl_NaryRelationIndexSetIterator *it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);

  if ( it->i <= sarl_nary_relation_count(it->r) ) {
    ++it->i;
  }
}

static void  sarl_set_iterator_nary_relation_index_prev(
  struct Sarl_SetIterator *a_it)
{
  if ( it->i > 0 ) {
    --it->i;
  }
};

static Sarl_Index sarl_set_iterator_nary_relation_index_value(
  struct Sarl_SetIterator *a_it)
{
  Sarl_NaryRelationIndexSetIterator *it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);

  if ( ! sarl_set_iterator_at_end(it) ) {
    return *it->i;
  }
  else {
    return 0;
  }
}

static int sarl_set_iterator_nary_relation_index_at_end(
  struct Sarl_SetIterator *a_it)
{
  Sarl_NaryRelationIndexSetIterator *it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);
  return it->i > 0 && it->i <= sarl_nary_relation_count(it->r);
};

static void  sarl_set_iterator_nary_relation_index_reset(
  struct Sarl_SetIterator *a_it) 
{
  Sarl_NaryRelationIndexSetIterator *it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);
  it->i = 1;
};

static void  sarl_set_iterator_nary_relation_index_reset_last(
  struct Sarl_SetIterator *a_it) 
{
  Sarl_NaryRelationIndexSetIterator *it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);
  it->i = sarl_nary_relation_count(it->r);
};

/* reference counting interface */
void sarl_set_iterator_nary_relation_index_decr_ref(
  struct Sarl_SetIterator *a_it)
{
  Sarl_NaryRelationIndexSetIterator *it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_nary_relation_decr_ref(it->r);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_nary_relation_index_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_NaryRelationIndexSetIterator *org_it = 
    static_cast<Sarl_NaryRelationIndexSetIterator*>(a_it);
  
  Sarl_NaryRelationIndexSetIterator* copy_it  = 
    new Sarl_NaryRelationIndexSetIterator();
  sarl_set_iterator_init(copy_it, &s_plainIteratorTable);

  copy_it->r = org_it->r;
  sarl_nary_relation_incr_ref(copy_it->r);
  copy_it->i = org_it->i;
  return copy_it;
}




