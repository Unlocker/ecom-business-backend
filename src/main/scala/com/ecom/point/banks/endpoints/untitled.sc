def x (f:(Int, Int) => Int *) = ???
val sum : (Int, Int) => Int = (i, h) => i + h
x(, sum, sum)