deferred class SARL_ITERATOR

inherit
	SARL_OBJECT

feature

	reset is
		deferred
		end

	next is
		deferred
		end

	next_gte(value: SARL_INDEX) is
		deferred
		end

	at_end: BOOLEAN is
		deferred
		end
	
end
