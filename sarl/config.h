#ifndef SARL_CONFIG_H
#define SARL_CONFIG_H

#define SARL_NOT_IMPLEMENTED \
  { fprintf(stderr, "%s:%d: Error, Function not yet implemented.\n", \
          __FILE__, __LINE__); };

#define SARL_ERROR 0
#define SARL_OK    1

#endif
