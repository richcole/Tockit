expanded class SARL_PAIR

creation
	make(f, s: SARL_INDEX) is
		do
			first := f;
			second := s;
		end

feature

	first: SARL_INDEX;
	second: SARL_INDEX;
end
