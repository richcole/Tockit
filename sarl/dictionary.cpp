"C" {

#include <sarl/dictionary.h>
#include <sarl/string.h> 
#include <sarl/sarl_index.h>
 
}

#include <sarl/dictionary_impl.h>
#include <sarl/test.h>

/* construction of set objects */
struct Sarl_Dictionary *
sarl_dictionary_create()
{
	return sarl_dictionary_plain_create();
}


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
    struct Sarl_Index index)
{
	return d->funcs->get_string(d, index);
};


struct Sarl_SetIterator*
  sarl_dictionary_get_indexes(
    struct Sarl_Dictionary *d
  )
{
	return f->funcs->get_indexes(d);
};






