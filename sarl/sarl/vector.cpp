extern "C" {
#include "vector.h"
};

#include "vector_impl.h"

/* construction of vector objects */
Sarl_Vector *sarl_vector_create(Sarl_Index arity)
{
  Sarl_Vector *result = new Sarl_Vector();
  sarl_ref_count_init(&result->ref_count);
  result->arity = arity;
  result->elements.resize(arity, 0);

  return result;
};

Sarl_Vector *sarl_vector_copy(Sarl_Vector *v)
{
  Sarl_Vector *result = new Sarl_Vector();
  sarl_ref_count_init(&result->ref_count);
  result->arity = v->arity;
  result->elements = v->elements;

  return result;
};


void sarl_vector_set(
  Sarl_Vector *v, Sarl_Index index, Sarl_Index value
)
{
  if ( index > 0 && index <= v->arity ) {
    v->elements[index-1] = value;
  }
  else {
    SARL_REPORT_ERROR("index out of bounds.");
  };
};


Sarl_Index sarl_vector_get(
  Sarl_Vector *v, Sarl_Index index
)
{
  if ( index > 0 && index <= v->arity ) {
    return v->elements[index-1];
  }
  else {
    SARL_REPORT_ERROR("index out of bounds");
    return 0;
  };
};

Sarl_Index sarl_vector_arity(Sarl_Vector *v)
{
  return v->arity;
};

/* reference counting interface */
void sarl_vector_decr_ref(Sarl_Vector *v)
{
  if ( sarl_ref_count_decr(&v->ref_count) ) {
    delete v;
  };
};
void sarl_vector_incr_ref(Sarl_Vector *v)
{
  sarl_ref_count_incr(&v->ref_count);
};

  

