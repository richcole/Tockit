#ifndef SARL_CONFIG_H
#define SARL_CONFIG_H

#define SARL_NOT_IMPLEMENTED \
  { fprintf(stderr, "%s:%d: Error, Function not yet implemented.\n", \
          __FILE__, __LINE__); };

#define SARL_FATAL_ERROR(x) \
  { fprintf(stderr, "%s:%d: Fatal Error: %s.\n", \
          __FILE__, __LINE__, (x)); };

#define SARL_REPORT_ERROR(x) \
  { fprintf(stderr, "%s:%d: Error: %s.\n", \
          __FILE__, __LINE__, (x)); };

#include <limits.h>

#define SARL_INDEX_MIN_VALUE INT_MIN
#define SARL_INDEX_MAX_VALUE INT_MAX

#define SARL_ERROR 0
#define SARL_OK    1

#define SARL_FALSE 0
#define SARL_TRUE  1

#endif
