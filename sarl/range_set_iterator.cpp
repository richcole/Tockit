extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/domain_set_iterator_impl.h>

struct Sarl_SetIterator *sarl_relation_iterator_domain(
  struct Sarl_RelationIterator *a_it)
{
  Sarl_DomainSetIterator* it  = new Sarl_DomainSetIterator();
  sarl_set_iterator_init(it, &s_domain_iterator_table);

  it->rel_it = a_it;
  sarl_relation_iterator_incr_ref(it->rel_it);
  return it;
};
