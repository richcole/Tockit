extern "C" {

#include <sarl/global.h>
#include <sarl/dictionary_indexes_set_iterator.h>
#include <sarl/set.h>
#include <sarl/set_iterator.h>
#include <sarl/ref_count.h>
#include <sarl/dictionary.h>
  
}

#include <sarl/set_impl.h>
#include <sarl/set_iterator_impl.h>
#include <sarl/test.h>
#include <sarl/dictionary_impl.h>
#include <sarl/plain_dictionary_impl.h>

using namespace std;

struct Sarl_DictionaryIndexesSetIterator : Sarl_SetIterator
{
  typedef Sarl_PlainDictionary::LabelMap::const_iterator Iterator;

  Sarl_PlainDictionary*  d;
  Iterator          it;
};

/* function prototypes used in function table declaired below */

static void sarl_set_iterator_dictionary_indexes_next_gte(
  struct Sarl_SetIterator *it, 
  Sarl_Index value);

static void sarl_set_iterator_dictionary_indexes_next(
  struct Sarl_SetIterator *it);

static Sarl_Index sarl_set_iterator_dictionary_indexes_value(
  struct Sarl_SetIterator *it);

static int sarl_set_iterator_dictionary_indexes_at_end(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_dictionary_indexes_reset(
  struct Sarl_SetIterator *it);

static void sarl_set_iterator_dictionary_indexes_decr_ref(
  struct Sarl_SetIterator *it);

static struct Sarl_SetIterator* sarl_set_iterator_dictionary_indexes_copy(
  struct Sarl_SetIterator *it);

/* function prototypes used in function table declaired below */

struct Sarl_SetIteratorFunctionTable s_DictionaryIndexesIteratorTable = 
{
  sarl_set_iterator_dictionary_indexes_next_gte,
  sarl_set_iterator_dictionary_indexes_next,
  sarl_set_iterator_dictionary_indexes_value,
  sarl_set_iterator_dictionary_indexes_at_end,
  sarl_set_iterator_dictionary_indexes_reset,
  sarl_set_iterator_dictionary_indexes_decr_ref,
  sarl_set_iterator_dictionary_indexes_copy
};

/* constructive operations */

struct Sarl_SetIterator *
  sarl_set_iterator_dictionary_indexes_create(
    struct Sarl_PlainDictionary *a_d)
{
  Sarl_DictionaryIndexesSetIterator* result  = 
    new Sarl_DictionaryIndexesSetIterator();

  sarl_set_iterator_init(result, &s_DictionaryIndexesIteratorTable);

  Sarl_PlainDictionary *d = static_cast<Sarl_PlainDictionary*>(a_d);

  sarl_dictionary_incr_ref(d);
  result->d = d;
  result->it = d->label_map.begin();

  return result;
};

/* set_iterator moving operations */
static void  sarl_set_iterator_dictionary_indexes_next_gte(
  struct Sarl_SetIterator *a_it, 
  Sarl_Index value)
{
  
  // skip if we are past value already
  if ( value <= sarl_set_iterator_value(a_it) ) {
    return;
  }
  
  Sarl_DictionaryIndexesSetIterator *it = 
    static_cast<Sarl_DictionaryIndexesSetIterator*>(a_it);

  it->it = it->d->label_map.lower_bound(value);
}

static void  sarl_set_iterator_dictionary_indexes_next(struct Sarl_SetIterator *a_it)
{
  Sarl_DictionaryIndexesSetIterator *it = 
    static_cast<Sarl_DictionaryIndexesSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    it->it++;
  }
}

static Sarl_Index sarl_set_iterator_dictionary_indexes_value(struct Sarl_SetIterator *a_it)
{
  Sarl_DictionaryIndexesSetIterator *it = 
    static_cast<Sarl_DictionaryIndexesSetIterator*>(a_it);
  if ( ! sarl_set_iterator_at_end(it) ) {
    return it->it->first;
  }
  else {
    return 0;
  }
}

static int sarl_set_iterator_dictionary_indexes_at_end(
  struct Sarl_SetIterator *a_it)
{
  Sarl_DictionaryIndexesSetIterator *it = 
    static_cast<Sarl_DictionaryIndexesSetIterator*>(a_it);
  return it->it == it->d->label_map.end();
};

static void  sarl_set_iterator_dictionary_indexes_reset(struct Sarl_SetIterator *a_it) 
{
  Sarl_DictionaryIndexesSetIterator *it = 
    static_cast<Sarl_DictionaryIndexesSetIterator*>(a_it);
  it->it = it->d->label_map.begin();
};

/* reference counting interface */
void sarl_set_iterator_dictionary_indexes_decr_ref(
  struct Sarl_SetIterator *a_it)
{
  Sarl_DictionaryIndexesSetIterator *it = 
    static_cast<Sarl_DictionaryIndexesSetIterator*>(a_it);
  if ( sarl_ref_count_decr(&it->ref_count) ) {
    sarl_dictionary_decr_ref(it->d);
    delete it;
  }
}

static struct Sarl_SetIterator* sarl_set_iterator_dictionary_indexes_copy(
  struct Sarl_SetIterator *a_it)
{
  Sarl_DictionaryIndexesSetIterator *org_it = 
    static_cast<Sarl_DictionaryIndexesSetIterator*>(a_it);
  
  Sarl_DictionaryIndexesSetIterator* copy_it  = 
    new Sarl_DictionaryIndexesSetIterator();
  sarl_set_iterator_init(copy_it, &s_DictionaryIndexesIteratorTable);

  copy_it->d = org_it->d;
  sarl_dictionary_incr_ref(copy_it->d);
  
  copy_it->it = org_it->it;
  return copy_it;
}




