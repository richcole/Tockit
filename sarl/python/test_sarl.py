from sarl import *

def checkSequence(seq, len):
    i=1
    it=SetIterator(a)
    while(not it.at_end()):
        if i <> it.value():
            print "Error, mismatch i=", i, ", it.value()=" + str(it.value())
        it.next()
        i = i + 1
    if i <> len:
        print "Error, mistmatch expected i=", len, ", but got i=", i

a = Set()
len=20

for i in range(1,len):
    a.insert(i)

it = SetIterator(a)
checkSequence(it, len)
it.reset()
checkSequence(it, len)

b = Set()
c = Set()

for i in range(1,len):
    if i % 3 == 0:
        b.insert(i)
    if i % 2 == 0:
        c.insert(i)

b_it = SetIterator(b)
c_it = SetIterator(c)
it = b_it.iterator_meet(c_it)

while(not it.at_end()):
    it.next()
    
if it.count_remaining() != 0:
    print "Error, it.count_remaining() returned ", it.count_remaining()
    
if SetIterator(b).count_remaining() != len/3:
    print "Error, SetIterator(b).count_remaining() returned ", \
          SetIterator(b).count_remaining()

if it.count() != len/6:
   print "Error, it.count() returned ", it.count()

x = SetIterator(a).iterator_union(SetIterator(b))
y = SetIterator(a).iterator_meet(SetIterator(b))
z = SetIterator(a).iterator_minus(SetIterator(b))

if x.subset(y):
    print "Error subset(x,y) returned true"

if not y.subset(x):
    print "Error subset(y,x) returned false"

if not y.subset(x):
    print "Error subset(y,x) returned false"

if z.lexical_compare(SetIterator(a)) <= 0:
    print "Error iterator_lexical_compare(z,SetIterator(a))"

if z.copy().lexical_compare(SetIterator(b)) >= 0:
    print "Error iterator_lexical_compare(z.copy(),SetIterator(b))"

if z.lexical_compare(y) >= 0:
    print "Error iterator_lexical_compare(z,a)"
    
print "Test Finished"

