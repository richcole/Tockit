#ifndef SARL_DICTIONARY_IMPL_H
#define SARL_DICTIONARY_IMPL_H

#include "dictionary_impl.h"

#include <map>
#include <string>

struct Sarl_PlainDictionary : Sarl_Dictionary
{
	std::map<Sarl_Index,std::string>  label_map;
	std::map<std::string, Sarl_Index> index_map;
};

#endif
