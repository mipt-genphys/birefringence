package ru.mipt.physics.birefringence

import groovy.transform.CompileStatic

import static java.lang.Math.*;
import ru.mipt.physics.birefringence.Vector

/**
 * Created by darksnake on 18-May-16.
 */

class Evaluator {
    Vector nVector(Vector phi1, Vector psi, double a) {
        Vector phi2 = psi - phi1 + a;
        return (phi1.sin()**2 + phi2.sin()**2 + phi1.sin() * phi2.sin() * cos(a) * 2).sqrt() / sin(a);
    }

    double n(double phi1, double psi, double a) {
        double phi2 = psi - phi1 + a;
        return sqrt(sin(phi1)**2 + sin(phi2)**2 + sin(phi1) * sin(phi2) * cos(a) * 2) / sin(a);
    }

    Vector costhVector(Vector phi1, Vector n) {
        return phi1.sin() / n;
    }

    double costh(double phi1, double n) {
        return sin(phi1) / n;
    }

    double sigman(double phi1, double psi, double n, double a, double phi1Err, double psiErr) {
        double phi2 = psi - phi1 + a;
        def nal = (0.5 * sin(2 * phi1) - 0.5 * sin(2 * phi2) -
                cos(a) * sin(phi1 - phi2)) / (2 * (n * sin(a)**2));
        def nb = -(sin(phi2) * cos(phi2) +
                cos(a) * sin(phi1) * cos(phi2)) / (n * sin(a)**2);
//        def na = -(n * cos(a)) / sin(a) + (0.5 * sin(2 * phi2) +
//                sin(phi1) * cos(a + phi2)) / (n * sin(a)**2)
        double res = sqrt((2 * phi1Err * nal)**2 + (nb * psiErr)**2)
        return res;
    }

    Vector sigmanVector(Vector phi1, Vector psi, Vector n, double a,double phi1Err, double psiErr) {
        Vector phi2 = psi - phi1 + a;
        double[] res = new double[phi1.size()];
        for (int i = 0; i < phi1.size(); i++) {
            def nal = (0.5 * sin(2 * phi1[i]) - 0.5 * sin(2 * phi2[i]) -
                    cos(a) * sin(phi1[i] - phi2[i])) / (2 * (n[i] * sin(a)**2));
            def nb = -(sin(phi2[i]) * cos(phi2[i]) +
                    cos(a) * sin(phi1[i]) * cos(phi2[i])) / (n[i] * sin(a)**2);
//            def na = -(n[i] * cos(a)) / sin(a) + (0.5 * sin(2 * phi2[i]) +
//                    sin(phi1[i]) * cos(a + phi2[i])) / (n[i] * sin(a)**2)
            res[i] = sqrt((2 * phi1Err * nal)**2 + (nb * psiErr)**2)
        }
        return new Vector(res);
    }

//    /**
//     * Adjustment check
//     * @param data
//     * @return
//     */
//    @Deprecated
//    def checkAdjustment(Vector nVector, Vector costhVector, Vector sigmanVector) {
//
//        double chi2Min = Double.MAX_VALUE;
//        double minSlope = 0;
//        double minBase = 0;
//
//        //TODO replace by analytic solution
//        // Direct 2-parameter search
//        for (double base = 1; base < 2; base += 0.002) {
//            for (double slope = -0.2; slope < 0.2; slope += 0.002) {
//                def chi2 = (((nVector - costhVector**2 * slope - base) / sigmanVector)**2)// chi2 expression
//                        .values() // as double array
//                        .sum(); // sum of the array
//                if (chi2 < chi2Min) {
//                    chi2Min = chi2;
//                    minSlope = slope;
//                    minBase = base
//                }
//            }
//        }
//        return new Tuple<>(minBase, minSlope, chi2Min);
//    }

    /**
     * calculating no as a weighted average
     * @param data
     */
    def average(Vector nVector, Vector sigmanVector) {
        Vector weights = sigmanVector**(-2)
        // weighted average
        double sum = (nVector * weights).values().sum()
        double weight = weights.values().sum()
        def no = sum / weight
        def noErr = sqrt(1 / weight)
        return new Tuple2<>(no, noErr)
    }

    /**
     * Fit single line analytically using xs, ys and y sigmas
     * @param xVector
     * @param yVector
     * @param sigmaVector
     * @return
     */
    def fitLine(Vector xVector, Vector yVector, Vector sigmaVector) {
        double[][] m = new double[2][2];

        Vector invSigma2Vector = sigmaVector**(-2);

        double b0 = (yVector * xVector * invSigma2Vector).sum();
        double b1 = (yVector * invSigma2Vector).sum();
        m[0][0] = ((xVector**2d) * invSigma2Vector).sum();
        m[0][1] = (xVector * invSigma2Vector).sum();
        m[1][0] = m[0][1];
        m[1][1] = invSigma2Vector.sum();
        def det = m[0][0] * m[1][1] - m[1][0] * m[0][1];

        double[][] mInv = new double[2][2];//covariance matrix

        mInv[0][0] = m[1][1] / det;
        mInv[0][1] = -m[1][0] / det;
        mInv[1][0] = -m[0][1] / det;
        mInv[1][1] = m[0][0] / det;

        def slope = mInv[0][0] * b0 + mInv[0][1] * b1;
        def base = mInv[1][0] * b0 + mInv[1][1] * b1;

        double chi2 = (yVector**2d * invSigma2Vector).sum() + slope**2 * m[0][0] + base**2 * m[1][1] - 2 * slope * b0 - 2 * base * b1 + 2 * slope * base * m[0][1];
        return ["base": base, "slope": slope, "chi2": chi2, "cov": mInv];
    }

    def calculate(Vector nVector, Vector costhVector, Vector sigmanVector) {

        def fit = fitLine(costhVector**2d, nVector**(-2d), sigmanVector * 2d / (nVector**3d));

        def base = fit.base;
        def slope = fit.slope;
        def cov = fit.cov;

        def ne = 1d / sqrt(base);
        def neErr = sqrt(cov[1][1]) * 0.5 * (ne**3);

        def no = 1d / sqrt(slope + base);
        def noErr = sqrt(cov[1][1] / 4d / base + cov[0][0] / 4d / abs(slope) + sqrt(cov[0][1] * cov[1][0] / base / abs(slope)) / 2d)

        return ["no": no, "noErr": noErr, "ne": ne, "neErr": neErr, "chi2": fit.chi2];
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
