extern "C" {
  #include <sarl/config.h>
  #include <sarl/input_stream.h>
  #include <sarl/string.h>
}

#include <sarl/iterator_impl.h>
#include <iostream>
#include <fstream>
#include <sstream>

struct Sarl_InputStream : Sarl_Iterator
{
  std::istream* input;
};

using namespace std;

int 
sarl_input_stream_get_line(Sarl_InputStream* input, Sarl_String *s)
{
  static char buf[4096];
  
  if ( input && input->input->good() ) {
    if ( input->input->getline(buf, sizeof(buf)), input->input->fail() ) {
      int   length = sizeof(buf)*2;
      char *expanding_buf = new char[sizeof(buf)*2];
      input->input->clear(ios::failbit);
      while ( 
        input->input->good() && 
        (input->input->getline(expanding_buf, length), input->input->fail()) 
      ) {
        delete[] expanding_buf;
        length *= 2;
        expanding_buf = new char[length];
        input->input->clear(ios::failbit);
      };
      if ( ! input->input->good() ) {
        return SARL_ERROR;
      }
      else {
        sarl_string_set_chars(s, expanding_buf, strlen(expanding_buf));
      }
    }
    else {
      sarl_string_set_chars(s, buf, strlen(buf));
    }
  }
  else {
    return SARL_ERROR;
  }

  return SARL_OK;
};


struct Sarl_InputStream* 
sarl_input_stream_create_from_file(FILE *file)
{
  //  Sarl_InputStream *result = new Sarl_InputStream();
  //  result->input = new ifstream(file);
  SARL_NOT_IMPLEMENTED;
  return SARL_ERROR;
};


struct Sarl_InputStream* 
sarl_input_stream_create_from_file_name(Sarl_String *file_name)
{
  Sarl_InputStream *result = new Sarl_InputStream();
  result->input = new ifstream(sarl_string_get_chars(file_name));
  return result;
};


struct Sarl_InputStream*
sarl_input_stream_create_from_file_string(Sarl_String *s)

{
  Sarl_InputStream *result = new Sarl_InputStream();
  result->input = new istringstream(string(sarl_string_get_chars(s)));
  return result;
};

