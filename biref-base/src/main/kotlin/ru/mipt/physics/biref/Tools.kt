package ru.mipt.physics.biref

import kotlin.math.*


/**
 * Created by darksnake on 28-Dec-16.
 */

const val D2R = PI / 180.0

fun Double.sqr(): Double {
    return pow(2.0)
}

fun nVector(phi1: Vector, psi: Vector, a: Double): Vector {
    val phi2 = psi - phi1 + a;
    return (phi1.sin.pow(2) + phi2.sin.pow(2) + phi1.sin * phi2.sin * cos(a) * 2).sqrt / sin(a);
}

fun n(phi1: Double, psi: Double, a: Double): Double {
    val phi2 = psi - phi1 + a;
    return sqrt(sin(phi1).pow(2.0) + sin(phi2).pow(2.0) + sin(phi1) * sin(phi2) * cos(a) * 2) / sin(a);
}

fun cosThetaVector(phi1: Vector, n: Vector): Vector {
    return phi1.sin / n;
}

fun cosTheta(phi1: Double, n: Double): Double {
    return sin(phi1) / n;
}

fun nErr(phi1: Double, psi: Double, n: Double, a: Double, phi1Err: Double, psiErr: Double): Double {
    val phi2 = psi - phi1 + a;
    val nal = (0.5 * sin(2 * phi1) - 0.5 * sin(2 * phi2) -
            cos(a) * sin(phi1 - phi2)) / (2 * (n * sin(a).pow(2.0)));
    val nb = -(sin(phi2) * cos(phi2) +
            cos(a) * sin(phi1) * cos(phi2)) / (n * sin(a).pow(2.0));
    val res = sqrt((2 * phi1Err * nal).pow(2.0) + (nb * psiErr).pow(2.0))
    return res;
}

fun nErrVector(phi1: Vector, psi: Vector, n: Vector, a: Double, phi1Err: Double, psiErr: Double): Vector {
    val phi2 = psi - phi1 + a;
    val res = DoubleArray(phi1.size()) { i -> 0.0 };
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
    val det = m[0][0] * m[1][1] - m[1][0] * m[0][1];

    val mInv = arrayOf(arrayOf(0.0, 0.0), arrayOf(0.0, 0.0));//covariance matrix

    mInv[0][0] = m[1][1] / det;
    mInv[0][1] = -m[1][0] / det;
    mInv[1][0] = -m[0][1] / det;
    mInv[1][1] = m[0][0] / det;

    val slope = mInv[0][0] * b0 + mInv[0][1] * b1;
    val base = mInv[1][0] * b0 + mInv[1][1] * b1;

    val chi2 = (yVector.pow(2) * invSigma2Vector).sum() + slope.sqr() * m[0][0] + base.sqr() * m[1][1] - 2 * slope * b0 - 2 * base * b1 + 2 * slope * base * m[0][1];
    return LineFitResult(base = base, slope = slope, chi2 = chi2, cov = mInv);
}

data class NFitResult(val no: Double, val noErr: Double, val ne: Double, val neErr: Double, val chi2: Double);

