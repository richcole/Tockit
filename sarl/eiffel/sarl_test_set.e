class SARL_TEST_SET

creation

	make
	
feature

	assert( expr: BOOLEAN ) is
		do
			if not expr then
				print("Error, assertion failed.");
			end
		end
	
	make is
		local
			S: SARL_SET;
			it: SARL_SET_ITERATOR;

			i: INTEGER_32;
			length: SARL_INDEX;
		do
			create {SARL_PLAIN_SET} S.make;

			-- insert from 1 to 100 into the set
			length := 100;
			from i := 1 until i = length loop
				S.insert(i);
			end

			-- iterate over the set
			from
				it := S.iterator;
				i := 1;
			until
				it.at_end and i = length
			loop
				assert( it.value = i );
			end
		end

end
			
