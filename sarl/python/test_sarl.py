from py_sarl import *

def checkSequence(seq, len):
    i=1
    it=SetIterator(a)
    while(not it.at_end()):
        if i <> it.val():
            print "Error, mismatch i=", i, ", it.val()=" + str(it.val())
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

it = intersection(SetIterator(b),SetIterator(c))


while(not it.at_end()):
    it.next()
    
if it.count_remaining() != 0:
    print "Error, it.count_remaining() returned ", it.count_remaining()
    
if SetIterator(b).count_remaining() != len/3:
    print "Error, SetIterator(b).count_remaining() returned ", \
          SetIterator(b).count_remaining()

if it.count() != len/6:
   print "Error, it.count() returned ", it.count()

x = union(SetIterator(a),SetIterator(b))
y = intersection(SetIterator(a),SetIterator(b))
z = set_minus(SetIterator(a),SetIterator(b))

if subset(x, y):
    print "Error subset(x,y) returned true"

if not subset(y, x):
    print "Error subset(y,x) returned false"

if not subset(y, x):
    print "Error subset(y,x) returned false"

if lexical_compare(z, SetIterator(a)) <= 0:
    print "Error lexical_compare(z,SetIterator(a))"

if lexical_compare(z.copy(), SetIterator(b)) >= 0:
    print "Error lexical_compare(z,SetIterator(b))"

if lexical_compare(z, y) >= 0:
    print "Error lexical_compare(z,a)"
    
print "Test Finished"
