package ru.mipt.physics.biref

data class XYErrPoint(val x: Double, val y: Double, val xErr: Double = 0.0, val yErr: Double)
data class DataPoint(val phi1: Double, val psio: Double, val psie: Double)

interface BirefUI {

    /**
     * Data currently loaded into UI in radians
     */
    var data: List<DataPoint>

    var alpha: Double

    var phiErr: Double

    var psiErr: Double

    /**
     * Display a message
     */
    fun message(m: String)

    fun ln();

    /**
     * Plot a constant line for ordinary wave
     */
    fun plotOConst(a: Double = 0.0, b: Double = 0.4, const: Double)

    /**
     * Plot data for ordinary wave
     */
    fun plotOData(data: List<XYErrPoint>)

    /**
     * Plot data for extraordinary wave
     */
    fun plotEData(data: List<XYErrPoint>)

    fun plotOFit(a: Double = 0.0, b: Double = 0.4, func: (Double) -> Double)

    fun plotEFit(a: Double = 0.0, b: Double = 0.4, func: (Double) -> Double)

    fun clearFits()

    fun cleatPlots()
}