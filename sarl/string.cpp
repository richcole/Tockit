extern "C" {

#include <sarl/config.h>
#include <sarl/string.h>
#include <sarl/ref_count.h>
  
}

#include <string.h>
#include "string_impl.h"
#include "iterator_impl.h"

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
  sarl_string_create_from_chars(Sarl_Char const* chars, Sarl_Index length)
{
  struct Sarl_String *result = sarl_string_create();
  sarl_string_set_chars(result, chars, length);
  return result;
};

struct Sarl_String *
  sarl_string_create_from_shorts(Sarl_Short const* shorts, Sarl_Index length)
{
  struct Sarl_String *result = sarl_string_create();
  sarl_string_set_shorts(result, shorts, length);
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
      delete[] s->char_buf;
    };
    if ( s->short_buf != 0 ) {
      delete[] s->short_buf;
    }
    s->char_buf = 0;
    s->short_buf = 0;
    s->length = 0;
  }
  else if ( length < s->capacity ) {
    Sarl_Index i;

    s->length = length;
    memcpy(s->char_buf, str, length * sizeof(*str));
    s->char_buf[length] = 0;
    for(i=0;i<length;++i) {
      s->short_buf[i] = s->char_buf[i];
    }
    s->short_buf[length] = 0;
  }
  else {
    s->capacity = length + 1;
    s->length = length;
    
    if ( s->char_buf != 0 ) {
      delete[] s->char_buf;
    };
    if ( s->short_buf != 0 ) {
      delete[] s->short_buf;
    };
    s->char_buf = new Sarl_Char[s->capacity];
    s->short_buf = new Sarl_Short[s->capacity];
    memcpy(s->char_buf, str, length * sizeof(*str));
    s->char_buf[length] = 0;
    
    for(i=0;i<length;++i) {
      s->short_buf[i] = s->char_buf[i];
    }
    s->short_buf[length] = 0;
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
      delete[] s->char_buf;
    };
    if ( s->short_buf != 0 ) {
      delete[] s->short_buf;
    }
    s->char_buf = 0;
    s->short_buf = 0;
    s->length = 0;
  }
  else if ( length < s->capacity ) {
    Sarl_Index i;

    s->length = length;
    memcpy(s->short_buf, str, length * sizeof(*str));
    s->short_buf[length] = 0;
    for(i=0;i<length;++i) {
      s->char_buf[i] = s->short_buf[i];
    }
    s->char_buf[length] = 0;
  }
  else {
    s->capacity = length + 1;
    s->length = length;
    
    if ( s->char_buf != 0 ) {
      delete[] s->char_buf;
    };
    if ( s->short_buf != 0 ) {
      delete[] s->short_buf;
    };
    s->char_buf = new Sarl_Char[s->capacity];
    s->short_buf = new Sarl_Short[s->capacity];
    memcpy(s->short_buf, str, length * sizeof(*str));
    s->short_buf[length] = 0;
    for(i=0;i<length;++i) {
      s->char_buf[i] = s->short_buf[i];
    }
    s->char_buf[length] = 0;
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
      delete[] s->char_buf;
    }
    if ( s->short_buf ) {
      delete[] s->short_buf;
    }
    delete s;
  };
};

void sarl_string_incr_ref(struct Sarl_String *s)
{
  sarl_ref_count_incr(&s->ref_count);
};

struct Sarl_String *
  sarl_string_obtain_ownership(
    struct Sarl_String *it
  )
{
  return sarl_iterator_obtain_ownership(it, sarl_string_copy);
};

struct Sarl_String *
  sarl_string_release_ownership(
    struct Sarl_String *it
  )
{
  return sarl_iterator_release_ownership(it);
};

void
  sarl_string_append(
    struct Sarl_String *s,
    struct Sarl_String *t
  )
{
  size_t t_len = sarl_string_length(t);

  Sarl_Char*  dest_char_buf  = 0;
  Sarl_Short* dest_short_buf = 0;
  Sarl_Index  new_capacity;

  if ( s->length + t_len + 1 > s->capacity ) {
    new_capacity = s->length + t_len + 1;
    dest_char_buf = new Sarl_Char[new_capacity];
    dest_short_buf = new Sarl_Short[new_capacity];
    memcpy(dest_char_buf, s->char_buf, s->length * sizeof(Sarl_Char));
    memcpy(dest_short_buf, s->short_buf, s->length * sizeof(Sarl_Short));
    if ( s->char_buf != 0 ) {
      delete[] s->char_buf;
    }
    if ( s->short_buf != 0 ) {
      delete[] s->short_buf;
    }
    s->char_buf = dest_char_buf;
    s->short_buf = dest_short_buf;
    s->capacity = new_capacity;
  }
    
  memcpy(
    s->char_buf + s->length, 
    sarl_string_get_chars(t), 
    t_len * sizeof(Sarl_Char)
  );

  memcpy(
    s->short_buf + s->length, 
    sarl_string_get_shorts(t), 
    t_len * sizeof(Sarl_Short)
  );

  s->char_buf[t_len] = 0;
  s->short_buf[t_len] = 0;
  s->length = s->length + t_len;
};


void
  sarl_string_append_chars(
    struct Sarl_String *s,
    Sarl_Char   *t
  )
{
  size_t t_len = strlen(t);

  Sarl_Char*  dest_char_buf  = 0;
  Sarl_Short* dest_short_buf = 0;
  Sarl_Index  new_capacity;

  if ( s->length + t_len + 1 > s->capacity ) {
    new_capacity = s->length + t_len + 1;
    dest_char_buf = new Sarl_Char[new_capacity];
    dest_short_buf = new Sarl_Short[new_capacity];
    memcpy(dest_char_buf, s->char_buf, s->length * sizeof(Sarl_Char));
    memcpy(dest_short_buf, s->short_buf, s->length * sizeof(Sarl_Short));
    if ( s->char_buf != 0 ) {
      delete[] s->char_buf;
    }
    if ( s->short_buf != 0 ) {
      delete[] s->short_buf;
    }
    s->char_buf = dest_char_buf;
    s->short_buf = dest_short_buf;
    s->capacity = new_capacity;
  }
    
  memcpy(s->char_buf + s->length, t, t_len * sizeof(Sarl_Char));
  memcpy(s->short_buf + s->length, t, t_len * sizeof(Sarl_Short));
  s->char_buf[t_len] = 0;
  s->short_buf[t_len] = 0;
  s->length = s->length + t_len;
};

void
  sarl_string_append_char(
    struct Sarl_String *s,
    Sarl_Char   t
  )
{
  Sarl_Char*  dest_char_buf  = 0;
  Sarl_Short* dest_short_buf = 0;
  Sarl_Index  new_capacity;

  if ( s->length + 1 + 1 > s->capacity ) {
    new_capacity = s->capacity * 2;
    dest_char_buf = new Sarl_Char[new_capacity];
    dest_short_buf = new Sarl_Short[new_capacity];
    memcpy(dest_char_buf, s->char_buf, s->length * sizeof(Sarl_Char));
    memcpy(dest_short_buf, s->short_buf, s->length * sizeof(Sarl_Short));
    if ( s->char_buf != 0 ) {
      delete[] s->char_buf;
    }
    if ( s->short_buf != 0 ) {
      delete[] s->short_buf;
    }
    s->char_buf = dest_char_buf;
    s->short_buf = dest_short_buf;
    s->capacity = new_capacity;
  }
  
  s->char_buf[s->length] = t;
  s->short_buf[s->length] = t; 
  s->char_buf[s->length + 1] = 0;
  s->short_buf[s->length + 1] = 0;
  s->length = s->length + 1;
};
 
 
