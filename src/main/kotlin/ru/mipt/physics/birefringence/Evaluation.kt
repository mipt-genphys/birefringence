package ru.mipt.physics.birefringence

import java.lang.Math.*

/**
 * Created by darksnake on 28-Dec-16.
 */

fun Double.sqr(): Double {
    return pow(this, 2.0)
}


fun nVector(phi1: Vector, psi: Vector, a: Double): Vector {
    val phi2 = psi - phi1 + a;
    return (phi1.sin().pow(2) + phi2.sin().pow(2) + phi1.sin() * phi2.sin() * cos(a) * 2).sqrt() / sin(a);
}

fun n(phi1: Double, psi: Double, a: Double): Double {
    val phi2 = psi - phi1 + a;
    return sqrt(pow(sin(phi1), 2.0) + pow(sin(phi2), 2.0) + sin(phi1) * sin(phi2) * cos(a) * 2) / sin(a);
}

fun costhVector(phi1: Vector, n: Vector): Vector {
    return phi1.sin() / n;
}

fun costh(phi1: Double, n: Double): Double {
    return sin(phi1) / n;
}

fun sigman(phi1: Double, psi: Double, n: Double, a: Double, phi1Err: Double, psiErr: Double): Double {
    val phi2 = psi - phi1 + a;
    val nal = (0.5 * sin(2 * phi1) - 0.5 * sin(2 * phi2) -
            cos(a) * sin(phi1 - phi2)) / (2 * (n * pow(sin(a), 2.0)));
    val nb = -(sin(phi2) * cos(phi2) +
            cos(a) * sin(phi1) * cos(phi2)) / (n * pow(sin(a), 2.0));
    val res = sqrt(pow((2 * phi1Err * nal), 2.0) + pow((nb * psiErr), 2.0))
    return res;
}

fun sigmanVector(phi1: Vector, psi: Vector, n: Vector, a: Double, phi1Err: Double, psiErr: Double): Vector {
    val phi2 = psi - phi1 + a;
    val res = Array<Double>(phi1.size()) { i -> 0.0 };
    for (i in 0 until phi1.size()) {
        val nal = (0.5 * sin(2 * phi1[i]) - 0.5 * sin(2 * phi2[i]) -
                cos(a) * sin(phi1[i] - phi2[i])) / (2 * (n[i] * sin(a).sqr()));
        val nb = -(sin(phi2[i]) * cos(phi2[i]) +
                cos(a) * sin(phi1[i]) * cos(phi2[i])) / (n[i] * sin(a).sqr());
        res[i] = sqrt((2 * phi1Err * nal).sqr() + (nb * psiErr).sqr());
    }
    return Vector(res);
}

/**
 * calculating no as a weighted average
 * @param data
 */
fun average(nVector: Vector, sigmanVector: Vector): Pair<Double, Double> {
    val weights = sigmanVector.pow(-2);
    // weighted average
    val sum = (nVector * weights).sum()
    val weight = weights.sum()
    val no = sum / weight
    val noErr = sqrt(1 / weight)
    return Pair<Double, Double>(no, noErr)
}

data class LineFitResult(val base: Double, val slope: Double, val chi2: Double, val cov: Array<Array<Double>>);

/**
 * Fit single line analytically using xs, ys and y sigmas
 * @param xVector
 * @param yVector
 * @param sigmaVector
 * @return
 */
fun fitLine(xVector: Vector, yVector: Vector, sigmaVector: Vector): LineFitResult {
    val m = arrayOf(arrayOf(0.0, 0.0), arrayOf(0.0, 0.0))

    val invSigma2Vector = sigmaVector.pow(-2);

    val b0 = (yVector * xVector * invSigma2Vector).sum();
    val b1 = (yVector * invSigma2Vector).sum();
    m[0][0] = ((xVector.pow(2)) * invSigma2Vector).sum();
    m[0][1] = (xVector * invSigma2Vector).sum();
    m[1][0] = m[0][1];
    m[1][1] = invSigma2Vector.sum();
    val det = m [0][0] * m[1][1] - m[1][0] * m[0][1];

    val mInv = arrayOf(arrayOf(0.0, 0.0), arrayOf(0.0, 0.0));//covariance matrix

    mInv[0][0] = m[1][1] / det;
    mInv[0][1] = -m[1][0] / det;
    mInv[1][0] = -m[0][1] / det;
    mInv[1][1] = m[0][0] / det;

    val slope = mInv [0][0] * b0 + mInv[0][1] * b1;
    val base = mInv [1][0] * b0 + mInv[1][1] * b1;

    val chi2 = (yVector.pow(2) * invSigma2Vector).sum() + slope.sqr() * m[0][0] + base.sqr() * m[1][1] - 2 * slope * b0 - 2 * base * b1 + 2 * slope * base * m[0][1];
    return LineFitResult(base = base, slope = slope, chi2 = chi2, cov = mInv);
}

data class NFitResult(val no: Double, val noErr: Double, val ne: Double, val neErr: Double, val chi2: Double);

fun calculate(nVector: Vector, costhVector: Vector, sigmanVector: Vector): NFitResult {

    val (base, slope, chi2, cov) = fitLine(costhVector.pow(2), nVector.pow(-2), sigmanVector * 2.0 / (nVector.pow(3)));

    val ne = 1.0 / sqrt(base);
    val neErr = sqrt(cov[1][1]) * 0.5 * (pow(ne, 3.0));

    val no = 1.0 / sqrt(slope + base);
    val noErr = sqrt(cov[1][1] / 4.0 / base + cov[0][0] / 4.0 / abs(slope) + sqrt(cov[0][1] * cov[1][0] / base / abs(slope)) / 2.0)

    return NFitResult(no, noErr, ne, neErr, chi2);
}

/**
 * The angle of least deviation
 * @param psiomin
 * @param psiemin
 * @param a
 * @return
 */
fun leastDev(psiomin: Double, psiemin: Double, a: Double): Pair<Double, Double> {
    val no = sin((psiomin + a) / 2) / sin(a / 2)
    val ne = sin((psiemin + a) / 2) / sin(a / 2)
    return Pair(no, ne);
}

/**
 * Total reflection angle
 * @param phi1o
 * @param phi1e
 * @param a
 */
fun totalRef(phi1o: Double, phi1e: Double, a: Double): Pair<Double, Double> {
    val no = sqrt(sin(phi1o).sqr() + 1 + (2 * sin(phi1o) * cos(a))) / sin(a);
    val ne = sqrt(sin(phi1e).sqr() + 1 + (2 * sin(phi1e) * cos(a))) / sin(a);
    return Pair(no, ne);
}