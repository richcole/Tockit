#include <sarl/cpp/Relation.h>
#include <sarl/cpp/RelationIterator.h>
#include <iostream>

using namespace std;


void main()
{
  Relation s;

  s.insert(1,2);
  s.insert(1,3);
  s.insert(1,4);

  s.insert(2,2);
  s.insert(2,3);
  s.insert(2,4);

  s.insert(3,2);
  s.insert(4,3);
  s.insert(5,4);

  RelationIterator t = RelationIterator(s);
  RelationIterator it = t.join(t);
  
  for(it.reset(); !it.at_end(); it.next()) {
    Sarl_Pair p = it.value();
    cerr << p.dom << "," << p.rng << endl;
  }
    
};


