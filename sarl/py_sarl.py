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
    def __init__(self, set):
        self.iterator = sarl_set_iterator_create(set.set)

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



