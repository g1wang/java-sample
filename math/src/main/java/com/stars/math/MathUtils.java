package com.stars.math;

import org.jblas.DoubleMatrix;

public class MathUtils {

    /**
     * cosin distance / Euclidean distance
     * @param ds1
     * @param ds2
     * @return
     */
    public static double cosDistance(double[] ds1, double[] ds2) {
        DoubleMatrix doubleMatrix1 = new DoubleMatrix(ds1);
        DoubleMatrix doubleMatrix2 = new DoubleMatrix(ds2);
        return doubleMatrix1.dot(doubleMatrix2) / (doubleMatrix1.norm2() * doubleMatrix2.norm2());
    }

}
