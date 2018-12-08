package common

import matrix.AbstractMatrix

trait MaxProductNorm extends AbstractMatrix {
   abstract override def norm(x:Double, y:Double)= x * y
}