from py_sarl import * 

a = Set()

for i in range(1,10):
    a.insert(i)

it=SetIterator(a)
while(not it.at_end()):
    print it.val()
    it.next()

it.reset()
while(not it.at_end()):
    print it.val()
    it.next()

