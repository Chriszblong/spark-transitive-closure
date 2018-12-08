package common

import matrix.AbstractMatrix

trait MaxDeltaNorm extends AbstractMatrix {
   abstract override def norm(x:Double, y:Double)= (x+y-1) max .0
}