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

#include "nary_relation_iterator_impl.h"
#include "nary_relation_iterator_impl.h"

struct Sarl_NaryRelationIndexSetIterator : public Sarl_SetIterator 
{
  Sarl_Index i;
  Sarl_NaryRelation *r;
};

extern Sarl_SetIterator* 
  nary_relation_iterator_create(
    Sarl_NaryRelation *r
  )
{
  Sarl_NaryRelationIterator *result = new Sarl_NaryRelationIterator();
  sarl_iterator_init(result);
  result->it = sarl_nary_relation_index_set_iterator_create(r);
  result->r = r;
  sarl_nary_relation_incr_ref(result->r);

  return result;
};

extern Sarl_SetIterator* 
  nary_relation_iterator_set_iterator(
    Sarl_NaryRelationIterator *r_it
  )
{
  return sarl_set_iterator_obtain_ownership(r_it->it);
};

/* prime the domain values if null */
void prime(Sarl_NaryRelationIterator *it)
{
  Sarl_Index i;
  Sarl_SetIterator *cand = 0;
  
  for(i=0;i<it->r->arity;++i) {
    if ( it->domains[i] == 0 ) {
      if ( i == 0 ) {
	it->domains[i] = sarl_map_range(it->r->maps[i]);
      }
      else {
	SARL_SET_ITERATOR_FOR(cand) {
	  if ( it->domains[i] == 0 ) {
	    it->domains[i] = sarl_map_image(sarl_set_iterator_value(cand));
	  }
	  else {
	    Sarl_SetIterator *temp;
	    sarl_set_iterator_release_ownership(it->domains[i]);
	    temp = 
	      sarl_set_iterator_union(
		it->domains[i],
		sarl_set_iterator_value(cand)
	      );
	    sarl_set_iterator_decr_ref(it->domains[i]);
	    it->domains[i] = temp;
	  }
	}
      }
    } 
    if ( cand == 0 ) {
      cand = sarl_map_coimage(
	it->r->maps[i], 
	sarl_set_iterator_value(it->domains[i])
      );
    }
    else {
      Sarl_SetIterator *temp;
      sarl_set_iterator_release_ownership(cand);
      temp = sarl_set_iterator_meet(
	cand,
	sarl_map_coimage(
	  it->r->maps[i],
	  sarl_set_iterator_value(it->domains[i])
	)
      );
      sarl_set_iterator_decr_ref(cand);
      cand = temp;
    }
  }
}

int next(Sarl_NaryRelationIterator *it)
{
  Sarl_Index i = it->r->arity-1;

  sarl_set_iterator_next(it->domains[i]);
  while ( sarl_set_iterator_at_end(it->domains[i]) ) {
    sarl_set_iterator_decr_ref(it->domains[i]);
    it->domains[i] = 0;
    if ( i == 0 ) {
      return 1; // we are now at end
    }
    else {
      --i;
      sarl_set_iterator_next(it->domains[i]);
    }
  }
};

  

	    
	    


