class SARL_OBJECT

creation
	make
	
feature

	make is
		do
			is_owned := true;
		end

	obtain_ownership: like Current is
		do
			if is_owned then
				Result := Current.copy;
			else
				Result := Current;
			end
		end

	release_ownership: like Current is
		require
			not is_owned;
		do
			is_owned := false;
		end

	is_owned: boolean
			
end
