extern "C" {
  #include <sarl/global.h>
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
sarl_input_stream_read_line(Sarl_InputStream* input, Sarl_String *s)
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

Sarl_Char
sarl_input_stream_char_value(Sarl_InputStream* input)
{
  return input->input->peek();
};


Sarl_Short
sarl_input_stream_short_value(Sarl_InputStream* input, Sarl_Index* index_ptr)
{
  return input->input->peek();
};


void
sarl_input_stream_next(Sarl_InputStream* input)
{
  Sarl_Char c;
  input->input->get(c);
};


bool
sarl_input_stream_at_end(Sarl_InputStream* input)
{
  return ! input->input->good();
};

int
sarl_input_stream_read_index(Sarl_InputStream* input, Sarl_Index* index_ptr)
{
  *index_ptr = 0;
  SARL_INPUT_STREAM_FOR(input) {
    Sarl_Char c = sarl_input_stream_char_value(input);
    if ( c >= '0' && c <= '9' ) {
      *index_ptr *= 10;
      *index_ptr += c - '0';
    }
    else {
      return 1;
    }
  }
  return 1;
};


struct Sarl_InputStream* 
sarl_input_stream_create_from_file(FILE *file)
{
  //  Sarl_InputStream *result = new Sarl_InputStream();
  // sarl_iterator_init(result);
  //  result->input = new ifstream(file);
  SARL_NOT_IMPLEMENTED;
  return SARL_ERROR;
};


struct Sarl_InputStream* 
sarl_input_stream_create_from_file_name(Sarl_String *file_name)
{
  Sarl_InputStream *result = new Sarl_InputStream();
  sarl_iterator_init(result);
  result->input = new ifstream(sarl_string_get_chars(file_name));
  return result;
};


struct Sarl_InputStream*
sarl_input_stream_create_from_string(Sarl_String *s)

{
  Sarl_InputStream *result = new Sarl_InputStream();
  sarl_iterator_init(result);
  result->input = new istringstream(string(sarl_string_get_chars(s)));
  return result;
};

void
sarl_input_stream_decr_ref(Sarl_InputStream *input)
{
  if ( sarl_ref_count_decr(&input->ref_count) ) {
    delete input->input;
    delete input;
  }
};

void
sarl_input_stream_incr_ref(Sarl_InputStream *input)
{
  sarl_ref_count_incr(&input->ref_count);
};

