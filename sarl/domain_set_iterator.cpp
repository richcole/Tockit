extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/domain_set_iterator_impl.h>

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_domain_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_domain_next(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_domain_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_domain_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_domain_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_domain_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_domain_copy(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_domain_inverse(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_domain_iterator_table = 
{
  sarl_set_iterator_domain_next_gte,
  sarl_set_iterator_domain_next,
  sarl_set_iterator_domain_value,
  sarl_set_iterator_domain_at_end,
  sarl_set_iterator_domain_reset,
  sarl_set_iterator_domain_decr_ref,
  sarl_set_iterator_domain_copy
};

/* set_iterator moving operations */
static void  sarl_set_iterator_domain_next_gte(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  Sarl_DomainSetIterator *it = static_cast<Sarl_DomainSetIterator*>(a_it);
  sarl_relation_iterator_next_gte(it->rel_it, sarl_pair(value, 0));
}

static void  sarl_set_iterator_domain_next(struct Sarl_SetIterator *a_it)
{
  Sarl_DomainSetIterator *it = static_cast<Sarl_DomainSetIterator*>(a_it);
  sarl_relation_iterator_next_gte(it->rel_it, 
    sarl_pair(sarl_relation_iterator_value(it->rel_it).dom+1, 0));
}

static Sarl_Index sarl_set_iterator_domain_value(struct Sarl_SetIterator *a_it)
{
  Sarl_DomainSetIterator *it = static_cast<Sarl_DomainSetIterator*>(a_it);
  return sarl_relation_iterator_value(it->rel_it).dom;
}

static int   sarl_set_iterator_domain_at_end(struct Sarl_SetIterator *a_it)
{
  Sarl_DomainSetIterator *it = static_cast<Sarl_DomainSetIterator*>(a_it);
  return sarl_relation_iterator_at_end(it->rel_it);
};

static void  sarl_set_iterator_domain_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_DomainSetIterator *it = static_cast<Sarl_DomainSetIterator*>(a_it);
  return sarl_relation_iterator_reset(it->rel_it);
};

/* reference counting interface */
void sarl_set_iterator_domain_decr_ref(struct Sarl_SetIterator *a_it)
{
  Sarl_DomainSetIterator *it = static_cast<Sarl_DomainSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_relation_iterator_decr_ref(it->rel_it);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_domain_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_DomainSetIterator *org_it = 
    static_cast<Sarl_DomainSetIterator*>(a_it);
  
  Sarl_DomainSetIterator* copy_it  = new Sarl_DomainSetIterator();
  sarl_set_iterator_init(copy_it, &s_domain_iterator_table);
  copy_it->rel_it = sarl_relation_iterator_copy(org_it->rel_it);

  return copy_it;
}

struct Sarl_SetIterator *sarl_relation_iterator_domain(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_DomainSetIterator* it  = new Sarl_DomainSetIterator();
  sarl_set_iterator_init(it, &s_domain_iterator_table);

  it->rel_it = sarl_relation_iterator_obtain_ownership(a_it);
  return it;
};

