#ifndef SARL_CPP_REF_COUNT_H
#define SARL_CPP_REF_COUNT_H

#include <iostream>

class RefCount;
class RefCountPtrBase;
template<class T> class RefCountPtr;
class GlobalRefCounter;

class GlobalRefCounter
{
public:
	void reg_object(RefCount *object);
	void unreg_object(RefCount *object);
	~GlobalRefCounter();
};

/** 
 * class RefCount 
 * 
 *  Please note that this reference counting class is not thread safe.
 */

class RefCount {

	friend RefCountPtrBase;

protected:
	RefCount() 
		: m_count(1)
	{
		ms_refCounter.reg_object(this);
	}

	~RefCount()
	{
		if ( m_count != 0 ) {
			std::cerr << "Error, object deleted with refCount=";
			std::cerr << m_count << std::endl;
		};
		ms_refCounter.unreg_object(this);
	}

public:
	bool decr_ref() 
	{
		return --m_count == 0;
	}

	void incr_ref()
	{
		++m_count;
	}
		
	unsigned int m_count;

	static GlobalRefCounter ms_refCounter;
};

/** 
 * class RefCountPtrBase
 * 
 *  A base class implementing functionality for reference counting.
 */

class RefCountPtrBase
{
public:
	RefCountPtrBase(RefCount *ap_ptr = 0) 
	{
		mp_ptr = ap_ptr;
	}

	RefCountPtrBase(RefCountPtrBase const& p) 
	{
		mp_ptr = p.mp_ptr;
		if ( mp_ptr != 0 ) {
			mp_ptr->incr_ref();
		}
	}

	RefCountPtrBase& operator=(RefCountPtrBase const& p)
	{
		if ( mp_ptr == p.mp_ptr ) return *this;

		if ( mp_ptr != 0 ) {
			if ( mp_ptr->decr_ref() ) {
				delete mp_ptr;
			}
		}
		mp_ptr = p.mp_ptr;
		if ( mp_ptr != 0 ) {
			mp_ptr->incr_ref();
		}

		return *this;
	}
	
	virtual ~RefCountPtrBase()
	{
		if ( mp_ptr && mp_ptr->decr_ref() ) {
			delete mp_ptr;
		}
	}

protected:
	RefCount* mp_ptr;
};

template<class T>
class RefCountPtr : public RefCountPtrBase
{
public:
	RefCountPtr(RefCountPtr<T> const& ap_ptr) 
		: RefCountPtrBase(ap_ptr)
	{
	}

	RefCountPtr<T>& operator=(RefCountPtr<T> const& t)
	{
		RefCountPtrBase::operator=(t);
		return *this;
	}

	RefCountPtr<T>& operator=(T *ptr) 
	{
		RefCountPtrBase::operator=(ptr);
		return *this;
	}
	
	RefCountPtr(T *ptr)
		: RefCountPtrBase(ptr)
	{
	}

	RefCountPtr()
		: RefCountPtrBase(0)
	{
	};

	bool operator==(T *ap_ptr) const
	{
		return mp_ptr == ap_ptr;
	};

	bool operator==(RefCountPtr<T> const& ap_ptr) const
	{
		return mp_ptr == ap_ptr->mp_ptr;
	};

	bool operator!=(T *ap_ptr) const
	{
		return mp_ptr != ap_ptr;
	};

	bool operator!=(RefCountPtr<T> const& ap_ptr) const
	{
		return mp_ptr != ap_ptr->mp_ptr;
	};

	bool operator<(T *ap_ptr) const
	{
		return mp_ptr < ap_ptr;
	};

	bool operator<(RefCountPtr<T> const& ap_ptr) const
	{
		return mp_ptr < ap_ptr.mp_ptr;
	};

	T& operator*() const 
	{
		return *static_cast<T*>(mp_ptr);
	}
	
	T* operator->() const
	{
		return static_cast<T*>(mp_ptr);
	}
};

template<class T>
class ThisVar : public RefCountPtr<T>
{
public:
	ThisVar(T *ap_this) 
		: RefCountPtr<T>(ap_this)
	{
		if ( ap_this != 0 ) {
			ap_this->incr_ref();
		}
	}
};


#define SARL_DECL_VAR(x) typedef RefCountPtr<x> x##_var;


#endif
