#ifndef SARL_DICTIONARY_IMPL_H
#define SARL_DICTIONARY_IMPL_H

extern "C" {

#include <sarl/dictionary.h>
#include <sarl/string.h>
#include <sarl/ref_count.h>
  
}

#include <sarl/dictionary_impl.h>
#include <sarl/test.h>


#include <map>
#include <string>

using namespace std;

struct Sarl_PlainDictionary : Sarl_Dictionary
{
	typedef map<Sarl_Index,string>  LabelMap;
	typedef map<string, Sarl_Index> IndexMap;

	LabelMap label_map;
	IndexMap index_map;
};

static Sarl_Index               
  sarl_plain_dictionary_get_index(struct Sarl_Dictionary *, Sarl_String *s);

static struct Sarl_String*      
  sarl_plain_dictionary_get_string(struct Sarl_Dictionary *, Sarl_Index);

static struct Sarl_SetIterator*      
  sarl_plain_dictionary_get_indexes(struct Sarl_Dictionary *);

static struct Sarl_Dictionary*  
  sarl_plain_dictionary_copy(struct Sarl_Dictionary *);

struct Sarl_DictionaryFunctionTable s_plainDictionaryTable = 
{
  sarl_plain_dictionary_get_index,
  sarl_plain_dictionary_get_string,
  sarl_plain_dictionary_get_indexes,
  sarl_plain_dictionary_plain_copy
};

static Sarl_Index sarl_plain_dictionary_get_index(
  struct Sarl_Dictionary *ap_d, Sarl_String *s)
{
	Sarl_PlainDictionary*    d       = static_cast<Sarl_PlainDictionary*>(ap_d);
	std::string              str     = s->get_chars;
	LabelMap::const_iterator it      = d->index_map.find(str);
	Sarl_Index               result;

	if ( it == d->index_map.end() ) {
		for(result=d->index_map.size(); result != index_map.size()-1; ++result) {
			if ( result == 0 ) continue;
			if ( d->string_map.find(result) == d->string_map.end() ) {
				index_map[str] = s->result;
				string_map[result] = str;
				return result;
			}
		}
	}

	return 0;
};


static struct Sarl_String*      
  sarl_plain_dictionary_get_string(struct Sarl_Dictionary *, Sarl_Index)
{
	Sarl_PlainDictionary*    d       = static_cast<Sarl_PlainDictionary*>(ap_d);
	std::string              str     = s->get_chars;
	LabelMap::const_iterator it      = d->index_map.find(str);
	Sarl_Index               result;

	if ( it == d->index_map.end() ) {
		for(result=d->index_map.size(); result != index_map.size()-1; ++result) {
			if ( result == 0 ) continue;
			if ( d->string_map.find(result) == d->string_map.end() ) {
				index_map[str] = s->result;
				string_map[result] = str;
				return result;
			}
		}
	}

	return 0;
};


static struct Sarl_SetIterator*      
  sarl_plain_dictionary_get_indexes(struct Sarl_Dictionary *);

static struct Sarl_Dictionary*  
  sarl_plain_dictionary_copy(struct Sarl_Dictionary *);






#endif
