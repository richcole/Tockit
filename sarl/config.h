#ifndef SARL_CONFIG_H
#define SARL_CONFIG_H

#define SARL_NOT_IMPLEMENTED \
  { fprintf(stderr, "%s:%d: Error, Function not yet implemented.\n", \
          __FILE__, __LINE__); };

#define SARL_FATAL_ERROR(x) \
  { fprintf(stderr, "%s:%d: Fatal Error: %s.\n", \
          __FILE__, __LINE__, (x)); };

#define SARL_ERROR 0
#define SARL_OK    1

#endif
