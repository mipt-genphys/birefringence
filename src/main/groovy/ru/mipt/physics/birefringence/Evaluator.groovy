package ru.mipt.physics.birefringence

import static java.lang.Math.*;

/**
 * Created by darksnake on 18-May-16.
 */
class Evaluator {

    private Vector nVector(Vector phi1, Vector phi2, double a) {
        return (phi1.sin()**2 + phi2.sin()**2 + phi1.sin() * phi2.sin() * cos(a) * 2).sqrt() / sin(a);
    }

    private Vector costhVector(Vector phi1, Vector n) {
        return phi1.sin() / n;
    }

    private Vector sigmanVector(Vector phi1, Vector phi2, Vector n, double a, double aErr, double phi1Err, double psiErr) {
        double[] res = new double[phi1.size()];
        for (int i = 0; i < phi1.size(); i++) {
            def nal = (0.5 * sin(2 * phi1[i]) - 0.5 * sin(2 * phi2[i]) -
                    cos(a) * sin(phi1[i] - phi2[i])) / (2 * (n[i] * sin(a)**2));
            def nb = -(sin(phi2[i]) * cos(phi2[i]) +
                    cos(a) * sin(phi1[i]) * cos(phi2[i])) / (n[i] * sin(a)**2);
            def na = -(n[i] * cos(a)) / sin(a) + (0.5 * sin(2 * phi2[i]) +
                    sin(phi1[i]) * cos(a + phi2[i])) / (n[i] * sin(a)**2)
            res[i] = sqrt((2 * phi1Err * nal)**2 + (nb * psiErr)**2 + (na * aErr)**2)
        }
        return new Vector(res);
    }

    /**
     * Adjustment check
     * @param data
     * @return
     */
    def checkAdjustment(Data data) {

        Vector nVector = nVector(data.phi1, data.phi2o(), data.a);
        Vector costhVector = costhVector(data.phi1, nVector);
        Vector sigmanVector = sigmanVector(data.phi1, data.phi2o(), nVector, data.a, data.aErr, data.phi1Err, data.psioErr);

//        def chi2Func = { Double k, Double k0 ->
//            (((nVector - costhVector**2 * k - k0) / sigmanVector)**2)// chi2 expression
//                    .values() // as double array
//                    .sum(); // sum of the array
//        }


        double chi2Min = Double.MAX_VALUE;
        double minK = 0;
        double minK0 = 0;

        // Direct 2-parameter search
        for (double k0 = 0; k0 < 3; k0 += 0.01) {
            for (double k = -0.2; k < 0.2; k += 0.01) {
                double chi2 = (((nVector - costhVector**2 * k - k0) / sigmanVector)**2)// chi2 expression
                        .values() // as double array
                        .sum(); // sum of the array
                if (chi2 < chi2Min) {
                    chi2Min = chi2;
                    minK = k;
                    minK0 = k0
                }
            }
        }

        if (minK >= 0.02) {
            println "incorrect adjustment";
        }
        //TODO make picture here

        return new Tuple2<>(minK, minK0);
    }

    /**
     * calculating no
     * @param data
     */
    def calculateno(Data data) {

        Vector nVector = nVector(data.phi1, data.phi2o(), data.a);
        Vector sigmanVector = sigmanVector(data.phi1, data.phi2o(), nVector, data.a, data.aErr, data.phi1Err, data.psioErr);

        Vector weights = new Vector(sigmanVector.values().collect { 1 / it**2 })

        double sum = (nVector * weights).values().sum()
        double weight = weights.values().sum()
        def no = sum / weight
        def noErr = sqrt(1 / weight)
        return new Tuple2<>(no, noErr)
    }

    def calculatene(Data data) {
        Vector nVector = nVector(data.phi1, data.phi2e(), data.a);
        Vector sigmanVector = sigmanVector(data.phi1, data.phi2e(), nVector, data.a, data.aErr, data.phi1Err, data.psieErr);

        double[][] m = new double[2][2];//initialized with zeroes
//        m[0][0] = 0
//        m[0][1] = 0
//        m[1][0] = 0
//        m[1][1] = 0
        double b0 = 0;
        double b1 = 0;
        for (int i = 0; i < data.size(); i++) {
            def d1 = nVector[i]**6 / sigmanVector[i]**4 / 4d;
            def d2 = (sin(data.phi1[i]) / nVector[i])**2
            m[0][0] += d1
            m[0][1] += d1 * d2
            m[1][1] += d2 * d2 * d1;
            b0 += d1 / nVector[i]**2;
            b1 += d1 * d2 / nVector[i]**2;
        }
        m[1][0] = m[0][1]
        def det = m[0][0] * m[1][1] - m[1][0] * m[0][1];
        double[][] mInv = new double[2][2];
        mInv[0][0] = m[1][1] / det;
        mInv[0][1] = -m[1][0] / det;
        mInv[1][0] = -m[0][1] / det;
        mInv[1][1] = m[0][0] / det;
        def ne = 1 / sqrt(mInv[0][0] * b0 + mInv[0][1] * b1);
        def no = 1 / sqrt(mInv[1][0] * b0 + mInv[1][1] * b1 + ne**(-2));
        def sigmane = sqrt(mInv[0][0]) * 0.5 * (mInv[0][0] * b0 + mInv[0][1] * b1)**(-3 / 2)
        return new Tuple(no, ne, sigmane);
    }

    /**
     * The angle of least deviation
     * @param psiomin
     * @param psiemin
     * @param a
     * @return
     */
    def leastDev(double psiomin, double psiemin, double a) {
        def no = sin((psiomin + a) / 2) / sin(a / 2)
        def ne = sin((psiemin + a) / 2) / sin(a / 2)
        return new Tuple2<>(no, ne);
    }

    /**
     * Total reflection angle
     * @param phi1o
     * @param phi1e
     * @param a
     */
    def totalRef(double phi1o, double phi1e, double a) {
        def no = sqrt((sin(phi1o))**2 + 1 + (2 * sin(phi1o) * cos(a))) / sin(a);
        def ne = sqrt((sin(phi1e))**2 + 1 + (2 * sin(phi1e) * cos(a))) / sin(a);
        return new Tuple2<>(no, ne);
    }
}
