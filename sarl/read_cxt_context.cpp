extern "C" {
#include <sarl/config.h>
#include <sarl/read_cxt_context.h>
#include <sarl/context.h>
#include <sarl/dictionary.h>
}

#include <fstream>

int sarl_read_cxt_format(
  Sarl_Dictionary*  G, 
  Sarl_Dictionary*  M, 
  Sarl_Context*     I,
  Sarl_String*      title,
  Sarl_InputStream* input,
  FILE*             errors
)
{
  std::string buf;

  Sarl_Index g, m, N_g, N_m;
  Sarl_String* buf = sarl_string_create(buf);

  // read the first line
  if ( sarl_input_stream_read_line(input, buf) != SARL_ERROR ) {
    if ( strcmp("B", buf) != 0 ) {
      sarl_output_buffer
      err << "Error, file must start with a B" << endl;
      return 0;
    }
  }
  else {
    err << "Error, file must start with a B" << endl;
    return 0;
  }
    
  // read the first line
  if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
    name = buf;
  }
  else {
    err << "Error, name of file not found." << endl;
    return 0;
  }

  // read the number of objects
  if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
    istringstream line(buf);
    if ( (line >> N_g).fail() ) {
      err << "Error, couldn't read the number of objects";
      return 0;
    }
  }

  // read the number of attributes
  if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
    istringstream line(buf);
    if ( (line >> N_m).fail() ) {
      err << "Error, couldn't read the number of objects";
      return 0;
    }
  }

  // read the objects
  G.resize(N_g+1);
  for(g=1;g<=N_g;++g) {
    if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
      G[g] = buf;
    } 
    else {
      err << "Error reading object." << endl;
      return 0;
    }
    sarl_context_insert_object(K, g);
  }

  // read the attributes
  M.resize(N_m+1);
  for(m=1;m<=N_m;++m) {
    if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
      M[m] = buf;
    }
    else {
      err << "Error reading attribute." << endl;
      return 0;
    }
    sarl_context_insert_attribute(K, m);
  }

  // read the relation
  for(g=1;g<=N_g;++g) {
    if ( in.getline(buf, sizeof(buf)) && ! in.fail() ) {
      for(m=1;m<=N_m;++m) {
        if ( buf[m-1] == 'x' ) {
          sarl_context_insert(K,g,m);
        }
        else if ( buf[m-1] != '.' ) {
          err << "Error reading object intent." << endl;
          return 0;
        }
      }
    }
  }

  return 1;

};

