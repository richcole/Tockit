#ifndef SARL_STRING_H
#define SARL_STRING_H

#include <sarl/index.h>

struct Sarl_String;

typedef char Sarl_Char;
typedef short Sarl_Short;

/* construction of set objects */

extern struct Sarl_String *
  sarl_string_create();

extern struct Sarl_String *
  sarl_string_create_from_chars(
    Sarl_Char const* s, Sarl_Index length
  );

extern struct Sarl_String *
  sarl_string_create_from_shorts(
    Sarl_Short const* s, Sarl_Index length
  );

extern struct Sarl_String *
  sarl_string_copy(
    struct Sarl_String *s
  );

extern void 
  sarl_string_set_chars(
    struct Sarl_String *s,
    Sarl_Char const *s, 
    Sarl_Index length
  );

extern void 
  sarl_string_set_shorts(
    struct Sarl_String *s, 
    Sarl_Short const *s, 
    Sarl_Index length
  );

extern Sarl_Char const* 
  sarl_string_get_chars(
    struct Sarl_String *s
  );

extern Sarl_Short const* 
  sarl_string_get_shorts(
    struct Sarl_String *s
  );

extern Sarl_Index 
  sarl_string_length(
    struct Sarl_String *s
  );

extern struct Sarl_String *
  sarl_string_obtain_ownership(
    struct Sarl_String *s
	);

extern struct Sarl_String *
  sarl_string_release_ownership(
    struct Sarl_String *s
  );

/* reference counting interface */

extern void 
  sarl_string_decr_ref(
    struct Sarl_String *s
  );

extern void 
  sarl_string_incr_ref(
    struct Sarl_String *s
  );

#endif
