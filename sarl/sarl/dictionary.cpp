extern "C" {

#include <sarl/dictionary.h>
#include <sarl/string.h> 
#include <sarl/index.h>
}

#include <sarl/dictionary_impl.h>
#include <sarl/test.h>

/* construction of set objects */

struct Sarl_Dictionary *
  sarl_dictionary_copy(
    struct Sarl_Dictionary *d
  )
{
  return d->funcs->copy(d);
};


Sarl_Index
  sarl_dictionary_get_index(
    struct Sarl_Dictionary *d,
    struct Sarl_String *s)
{
  return d->funcs->get_index(d, s);
};

Sarl_String*
  sarl_dictionary_get_string(
    struct Sarl_Dictionary *d,
    Sarl_Index index)
{
  return d->funcs->get_string(d, index);
};


struct Sarl_SetIterator*
  sarl_dictionary_get_indexes(
    struct Sarl_Dictionary *d
  )
{
  return d->funcs->get_indexes(d);
};

extern void
  sarl_dictionary_incr_ref(
    struct Sarl_Dictionary *d
  )
{
  sarl_ref_count_incr(&d->ref_count);
};


extern void
  sarl_dictionary_decr_ref(
    struct Sarl_Dictionary *d
  )
{
  if ( sarl_ref_count_decr(&d->ref_count) ) {
    delete d;
  }
};






