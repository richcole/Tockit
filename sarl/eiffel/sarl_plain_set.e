class SARL_PLAIN_SET is

inherit
	SARL_SET

creation
	make

feature

	make is
		do
			create collection;
		end

	iterator: SARL_SET_ITERATOR is
		do
			create {SARL_PLAIN_SET_ITERATOR} Result.make(Self);
		end

	insert(i: SARL_INDEX) is
		do
			collection.insert(i);
		end
	
	remove(i: SARL_INDEX) is
		do
			collection.erase(i);
		end

feature { SARL_PLAIN_SET_ITERATOR }

	collection: COLLECTION[INTEGER_32]

end



	
