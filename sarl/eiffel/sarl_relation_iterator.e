deferred class SARL_RELATION_ITERATOR

inherit
	SARL_ITERATOR

feature

	value: SARL_PAIR is
		deferred
		end

	meet, infix "&" (x: SARL_RELATION_ITERATOR): SARL_RELATION_ITERATOR is
		do
			create {SARL_MEET_RELATION_ITERATOR} Result.make(Current, x);
		end

	join, infix "|" (x: SARL_RELATION_ITERATOR): SARL_RELATION_ITERATOR is
		do
			create {SARL_MEET_RELATION_ITERATOR} Result.make(Current, x);
		end

	minus, infix "-" (x: SARL_RELATION_ITERATOR): SARL_RELATION_ITERATOR is
		do
			create {SARL_MINUS_RELATION_ITERATOR} Result.make(Current, x);
		end			

end


		

