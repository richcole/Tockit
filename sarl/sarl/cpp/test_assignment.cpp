#include <sarl/test.h>
#include <iostream>

using namespace std;

struct Object 
{
  Object() {
    num_owners = 0;
    num_refs = 0;
    ++num_objects;
    ++num_creates;
  };

  ~Object() 
  {
    --num_objects;
  };
  
  int num_owners;
  int num_refs;
  static int num_objects;
  static int num_creates;

  void decr() 
  {
    if ( (--num_refs) == 0 ) {
      delete this;
    }
  }

  void incr()
  {
    num_refs++;
  };

};

int Object::num_objects;
int Object::num_creates;

class Assignable 
{
  friend int main();

  Object* object;
public:

  class Temporary 
  {
  public:
    Object *object;

    Temporary(Object *a_object) {
      object = a_object;
      object->incr();
    }
    
    ~Temporary() 
    {
      object->decr();
    };
  };

  Assignable(Temporary const& temp) 
  {
    copy(temp.object);
  };

  void create() 
  {
    object = new Object();
    object->num_owners++;
    object->incr();
  };

  void copy(Object* a_object)
  {
    object = a_object;
    object->num_owners++;
    object->incr();
  }

  ~Assignable() 
  {
    object->decr();
  };

  Assignable() 
  {
    create();
  };
  
  Assignable(Assignable const& a) 
  {
    if ( a.object->num_owners ) {
      create();
    }
    else {
      copy(a.object);
    }
  };

  Assignable& operator=(Assignable const& a) 
  {
    if ( this == &a ) return *this;
    
    object->decr();
    if ( a.object->num_owners ) {
      create();
    }
    else {
      copy(a.object);
    }
  }

  Assignable& operator=(Temporary const& a) 
  {
    object->decr();
    copy(a.object);
  }

  Temporary retn() 
  {
    object->num_owners--;

    return Temporary(object);
  };
};

Assignable::Temporary test(Assignable const& a, Assignable b)
{
  b = a;
  return b.retn();
};


int main()
{
  
  {
    Assignable a, b, c;
  
    c = test(a,b);
    SARL_TEST_ASSERT_EQ(c.object->num_owners, 1);
    SARL_TEST_ASSERT_EQ(a.object->num_owners, 1);
    SARL_TEST_ASSERT_EQ(b.object->num_owners, 1);
    SARL_TEST_ASSERT_EQ(Object::num_objects, 3);
    SARL_TEST_ASSERT_EQ(Object::num_creates, 5);
  }

  SARL_TEST_ASSERT_EQ(Object::num_objects, 0);
};

