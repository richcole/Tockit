#include <string.h>

// ----------------------------------------------------------------------------
// Args                                                            Richard Cole
// 
// Provides argument parsing functions
// ----------------------------------------------------------------------------

char *find_args(int n_args, char **args, char *arg) 
// find the argument that follows the argumnet arg
{
    int i;
    for(i=0;i<n_args-1;i++) {
	if (strcmp(args[i], arg) == 0)
	    return args[i+1];
    };

    return 0;
};
