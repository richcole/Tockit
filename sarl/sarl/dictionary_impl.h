#ifndef SARL_DICTIONARY_IMPL_H
#define SARL_DICTIONARY_IMPL_H

extern "C" {
#include <sarl/ref_count.h>
#include <sarl/ownership.h>
}

struct Sarl_DictionaryFunctionTable 
{
  Sarl_Index               
    (*get_index)(struct Sarl_Dictionary *, Sarl_String *s);
  
  struct Sarl_String*      
    (*get_string)(struct Sarl_Dictionary *, Sarl_Index);

  struct Sarl_SetIterator*      
    (*get_indexes)(struct Sarl_Dictionary *);

  struct Sarl_Dictionary*  
    (*copy)(struct Sarl_Dictionary *);
};

struct Sarl_Dictionary
{
  Sarl_RefCount   ref_count;
  Sarl_Ownership  ownership;

  struct Sarl_DictionaryFunctionTable* funcs;
};

inline void 
  sarl_dictionary_init(
    struct Sarl_Dictionary* d,
    struct Sarl_DictionaryFunctionTable* funcs
  )
{
  sarl_ref_count_init(&d->ref_count);
  d->ownership = SARL_HAS_OWNER;
  d->funcs = funcs;
}

#endif
