extern "C" {

#include <sarl/global.h>
#include <sarl/dictionary.h>
#include <sarl/string.h>
#include <sarl/ref_count.h>
#include <sarl/dictionary_indexes_set_iterator.h>
}

#include <sarl/dictionary_impl.h>
#include <sarl/test.h>
#include <sarl/plain_dictionary_impl.h>


#include <map>
#include <string>

static Sarl_Index               
  sarl_dictionary_plain_get_index(struct Sarl_Dictionary *, Sarl_String *s);

static struct Sarl_String*      
  sarl_dictionary_plain_get_string(struct Sarl_Dictionary *, Sarl_Index);

static struct Sarl_SetIterator*      
  sarl_dictionary_plain_get_indexes(struct Sarl_Dictionary *);

static struct Sarl_Dictionary*  
  sarl_dictionary_plain_copy(struct Sarl_Dictionary *);

struct Sarl_DictionaryFunctionTable s_plainDictionaryTable = 
{
  sarl_dictionary_plain_get_index,
  sarl_dictionary_plain_get_string,
  sarl_dictionary_plain_get_indexes,
  sarl_dictionary_plain_copy,
};

static struct Sarl_Dictionary* 
  sarl_dictionary_plain_create()
{
  Sarl_PlainDictionary *result = new Sarl_PlainDictionary();
  sarl_dictionary_init(result, &s_plainDictionaryTable);
  return result;
};

struct Sarl_Dictionary*
  sarl_dictionary_create()
{
  return sarl_dictionary_plain_create();
}

static struct Sarl_Dictionary*  
  sarl_dictionary_plain_copy(struct Sarl_Dictionary *d)
{
  SARL_NOT_IMPLEMENTED;
  return 0;
};


static Sarl_Index 
  sarl_dictionary_plain_get_index(
    struct Sarl_Dictionary* ap_d, 
    struct Sarl_String*     s
  )
{
  Sarl_PlainDictionary*    d       = static_cast<Sarl_PlainDictionary*>(ap_d);
  std::string              str     = sarl_string_get_chars(s);
  Sarl_PlainDictionary::IndexMap::const_iterator it = d->index_map.find(str);
  Sarl_Index               result;

  if ( it == d->index_map.end() ) {
    for(
      result=d->index_map.size(); 
      result!=(Sarl_Index)d->index_map.size()-1; 
      ++result) 
    {
      if ( result == 0 ) continue;
      if ( d->label_map.find(result) == d->label_map.end() ) {
        d->index_map[str] = result;
        d->label_map[result] = str;
        return result;
      }
    }
  }
  else {
    return it->second;
  };
  
      

  return 0;
};


static struct Sarl_String*      
  sarl_dictionary_plain_get_string(
    struct Sarl_Dictionary *ap_d, 
    Sarl_Index index
  )
{
  Sarl_PlainDictionary*    d       = static_cast<Sarl_PlainDictionary*>(ap_d);
  Sarl_PlainDictionary::LabelMap::const_iterator it = d->label_map.find(index);

  return 
    ( it != d->label_map.end() )
    ? sarl_string_create_from_chars(it->second.c_str(), it->second.size()) 
    : 0;
};


static struct Sarl_SetIterator*      
  sarl_dictionary_plain_get_indexes(struct Sarl_Dictionary *ap_d)
{
  Sarl_PlainDictionary*    d       = static_cast<Sarl_PlainDictionary*>(ap_d);
  return sarl_set_iterator_dictionary_indexes_create(d);
};

