#ifndef SARL_DICTIONARY_H
#define SARL_DICTIONARY_H

#include <sarl/index.h>

struct Sarl_Dictionary;

/* construction of set objects */
extern struct Sarl_Dictionary *
  sarl_dictionary_create();

extern struct Sarl_Dictionary *
  sarl_dictionary_copy(
    struct Sarl_Dictionary *d
  );

extern Sarl_Index
  sarl_dictionary_get_index(
    struct Sarl_Dictionary *d,
    struct Sarl_String *s);

extern Sarl_String*
  sarl_dictionary_get_string(
    struct Sarl_Dictionary *d,
    Sarl_Index index);

extern struct Sarl_SetIterator*
  sarl_dictionary_get_indexes(
    struct Sarl_Dictionary *d
  );

extern void
  sarl_dictionary_incr_ref(
    struct Sarl_Dictionary *d
  );

extern void
  sarl_dictionary_decr_ref(
    struct Sarl_Dictionary *d
  );

#endif
