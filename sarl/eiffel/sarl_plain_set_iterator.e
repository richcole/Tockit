class SARL_PLAIN_SET_ITERATOR is

inherit
	SARL_SET_ITERATOR

creation
	make

feature

	make(x: SARL_PLAIN_SET) is
		do
			set := x;
			it := set.collection.iterator;
			reset;
		end

	reset is
		do
			it.start;
		end

	next is
		do
			it.next;
		end

	next_gte(value: SARL_INDEX) is
		deferred
		end

	at_end: BOOLEAN is
		deferred
		end

	value: SARL_INDEX is
		deferred
		end

feature {NONE}

	set: SARL_PLAIN_SET;
	it:  like set.collection.iterator;
	
end

			
