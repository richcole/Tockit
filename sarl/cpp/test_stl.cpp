#include <set>

using namespace std;

main()
{
  set<unsigned int> *S, *T;

  S = new set<unsigned int>();
  T = new set<unsigned int>();
  
  S->insert(4);
  T->insert(6);

  delete S;
  delete T;
};
