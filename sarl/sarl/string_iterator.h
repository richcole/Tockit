#ifndef SARL_STRING_ITERATOR_H
#define SARL_STRING_ITERATOR_H

#include <sarl/index.h>

struct Sarl_String;

typedef char Sarl_Char;
typedef short Sarl_Short;


/* construction of set objects */
extern struct Sarl_StringIterator *
  sarl_string_iterator_create(struct Sarl_String *);

extern struct Sarl_StringIterator *
  sarl_string_iterator_copy(
    struct Sarl_StringIterator *s
  );


/* reference counting interface */
extern void sarl_string_decr_ref(struct Sarl_String *);
extern void sarl_string_incr_ref(struct Sarl_String *);

#endif
