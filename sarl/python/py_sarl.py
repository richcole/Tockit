from sarl import *

class Set:
    def __init__(self):
        self.set = sarl_set_create()

    def __del__(self):
        sarl_set_decr_ref(self.set)

    def insert(self, i):
        sarl_set_insert(self.set, i)

    def remove(self, i):
        sarl_set_remove(self.set, i)

    def clone(self, s):
        return sarl_set_clone(SetIterator(self.set))

class SetIterator:
    def __init__(self, arg):
        if isinstance(arg, Set):
            self.iterator = sarl_set_iterator_create(arg.set)
        else:
            self.iterator = arg

    def __del__(self):
        sarl_set_iterator_decr_ref(self.iterator)

    def reset(self):
        sarl_set_iterator_reset(self.iterator)

    def at_end(self):
        return sarl_set_iterator_at_end(self.iterator)

    def next(self):
        sarl_set_iterator_next(self.iterator)

    def next_gte(self, i):
        sarl_set_iterator_next(self.iterator, i)

    def val(self):
        return sarl_set_iterator_val(self.iterator)

    def count(self):
        return sarl_set_iterator_count(self.iterator)

    def count_remaining(self):
        return sarl_set_iterator_count_remaining(self.iterator)

    def copy(self):
        it = sarl_set_iterator_copy(self.iterator)
        return SetIterator(it)

def intersection(first, second):
    it = sarl_set_iterator_meet(first.iterator, second.iterator)
    return SetIterator(it)

def union(first, second):
    it = sarl_set_iterator_union(first.iterator, second.iterator)
    return SetIterator(it)

def set_minus(first, second):
    it = sarl_set_iterator_minus(first.iterator, second.iterator)
    return SetIterator(it)

def lexical_compare(first, second):
    return sarl_set_iterator_lexical_compare(first.iterator, second.iterator)

def subset(first, second):
    return sarl_set_iterator_subset(first.iterator, second.iterator)



