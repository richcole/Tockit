from py_sarl import *

a = Set()
b = Set()
c = Set()

for i in range(1,10):
    a.insert(i)

x = SetIterator(a)
y = intersection(SetIterator(b),SetIterator(c))
z1 = union(SetIterator(b),SetIterator(c))
z2 = set_minus(SetIterator(b),SetIterator(c))

for i in range(1,10):
    if i % 3 == 0:
        b.insert(i)
    if i % 2 == 0:
        c.insert(i)

y.reset()
y.count_remaining()

while(not x.at_end()):
    x.next()
    
if x.copy().count_remaining() != 0:
    print "Error, it.count_remaining() returned ", it.count_remaining()

x.count_remaining()

print subset(x,x.copy())
print x.count()
print x.count_remaining()
print lexical_compare(x.copy(),x.copy())
                      


