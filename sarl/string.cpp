extern "C" {

#include <sarl/string.h>
#include <sarl/ref_count.h>
  
}

#include <string.h>
#include "string_impl.h"

static Sarl_Short null_short_buf[1] = { 0 };
static Sarl_Char  null_char_buf[1] = { 0 };

struct Sarl_String *
  sarl_string_create()
{
  struct Sarl_String *result = new Sarl_String;
  sarl_string_init(result);
  return result;
};

struct Sarl_String *
  sarl_string_copy(
    struct Sarl_String *s
  )
{
  struct Sarl_String *result = new Sarl_String;
  sarl_string_init(result);

  result->length = s->length;
  result->capacity = s->length;

  if ( s->char_buf != 0 && s->length != 0 ) {
    result->char_buf = new Sarl_Char[s->length];
    memcpy(result->char_buf, s->char_buf, sizeof(Sarl_Char)*s->length);
  }
  else {
    s->char_buf = 0;
  }
  
  if ( s->short_buf != 0 && s->length != 0 ) {
    result->short_buf = new Sarl_Short[s->length];
    memcpy(result->short_buf, s->short_buf, sizeof(Sarl_Short)*s->length);
  }
  else {
    result->short_buf = 0;
  }

  return result;
};


void 
  sarl_string_set_chars(
    struct Sarl_String *s,
    Sarl_Char const*    str, 
    Sarl_Index length
  )
{
  Sarl_Index i;

  if ( length == 0 ) {
    if ( s->char_buf != 0 ) {
      delete s->char_buf;
    };
    if ( s->short_buf != 0 ) {
      delete s->short_buf;
    }
    s->char_buf = 0;
    s->short_buf = 0;
    s->length = 0;
  }
  else if ( length < s->capacity ) {
    Sarl_Index i;

    s->length = length;
    memcpy(s->char_buf, str, length * sizeof(*str));
    for(i=0;i<length;++i) {
      s->short_buf[i] = s->char_buf[i];
    }
  }
  else {
    s->capacity = length;
    s->length = length;
    
    if ( s->char_buf != 0 ) {
      delete s->char_buf;
    };
    s->char_buf = new Sarl_Char[length];
    s->short_buf = new Sarl_Short[length];
    memcpy(s->char_buf, str, length * sizeof(str));
    for(i=0;i<length;++i) {
      s->short_buf[i] = s->char_buf[i];
    }
  }
};


void 
  sarl_string_set_shorts(
    struct Sarl_String *s, 
    Sarl_Short const* str, 
    Sarl_Index length
  )
{
  Sarl_Index i;

  if ( length == 0 ) {
    if ( s->char_buf != 0 ) {
      delete s->char_buf;
    };
    if ( s->short_buf != 0 ) {
      delete s->short_buf;
    }
    s->char_buf = 0;
    s->short_buf = 0;
    s->length = 0;
  }
  else if ( length < s->capacity ) {
    Sarl_Index i;

    s->length = length;
    memcpy(s->short_buf, str, length * sizeof(*str));
    for(i=0;i<length;++i) {
      s->char_buf[i] = s->short_buf[i];
    }
  }
  else {
    s->capacity = length;
    s->length = length;
    
    if ( s->char_buf != 0 ) {
      delete s->char_buf;
    };
    s->char_buf = new Sarl_Char[length];
    s->short_buf = new Sarl_Short[length];
    memcpy(s->short_buf, str, length * sizeof(str));
    for(i=0;i<length;++i) {
      s->char_buf[i] = s->short_buf[i];
    }
  }
};

Sarl_Char const* 
  sarl_string_get_chars(
    struct Sarl_String *s
  )
{
  return s->char_buf == 0 ? null_char_buf : s->char_buf;
};

Sarl_Short const* 
  sarl_string_get_shorts(
    struct Sarl_String *s
  )
{
  return s->short_buf == 0 ? null_short_buf : s->short_buf;
};


Sarl_Index 
  sarl_string_length(
    struct Sarl_String *s
  )
{
  return s->length;
};

/* reference counting interface */
void sarl_string_decr_ref(struct Sarl_String *s)
{
  if ( sarl_ref_count_decr(&s->ref_count) ) {
    if ( s->char_buf ) {
      delete s->char_buf;
    }
    if ( s->short_buf ) {
      delete s->short_buf;
    }
    delete s;
  };
};

void sarl_string_incr_ref(struct Sarl_String *s)
{
  sarl_ref_count_incr(&s->ref_count);
};

