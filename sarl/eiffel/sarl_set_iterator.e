deferred class SARL_SET_ITERATOR

inherit
	SARL_ITERATOR

feature

	value: SARL_INDEX is
		deferred
		end

	meet, infix "&" (x: SARL_SET_ITERATOR): SARL_SET_ITERATOR is
		do
			create {SARL_MEET_SET_ITERATOR} Result.make(Current, x);
		end

	join, infix "|" (x: SARL_SET_ITERATOR): SARL_SET_ITERATOR is
		do
			create {SARL_MEET_SET_ITERATOR} Result.make(Current, x);
		end

	minus, infix "-" (x: SARL_SET_ITERATOR): SARL_SET_ITERATOR is
		do
			create {SARL_MINUS_SET_ITERATOR} Result.make(Current, x);
		end			

end
