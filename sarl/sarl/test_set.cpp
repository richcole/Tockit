extern "C" {

#include <sarl/set.h>
#include <sarl/set_iterator.h>
  
}

#include <iostream>
using namespace std;


int main()
{
  Sarl_Index        i;
  Sarl_Set*         set;
  Sarl_SetIterator* it;

  set = sarl_set_create();
  
  for(i=0;i<10;i++) {
    sarl_set_insert(set, i);
  }

  it = sarl_set_iterator_create(set);
  sarl_set_decr_ref(set);

  i=0;
  SARL_SET_ITERATOR_FOR(it) {
    if (i != sarl_set_iterator_value(it)) {
      cerr << "Error, set content doesn't match" << endl;
    }
    ++i;
  };

  sarl_set_iterator_decr_ref(it);
};
