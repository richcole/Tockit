extern "C" {
#include <sarl/lattice.h>
#include <sarl/set.h>
#include <sarl/relation.h>
#include <sarl/transitive_relation.h>
#include <sarl/lattice_iterator.h>
#include <sarl/set_iterator.h>
#include <sarl/relation_iterator.h>
}

#include <sarl/lattice_impl.h>
#include <sarl/lattice_iterator_impl.h>
#include <sarl/set_impl.h>
#include <sarl/relation_impl.h>

struct Sarl_SetIterator* sarl_relation_iterator_return_empty_set(
  Sarl_RelationIterator* r_it)
{
  Sarl_Set* empty_set = sarl_set_create();
  Sarl_SetIterator* result = sarl_set_iterator_create(empty_set);
  sarl_set_decr_ref(empty_set);
  return result;
};

struct Sarl_SetIterator* sarl_relation_iterator_intent_union(
  Sarl_RelationIterator* r_it, Sarl_SetIterator* s
)
{
  sarl_relation_iterator_aggregate(
    r_it, s, 
    sarl_relation_iterator_intent, 
    sarl_set_iterator_union, 
    sarl_relation_iterator_return_empty_set
  );
};

struct Sarl_SetIterator* sarl_relation_iterator_extent_union(
  Sarl_RelationIterator* r_it, Sarl_SetIterator* s
)
{
  sarl_relation_iterator_aggregate(
    r_it, s, 
    sarl_relation_iterator_extent, 
    sarl_set_iterator_union, 
    sarl_relation_iterator_return_empty_set
  );
};

struct Sarl_Lattice* sarl_lattice_copy(
  Sarl_LatticeIterator* it
)
{
  Sarl_Lattice* lattice = new Sarl_Lattice();
  sarl_lattice_init(lattice);
  
  lattice->intent = sarl_relation_create();
  lattice->extent = sarl_relation_create();
  lattice->order  = sarl_transitive_relation_create();

  Sarl_Index concept = 1;
  SARL_LATTICE_ITERATOR_FOR(it) {

    // determine intent
    Sarl_SetIterator* intent = sarl_lattice_iterator_intent(it);
    SARL_SET_ITERATOR_FOR(intent) {
      sarl_relation_insert(
        lattice->intent, concept, sarl_set_iterator_value(intent)
      );
    };

    // determine extent
    Sarl_SetIterator* extent = sarl_lattice_iterator_intent(it);
    SARL_SET_ITERATOR_FOR(extent) {
      sarl_relation_insert(
        lattice->extent, concept, sarl_set_iterator_value(extent)
      );
    };

    // determine upper cover
    sarl_set_iterator_reset(intent);
    sarl_set_iterator_release_ownership(intent);

    Sarl_RelationIterator* intent_it = 
      sarl_relation_iterator_create(lattice->intent);
    
    Sarl_SetIterator* down_set = 
      sarl_relation_iterator_extent_set(intent_it, intent);
    
    SARL_SET_ITERATOR_FOR(down_set) {
      sarl_transitive_relation_insert(
        lattice->order, sarl_set_iterator_value(down_set), concept
      );
    };

    sarl_set_iterator_decr_ref(intent);
    sarl_set_iterator_decr_ref(extent);
    sarl_set_iterator_decr_ref(down_set);

    // advance to the next concept
    ++concept;
  };

  // calculate the contingents
  
  Sarl_RelationIterator* intent_it = 
    sarl_relation_iterator_create(lattice->intent);

  Sarl_SetIterator* c_it = sarl_relation_domain(lattice->intent);

  SARL_SET_ITERATOR_FOR(c_it) {
    Sarl_SetIterator *upper_covers;
    Sarl_SetIterator *upper_intents;
    Sarl_SetIterator *intent;
    Sarl_SetIterator *contingent;

    upper_covers = sarl_transitive_relation_upper_covers(
      lattice->order,
      sarl_set_iterator_value(c_it)
    );
    sarl_set_iterator_release_ownership(upper_covers);
      
    upper_intents = 
      sarl_relation_iterator_intent_union(intent_it, upper_covers);
    sarl_set_iterator_release_ownership(upper_intents);

    intent =
      sarl_relation_iterator_intent(intent_it, sarl_set_iterator_value(c_it));
    
    contingent = sarl_set_iterator_minus(intent, upper_intents);

    SARL_SET_ITERATOR_FOR(contingent) {
      sarl_relation_insert(
        lattice->intent_contingent, 
        sarl_set_iterator_value(c_it), 
        sarl_set_iterator_value(contingent)
      );
    };

    sarl_set_iterator_decr_ref(upper_covers);
    sarl_set_iterator_decr_ref(upper_intents);
    sarl_set_iterator_decr_ref(intent);
    sarl_set_iterator_decr_ref(contingent);
  };
  sarl_relation_iterator_decr_ref(intent_it);
  
  Sarl_RelationIterator* extent_it = 
    sarl_relation_iterator_create(lattice->intent);

  SARL_SET_ITERATOR_FOR(c_it) {
    Sarl_SetIterator *lower_covers;
    Sarl_SetIterator *lower_extents;
    Sarl_SetIterator *extent;
    Sarl_SetIterator *contingent;

    lower_covers = sarl_transitive_relation_lower_covers(
      lattice->order,
      sarl_set_iterator_value(c_it)
    );
    sarl_set_iterator_release_ownership(lower_covers);
      
    lower_extents = 
      sarl_relation_iterator_extent_union(extent_it, lower_covers);
    sarl_set_iterator_release_ownership(lower_extents);

    extent =
      sarl_relation_iterator_intent(extent_it, sarl_set_iterator_value(c_it));
    
    contingent = sarl_set_iterator_minus(extent, lower_extents);

    SARL_SET_ITERATOR_FOR(contingent) {
      sarl_relation_insert(
        lattice->extent_contingent, 
        sarl_set_iterator_value(c_it), 
        sarl_set_iterator_value(contingent)
      );
    };

    sarl_set_iterator_decr_ref(lower_covers);
    sarl_set_iterator_decr_ref(lower_extents);
    sarl_set_iterator_decr_ref(extent);
    sarl_set_iterator_decr_ref(contingent);
  };
  sarl_relation_iterator_decr_ref(extent_it);
  
  return lattice;
}

void
sarl_lattice_decr_ref(
	struct Sarl_Lattice *a_lattice)
{
  if ( sarl_ref_count_decr(&a_lattice->ref_count) ) {
    sarl_relation_decr_ref(a_lattice->intent);
    sarl_relation_decr_ref(a_lattice->extent);
    sarl_transitive_relation_decr_ref(a_lattice->order);
    delete a_lattice;
  }
};

void
sarl_lattice_incr_ref(
	struct Sarl_Lattice *ap_lattice)
{
  sarl_ref_count_incr(&ap_lattice->ref_count);
};

void sarl_lattice_decr_ref(struct Sarl_Set *S)
{
  if ( sarl_ref_count_decr(&S->ref_count) ) {
    delete S;
  };
}

void sarl_lattice_incr_ref(struct Sarl_Set *S)
{
  sarl_ref_count_incr(&S->ref_count);
}

