#ifndef SARL_PLAIN_DICTIONARY_IMPL_H
#define SARL_PLAIN_DICTIONARY_IMPL_H

#include "dictionary_impl.h"

#include <map>
#include <string>

struct Sarl_PlainDictionary : Sarl_Dictionary
{
  typedef std::map<Sarl_Index,std::string> LabelMap;
  typedef std::map<std::string, Sarl_Index> IndexMap;

  LabelMap label_map;
  IndexMap index_map;
};

#endif
