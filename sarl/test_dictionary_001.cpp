extern "C" {
#include <sarl/dictionary.h>
#include <sarl/string.h>
#include <sarl/test.h>
#include <sarl/set_iterator.h>
}

#include <string>
#include <sstream>
#include <set>

using namespace std;

int main()
{
  Sarl_Dictionary* d = sarl_dictionary_create();

  std::string test_string = 
    "This is a test string. The purpose is to test the use of dictionary\n"
    "class in sarl.";

  istringstream input(test_string);
  string word;
  set<string> word_set;
  
  while( !input.eof() && ! (input >> word).fail() ) {
    Sarl_String* s = sarl_string_create_from_chars(word.c_str(), word.size());
    sarl_string_release_ownership(s);
    sarl_dictionary_get_index(d, s);
    sarl_string_decr_ref(s);

    word_set.insert(word);
  }

  Sarl_SetIterator *indexes = sarl_dictionary_get_indexes(d);
  
  SARL_TEST_ASSERT_EQ(
    (Sarl_Index)word_set.size(), 
    sarl_set_iterator_count_remaining(indexes)
  );

  SARL_SET_ITERATOR_FOR(indexes) {
    Sarl_String *s = 
      sarl_dictionary_get_string(d, sarl_set_iterator_value(indexes));
    
    SARL_TEST_ASSERT(
      word_set.find(sarl_string_get_chars(s)) !=
      word_set.end()
    );

    sarl_string_decr_ref(s);
  };
  
  sarl_dictionary_decr_ref(d);
  sarl_set_iterator_decr_ref(indexes);

  return 0;
};

    
