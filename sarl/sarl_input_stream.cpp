"C" {
  #include <sarl/input_stream.h>
}

#include <sarl/iterator_impl.h>
#include <iostream>
#include <fstream>
#include <sstream>

struct Sarl_InputStream : Sarl_Iterator
{
  std::ifstream* input;
};

using namespace std;


int 
sarl_input_stream_get_line(Sarl_InputStream* input, Sarl_String *s)
{
  static char buf[4096];
  
  if ( input && input->input->good() ) {
    if ( input->getline(buf, sizeof(buf)), input->input->fail() ) {
      int   length = sizeof(buf)*2;
      char *expanding_buf = new char[sizeof(buf)*2];
      intput->input->clear(ios::fail_bit);
      while ( 
        input->input->good() && 
        (input->input->get_line(expanding_buf, length), input->input->fail()) 
      ) {
        delete[] expanding_buf;
        length *= 2;
        expanding_buf = new char[expanding_buf*2];
        intput->input->clear(ios::fail_bit);
      };
      if ( ! input->input->good() ) {
        return -1;
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
    return -1;
  }
};


struct Sarl_InputStream* 
sarl_input_stream_create_from_file(FILE *file)
{
  Sarl_InputStream *result = new Sarl_InputStream();
  result->input = new ifstream(file);
};


struct Sarl_InputStream* 
sarl_input_stream_create_from_file_name(Sarl_String *file_name)
{
  Sarl_InputStream *result = new Sarl_InputStream();
  result->input = new ifstream(sarl_string_get_chars());
};


struct Sarl_InputStream*
sarl_input_stream_create_from_file_string(Sarl_String *s)

{
  Sarl_InputStream *result = new Sarl_InputStream();
  result->input = new isstream(string(sarl_string_get_chars()));
};

