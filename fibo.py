# Fibonacci numbers module
import math

class mia:
    
    def fib(self, n):    # write Fibonacci series up to n
        a, b = 0, 1
        c=1;
        while b < n:
            print b/c,
            c = 1.0 + b -1.0
            a, b = b, a+b
    
    def fib2(n): # return Fibonacci series up to n
        result = []
        a, b = 0, 1
        while b < n:
            result.append(b)
            a, b = b, a+b
        return result
    
    result = set([""])
    
    def square2(self, un , du):
        p = un*un
        s = du*du
        t = math.sqrt(p+s)
        f = math.floor(t);
        if f == t:
            tripla = ""+str(un)+"_"+str(du)+"_"+str(int(t))
            if du < un:
                tripla = ""+str(du)+"_"+str(un)+"_"+str(int(t))
            self.result.add(tripla)

    def square3(self, un , du, tri):
        p = un*un
        s = du*du
        r = tri*tri
        t = math.sqrt(p+s+r)
        f = math.floor(t);
#        print t, f
        if f == t:
#            print t, f,un,du,tri
            tripla = "";
            f= math.trunc(f)
            if un < du:
                tripla = ""+str(un)+"_"+str(du)+"_"+str(tri)+"_"+str(f)
            elif du < un:
                tripla = ""+str(du)+"_"+str(un)+"_"+str(tri)+"_"+str(f)
            self.result.add(tripla)

    def fa(self):
        for x in range(1, 20):
            for y in range(1, 20):
                for z in range(1, 20):
                    self.square3(x,y,z)
        print self.result