fun calculate(nVector: Vector, costhVector: Vector, sigmanVector: Vector): NFitResult {

    val (base, slope, chi2, cov) = fitLine(costhVector.pow(2), nVector.pow(-2), sigmanVector * 2.0 / (nVector.pow(3)));

    val ne = 1.0 / sqrt(base);
    val neErr = sqrt(cov[1][1]) * 0.5 * (ne.pow(3.0));

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


fun calibrate(ui: BirefUI) {
    ui.ln()
    ui.message("Калибровка по обыкновенной волне...")
    val (phi1, psio) = buildoData(ui);
    val nVector = nVector(phi1, psio, ui.alpha);
    val costhVector = cosThetaVector(phi1, nVector);
    val sigmanVector = nErrVector(phi1, psio, nVector, ui.alpha, ui.phiErr, ui.psiErr);
    val ndf = phi1.size() - 2

    val maxCos2 = (costhVector.values.max() ?: 0.0).sqr();

    val (base, slope, chi2, cov) = fitLine(costhVector.pow(2), nVector, sigmanVector)

    ui.message("\tПри A = ${format(ui.alpha * 180 / PI)}" +
            ", phiErr = ${format(ui.phiErr * 180 / PI)}" +
            ", psiErr = ${format(ui.psiErr * 180 / PI)} " +
            "градусов получаем следующие параметры зависимости:\n" +
            "\tсмещение: ${format(base, 3)}, наклон: ${format(slope, 3)}, chi2 = ${format(chi2)}, количество степеней свободы: $ndf.")
    if (abs(slope) >= 0.02) {
        ui.message("\tПлохая калибровка!");
    }

    if (chi2 / ndf < 0.5) {
        ui.message("\tОшибки завышены!");
    } else if (chi2 / ndf > 2) {
        ui.message("\tОшибки занижены!");
    }

    ui.message("Вычисляем no как средне взвешенное для соответствющей серии измерений:")
    val (noConst, noErr) = average(nVector, sigmanVector);
    val chi2const = (((nVector - base) / sigmanVector).pow(2)).sum();
    ui.message("\tno = ${format(noConst, 3)} \u00b1 ${format(noErr, 3)}, chi2 = ${format(chi2const)}, количество степеней свободы: ${ndf + 1}.")

    ui.plotOConst(noConst, 0.0, maxCos2)
    ui.plotOFit(0.0, maxCos2) { it * slope + base }
}

fun analyze(ui: BirefUI) {
    ui.ln();
    ui.message("Расчет пe по необыкновенной волне...")

    val (phi1, psie) = buildeData(ui);

    val nVector = nVector(phi1, psie, ui.alpha);
    val costhVector = cosThetaVector(phi1, nVector);
    val sigmanVector = nErrVector(phi1, psie, nVector, ui.alpha, ui.phiErr, ui.psiErr);

    val maxCos2 = (costhVector.values.max() ?: 0.0).sqr();

    val (no, noErr, ne, neErr, chi2) = calculate(nVector, costhVector, sigmanVector);

    val ndf = phi1.size() - 2

    val func = { costh2: Double -> 1.0 / sqrt(costh2 / no.sqr() + (1 - costh2) / ne.sqr()) }
    ui.message("\tПри A = ${format(ui.alpha * 180 / PI)}" +
            ", phiErr = ${format(ui.phiErr * 180 / PI)}" +
            ", psiErr = ${format(ui.psiErr * 180 / PI)} " +
            "градусов получаем следующие результаты:\n" +
            "\tno = ${format(no, 3)}, noErr = ${format(noErr, 3)}, ne = ${format(ne, 4)}, neErr = ${format(neErr, 4)}, chi2 = ${format(chi2)}, количество степеней свободы: ${ndf}.")


    ui.plotEFit(0.0, maxCos2, func)
}

fun updateDataPlot(ui: BirefUI) {
    ui.plotOData(ui.data.filter { it.phi1 > 0 && it.psio > 0 }.map {
        val no = n(it.phi1, it.psio, ui.alpha);
        val noErr = nErr(it.phi1, it.psio, no, ui.alpha, ui.phiErr, ui.psiErr);
        val costho2 = cosTheta(it.phi1, no).sqr();
        XYErrPoint(costho2, no, 0.0, noErr);
    })

    ui.plotEData(ui.data.filter { it.phi1 > 0 && it.psie > 0 }.map {
        val no = n(it.phi1, it.psie, ui.alpha);
        val noErr = nErr(it.phi1, it.psie, no, ui.alpha, ui.phiErr, ui.psiErr);
        val costho2 = cosTheta(it.phi1, no).sqr();
        XYErrPoint(costho2, no, 0.0, noErr);
    })
}


/**
 * Format number using 2 digits precision
 * @param num
 * @return
 */
expect fun format(num: Number, digits: Int): String

fun format(num: Number): String {
    return format(num, 2)
}

/**
 * Convert table to Data
 * @return
 */
private fun buildeData(ui: BirefUI): Pair<Vector, Vector> {
    val phi1Vals = ArrayList<Double>()
    val psieVals = ArrayList<Double>()
    for (td in ui.data) {
        if (td.phi1 > 0 && td.psie > 0) {
            phi1Vals.add(td.phi1)
            psieVals.add(td.psie)
        }
    }
    return Pair(Vector(phi1Vals.toDoubleArray()), Vector(psieVals.toDoubleArray()));
}

private fun buildoData(ui: BirefUI): Pair<Vector, Vector> {
    val phi1Vals = ArrayList<Double>()
    val psioVals = ArrayList<Double>()
    for (td in ui.data) {
        if (td.phi1 > 0 && td.psio > 0) {
            phi1Vals.add(td.phi1)
            psioVals.add(td.psio)
        }
    }
    return Pair(Vector(phi1Vals.toDoubleArray()), Vector(psioVals.toDoubleArray()));
}
