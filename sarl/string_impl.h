#ifndef SARL_STRING_IMPL_H
#define SARL_STRING_IMPL_H

typedef char  Sarl_Char;
typedef short Sarl_Short;

struct Sarl_String
{
  Sarl_RefCount   ref_count;
  Sarl_Ownership  ownership;

  Sarl_Char  *char_buf;
  Sarl_Short *short_buf;
  Sarl_Index  length;
  Sarl_Index  capacity;
};

inline void 
  sarl_string_init(
    struct Sarl_String *s
  )
{
  sarl_ref_count_init(&s->ref_count);

  s->ownership = SARL_HAS_OWNER;
  s->char_buf  = 0;
  s->short_buf = 0;
  s->length    = 0;
  s->capacity  = 0;
}


#endif
