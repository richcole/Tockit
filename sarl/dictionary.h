#ifndef SARL_STRING_H
#define SARL_STRING_H

#include <sarl/index.h>

struct Sarl_Dictionary;

typedef char Sarl_Char;
typedef short Sarl_Short;

/* construction of set objects */
extern struct Sarl_Dictionary *
  sarl_dictionary_create();

extern struct Sarl_Dictionary *
  sarl_dictionary_copy(
    struct Sarl_Dictionary *d
  );

extern Sarl_Index*
  sarl_dictionary_get_index(
    struct Sarl_Dictionary *d,
    struct Sarl_String *s);

extern Sarl_String*
  sarl_dictionary_get_string(
    struct Sarl_Dictionary *d,
    struct Sarl_Index *s);

#endif
