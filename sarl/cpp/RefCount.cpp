#include <sarl/cpp/RefCount.h>

GlobalRefCounter RefCount::ms_refCounter;

unsigned int s_globalRefCount;

void GlobalRefCounter::reg_object(RefCount *object)
{
	++s_globalRefCount;
};

void GlobalRefCounter::unreg_object(RefCount *object)
{
	if ( s_globalRefCount == 0 ) {
		std::cerr << __FILE__ << "(" << __LINE__ << "):" << endl;
		std::cerr << "  Error, object deleted with globalRefCount=";
		std::cerr << s_globalRefCount << std::endl;
	}
	else {
		--s_globalRefCount;
	}
};

GlobalRefCounter::~GlobalRefCounter()
{
	if ( s_globalRefCount ) {
		std::cerr << __FILE__ << "(" << __LINE__ << "):" << endl;
		std::cerr << "  Error, s_globalRefCount=" << endl;
		std::cerr << s_globalRefCount << std::endl;
	};
};


