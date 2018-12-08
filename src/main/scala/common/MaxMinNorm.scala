package common

import matrix.AbstractMatrix

trait MaxMinNorm extends AbstractMatrix {
   abstract override def norm(x:Double, y:Double)= x min y
}