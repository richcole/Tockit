#include "Set.h"
#include "SetIterator.h"

Set::Set(SetIterator const& it) {
	mp_setRef = sarl_set_copy(it.mp_itRef);
}

Set Set::copy() {
	return Set(SetIterator(*this));
}
