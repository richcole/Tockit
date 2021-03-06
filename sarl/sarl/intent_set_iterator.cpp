extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/relation_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/domain_set_iterator_impl.h>
#include <sarl/set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_intent_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_intent_prev_leq(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_intent_next(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_intent_prev(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_intent_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_intent_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_intent_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_intent_reset_last(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_intent_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_intent_copy(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_intent_inverse(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_intent_iterator_table = 
{
  sarl_set_iterator_intent_next_gte,
  sarl_set_iterator_intent_prev_leq,
  sarl_set_iterator_intent_next,
  sarl_set_iterator_intent_prev,
  sarl_set_iterator_intent_value,
  sarl_set_iterator_intent_at_end,
  sarl_set_iterator_intent_reset,
  sarl_set_iterator_intent_reset_last,
  sarl_set_iterator_intent_decr_ref,
  sarl_set_iterator_intent_copy
};

struct Sarl_SetIterator* sarl_relation_iterator_intent(
  Sarl_RelationIterator* ap_it,
  Sarl_Index a_object)
{
  Sarl_IntentSetIterator *it = new Sarl_IntentSetIterator();

  sarl_set_iterator_init(it, &s_intent_iterator_table);

  it->iterator = sarl_relation_iterator_obtain_ownership(ap_it);
  it->object = a_object;

  sarl_set_iterator_reset(it);
  return it;
};

struct Sarl_SetIterator* sarl_relation_iterator_extent(
  Sarl_RelationIterator* ap_it,
  Sarl_Index a_object)
{
  Sarl_IntentSetIterator *it = new Sarl_IntentSetIterator();

  sarl_set_iterator_init(it, &s_intent_iterator_table);

  it->iterator = sarl_relation_iterator_inverse(ap_it);
  it->object = a_object;
  sarl_set_iterator_reset(it);

  return it;
};


/* set_iterator moving operations */
static void  sarl_set_iterator_intent_next_gte(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  Sarl_IntentSetIterator *it = static_cast<Sarl_IntentSetIterator*>(a_it);
  sarl_relation_iterator_next_gte(
    it->iterator, 
    sarl_pair(it->object, value)
  );
}

static void  sarl_set_iterator_intent_prev_leq(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  SARL_NOT_IMPLEMENTED;
};

static void  sarl_set_iterator_intent_next(struct Sarl_SetIterator *a_it)
{
  Sarl_IntentSetIterator *it = static_cast<Sarl_IntentSetIterator*>(a_it);
  sarl_relation_iterator_next(it->iterator);
}

static void  sarl_set_iterator_intent_prev(struct Sarl_SetIterator *a_it)
{
  SARL_NOT_IMPLEMENTED;
};

static Sarl_Index sarl_set_iterator_intent_value(struct Sarl_SetIterator *a_it)
{
  Sarl_IntentSetIterator *it = static_cast<Sarl_IntentSetIterator*>(a_it);
  Sarl_Pair p = sarl_relation_iterator_value(it->iterator);
  return (it->object == p.dom ? p.rng : 0);
}

static int   sarl_set_iterator_intent_at_end(struct Sarl_SetIterator *a_it)
{
  Sarl_IntentSetIterator *it = static_cast<Sarl_IntentSetIterator*>(a_it);
  return sarl_relation_iterator_at_end(it->iterator) || 
    it->object != sarl_relation_iterator_value(it->iterator).dom;
};

static void  sarl_set_iterator_intent_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_IntentSetIterator *it = static_cast<Sarl_IntentSetIterator*>(a_it);
  sarl_relation_iterator_reset(it->iterator);
  sarl_relation_iterator_next_gte(
    it->iterator, 
    sarl_pair(it->object, SARL_INDEX_MIN_VALUE)
  );
};

static void  sarl_set_iterator_intent_reset_last(struct Sarl_SetIterator *a_it) 
{
  SARL_NOT_IMPLEMENTED;
};

/* reference counting interface */
void sarl_set_iterator_intent_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_IntentSetIterator *it = static_cast<Sarl_IntentSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_relation_iterator_decr_ref(it->iterator);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_intent_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_IntentSetIterator *org_it = 
    static_cast<Sarl_IntentSetIterator*>(a_it);
  
  Sarl_IntentSetIterator* copy_it  = new Sarl_IntentSetIterator();
  sarl_set_iterator_init(copy_it, &s_intent_iterator_table);

  copy_it->iterator = sarl_relation_iterator_copy(org_it->iterator);
  copy_it->object = org_it->object;
  
  return copy_it;
}


