extern "C" {
  #include <sarl/global.h>
  #include <sarl/output_stream.h>
  #include <sarl/string.h>
}

#include <sarl/output_stream_impl.h>

#include <sarl/iterator_impl.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>

using namespace std;

static void 
  sarl_output_stream_string_decr_ref(struct Sarl_OutputStream *a_it);

static void 
  sarl_output_stream_file_decr_ref(struct Sarl_OutputStream *a_it);

static int 
  sarl_output_stream_file_write_line(
    Sarl_OutputStream* a_output, Sarl_String *s
  );

static int 
  sarl_output_stream_string_write_line(
    Sarl_OutputStream* a_output, Sarl_String *s
  );

static int 
  sarl_output_stream_string_write_char(
    Sarl_OutputStream* a_output, Sarl_Char t
  );

static int 
  sarl_output_stream_file_write_char(
    Sarl_OutputStream* a_output, Sarl_Char t
  );

struct Sarl_OutputStreamFunctionTable s_outputStreamFileTable = 
{
  sarl_output_stream_file_write_line,
  sarl_output_stream_file_write_char,
  sarl_output_stream_file_decr_ref
};

struct Sarl_OutputStreamFunctionTable s_outputStreamStringTable = 
{
  sarl_output_stream_string_write_line,
  sarl_output_stream_string_write_char,
  sarl_output_stream_string_decr_ref
};

/* reference counting interface */
void sarl_output_stream_string_decr_ref(struct Sarl_OutputStream *a_output)
{
  Sarl_OutputStreamString* output = 
    static_cast<Sarl_OutputStreamString*>(a_output);

  if ( sarl_ref_count_decr(&output->ref_count) ) {
    sarl_string_decr_ref(output->s);
    delete output;
  }
}

void sarl_output_stream_file_decr_ref(struct Sarl_OutputStream *a_output)
{
  Sarl_OutputStreamFile* output = 
    static_cast<Sarl_OutputStreamFile*>(a_output);

  if ( sarl_ref_count_decr(&output->ref_count) ) {
    delete output->output;
    delete output;
  }
}

static int 
  sarl_output_stream_file_write_char(
    Sarl_OutputStream* a_output, Sarl_Char t
  )
{
  Sarl_OutputStreamFile* output = 
    static_cast<Sarl_OutputStreamFile*>(a_output);

  output->output->put(t);
  return output->output->fail() ? SARL_ERROR : SARL_OK;
};


static int 
  sarl_output_stream_string_write_char(
    Sarl_OutputStream* a_output, Sarl_Char t
  )
{
  Sarl_OutputStreamString* output = 
    static_cast<Sarl_OutputStreamString*>(a_output);

  sarl_string_append_char(output->s, t);
  return SARL_OK;
};


int 
sarl_output_stream_file_write_line(Sarl_OutputStream* a_output, Sarl_String *s)
{
  Sarl_OutputStreamFile* output = 
    static_cast<Sarl_OutputStreamFile*>(a_output);

  output->output->write(sarl_string_get_chars(s), sarl_string_length(s));
  (*(output->output)) << endl;
  return output->output->fail() ? SARL_ERROR : SARL_OK;
};

int 
sarl_output_stream_string_write_line(Sarl_OutputStream* a_output, Sarl_String *s)
{
  Sarl_OutputStreamString* output = 
    static_cast<Sarl_OutputStreamString*>(a_output);

  sarl_string_append(output->s, s);
  sarl_string_append_chars(output->s, "\n");
  return SARL_OK;
};


struct Sarl_OutputStream* 
sarl_output_stream_create_from_file(FILE *file)
{
  //  Sarl_OutputStream *result = new Sarl_OutputStream();
  //  result->output = new ifstream(file);
  SARL_NOT_IMPLEMENTED;
  return SARL_ERROR;
};


struct Sarl_OutputStream* 
sarl_output_stream_create_from_file_name(Sarl_String *file_name)
{
  Sarl_OutputStreamFile *result = new Sarl_OutputStreamFile();
  sarl_output_stream_init(result, &s_outputStreamFileTable);
  result->output = new ofstream(sarl_string_get_chars(file_name));
  return result;
};


struct Sarl_OutputStream*
sarl_output_stream_create_from_string(Sarl_String *s)

{
  Sarl_OutputStreamString *result = new Sarl_OutputStreamString();
  sarl_output_stream_init(result, &s_outputStreamStringTable);
  result->s = s;
  sarl_string_incr_ref(s);
  return result;
};

int sarl_output_stream_write_line(
  struct Sarl_OutputStream *output, Sarl_String* s)
{
  return output->funcs->write_line(output, s);
}

void sarl_output_stream_decr_ref(struct Sarl_OutputStream *output)
{
  output->funcs->decr_ref(output);
}

int sarl_output_stream_write_char(
  Sarl_OutputStream* output, Sarl_Char ch)
{
  return output->funcs->write_char(output, ch);
};

int sarl_output_stream_write_index(
  Sarl_OutputStream* output, Sarl_Index index)
{
  int result = SARL_OK;

  std::vector<char> v;

  if ( index == 0 ) {
    v.push_back('0');
  };
  
  while ( index != 0 ) {
    v.push_back('0' + (index % 10));
    index /= 10;
  }

  while ( v.size() != 0 && result != SARL_ERROR ) {
    result = sarl_output_stream_write_char(output, v.back());
    v.pop_back();
  }

  return result;

};

void sarl_output_stream_incr_ref(Sarl_OutputStream* output) 
{
  sarl_ref_count_incr(&output->ref_count);
};

  




