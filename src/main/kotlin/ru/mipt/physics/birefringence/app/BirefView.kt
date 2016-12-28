package ru.mipt.physics.birefringence.app

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.util.converter.DoubleStringConverter
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.fx.ChartViewer
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYErrorRenderer
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYIntervalSeries
import org.jfree.data.xy.XYIntervalSeriesCollection
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import ru.mipt.physics.birefringence.*
import ru.mipt.physics.birefringence.Vector
import tornadofx.*
import java.awt.BasicStroke
import java.awt.Color
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern

/**
 * Created by darksnake on 28-Dec-16.
 */
class BirefView : View("Biref") {
    override val root: BorderPane by fxml(location = "/fxml/Biref.fxml")


    /**
     * Support class for data table display
     */
    class TableData(phi1: Double = 0.0, psio: Double = 0.0, psie: Double = 0.0) {
        var phi1 by property(phi1)
        fun phi1Property() = getProperty(TableData::phi1)

        var psio by property(psio)
        fun psioProperty() = getProperty(TableData::psio)

        var psie by property(psie)
        fun psieProperty() = getProperty(TableData::psie)

    }

    val aField: Spinner<Double> by fxid();
    val phiErrField: Spinner<Double> by fxid();
    val psiErrField: Spinner<Double> by fxid();
    val dataTable: TableView<TableData> by fxid();

    val chartPane: AnchorPane by fxid();
    val output: TextArea by fxid();
    val outputPane: TitledPane by fxid();
    val progressIndicator: ProgressIndicator by fxid();

    val oData: XYIntervalSeries = XYIntervalSeries("oData");
    val eData: XYIntervalSeries = XYIntervalSeries("eData");
    val oFit: XYSeries = XYSeries("oFit");
    val oConstant: XYSeries = XYSeries("oConstant");
    val eFit: XYSeries = XYSeries("eFit");


    init {
        //Action to clear output
        val clearOutAction = MenuItem("Очистить");
        clearOutAction.setOnAction { event -> output.clear() }
        output.contextMenu = ContextMenu(clearOutAction);

        with(dataTable) {
            isEditable = true
            column("phi1", TableData::phi1).useTextField(DoubleStringConverter()){
                it.rowValue.phi1 = it.newValue;
                if (dataTable.items[dataTable.items.size - 1].phi1 > 0) {
                    dataTable.items.add(TableData())
                }
                updateChartData();
            }
            column("psio", TableData::psio).useTextField(DoubleStringConverter()){
                it.rowValue.psio = it.newValue;
                updateChartData();
            }
            column("psie", TableData::psie).useTextField(DoubleStringConverter()){
                it.rowValue.psie = it.newValue;
                updateChartData();
            }
            columnResizePolicy = SmartResize.POLICY
            selectionModel.isCellSelectionEnabled = true;
        }

        val listener = ChangeListener<Double> { observableValue: ObservableValue<out Double>, d: Double, d1: Double -> updateChartData() }

        aField.valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(20.0, 45.0, 38.0, 0.05)
        aField.valueProperty().addListener(listener);
//        aErrField.textProperty().addListener(listener);
        phiErrField.valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 3.0, 1.0, 0.05)
        phiErrField.valueProperty().addListener(listener);
        psiErrField.valueFactory = SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 3.0, 1.0, 0.05)
        psiErrField.valueProperty().addListener(listener);

        //initialize chart
        val xAxis = NumberAxis("cos^2(theta)");
        val yAxis = NumberAxis("n");
        yAxis.autoRangeIncludesZero = false
        val plot = XYPlot(null, xAxis, yAxis, XYErrorRenderer());
        val chart = JFreeChart(plot);
        val viewer = ChartViewer(chart);
        chartPane.children.add(viewer);
        AnchorPane.setBottomAnchor(viewer, 0.0);
        AnchorPane.setTopAnchor(viewer, 0.0);
        AnchorPane.setLeftAnchor(viewer, 0.0);
        AnchorPane.setRightAnchor(viewer, 0.0);

        val dataSeries = XYIntervalSeriesCollection()
        dataSeries.addSeries(oData);
        dataSeries.addSeries(eData);

        val fitSeries = XYSeriesCollection();
        fitSeries.addSeries(oFit)
        fitSeries.addSeries(eFit)
        fitSeries.addSeries(oConstant)
        val lineRenderer = XYLineAndShapeRenderer(true, false);
        plot.setRenderer(1, lineRenderer)
        lineRenderer.setSeriesPaint(0, Color.red)
        lineRenderer.setSeriesStroke(0, BasicStroke(2.0.toFloat()))
        lineRenderer.setSeriesPaint(1, Color.blue)
        lineRenderer.setSeriesStroke(1, BasicStroke(2.0.toFloat()))
        lineRenderer.setSeriesPaint(2, Color.black)
        lineRenderer.setSeriesStroke(2, BasicStroke(2.0.toFloat()))



        plot.setDataset(0, dataSeries);
        plot.setDataset(1, fitSeries)
        //Debug mode
        loadFile(resources.stream("/data.txt")!!);
    }

    private fun getA(): Double {
        return aField.value * Math.PI / 180;
    }

    private fun getPhiErr(): Double {
        return phiErrField.value * Math.PI / 180;
    }

    private fun getPsiErr(): Double {
        return psiErrField.value * Math.PI / 180;
    }

    fun updateChartData() {
        clearFits()
        clearDataPlots()
        val a = getA();
        val phiErr = getPhiErr()
        val psiErr = getPsiErr();
        dataTable.items.forEach { it ->
            val phi1 = it.phi1 * Math.PI / 180
            val psio = it.psio * Math.PI / 180
            val psie = it.psie * Math.PI / 180

            if (phi1 > 0) {
                if (psio > 0) {
                    val no = n(phi1, psio, a);
                    val noErr = sigman(phi1, psio, no, a, phiErr, psiErr);
                    val costho2 = (costh(phi1, no).sqr());
                    oData.add(costho2, costho2, costho2, no, no - noErr, no + noErr);
                }
                if (psie > 0) {
                    val ne = n(phi1, psie, a);
                    val neErr = sigman(phi1, psie, ne, a, phiErr, psiErr);
                    val costhe2 = (costh(phi1, ne).sqr());
                    eData.add(costhe2, costhe2, costhe2, ne, ne - neErr, ne + neErr);
                }
            }
        }
    }

    private fun clearDataPlots() {
        oData.clear();
        eData.clear();
    }


    private fun clearFits() {
        oFit.clear();
        eFit.clear();
        oConstant.clear()
    }

    /**
     * Convert table to Data
     * @return
     */
    private fun buildoData(): Pair<Vector, Vector> {
        val phi1Vals = ArrayList<Double>()
        val psioVals = ArrayList<Double>()
        for (td in dataTable.items) {
            if (td.phi1 > 0 && td.psio > 0) {
                phi1Vals.add(td.phi1 * Math.PI / 180)
                psioVals.add(td.psio * Math.PI / 180)
            }
        }
        return Pair(Vector(phi1Vals.toTypedArray()), Vector(psioVals.toTypedArray()));
    }

    /**
     * Convert table to Data
     * @return
     */
    private fun buildeData(): Pair<Vector, Vector> {
        val phi1Vals = ArrayList<Double>()
        val psieVals = ArrayList<Double>()
        for (td in dataTable.items) {
            if (td.phi1 > 0 && td.psie > 0) {
                phi1Vals.add(td.phi1 * Math.PI / 180)
                psieVals.add(td.psie * Math.PI / 180)
            }
        }
        return Pair(Vector(phi1Vals.toTypedArray()), Vector(psieVals.toTypedArray()));
    }

    fun onCalibrateClick() {
        //TODO check if calculation is in progress
        ln();
        message("Начинаем проверку калибровки по обыкновенной волне...")

        data class Res(val nVector: Vector, val sigmanVector: Vector, val fitResult: LineFitResult, val ndf: Int, val maxCos2: Double);

        //calculating on the separate thread
        val task = runAsync {
            val (phi1, psio) = buildoData();

            val nVector = nVector(phi1, psio, getA());
            val costhVector = costhVector(phi1, nVector);
            val sigmanVector = sigmanVector(phi1, psio, nVector, getA(), getPhiErr(), getPsiErr());
            val fitRes = fitLine(costhVector.pow(2), nVector, sigmanVector)

            Res(nVector, sigmanVector, fitRes, phi1.size() - 2, costhVector.values.max()!!.sqr())
        } ui {
            val (base, slope, chi2, cov) = it.fitResult;

            message("\tПри A = ${f(getA() * 180 / Math.PI)}" +
                    ", phiErr = ${f(getPhiErr() * 180 / Math.PI)}" +
                    ", psiErr = ${f(getPsiErr() * 180 / Math.PI)} " +
                    "градусов получаем следующие параметры зависимости:\n" +
                    "\tсмещение: ${f(base, 3)}, наклон: ${f(slope, 3)}, chi2 = ${f(chi2)}, количество степеней свободы: ${it.ndf}.")
            if (Math.abs(slope) >= 0.02) {
                message("\tПлохая калибровка!");
            }

            if (chi2 / it.ndf < 0.5) {
                message("\tОшибки завышены!");
            } else if (chi2 / it.ndf > 2) {
                message("\tОшибки занижены!");
            }

            ln();
            message("Вычисляем no как средне взвешенное для соответствющей серии измерений:")
            val (noConst, noErr) = average(it.nVector, it.sigmanVector);
            val chi2const = (((it.nVector - base) / it.sigmanVector).pow(2)).sum();
            message("\tno = ${f(noConst, 3)} \u00b1 ${f(noErr, 3)}, chi2 = ${f(chi2const)}, количество степеней свободы: ${it.ndf + 1}.")


            oFit.clear();
            oConstant.clear();
            var x = 0.0;
            while (x < it.maxCos2) {
                oFit.add(x, x * slope + base);
                oConstant.add(x, noConst);
                x += 0.005
            }

            outputPane.setExpanded(true)
        }
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
    }


    fun onAnalyzeClick() {
        ln();
        message("Начинаем расчет пe по необыкновенной волне...")

        //calculating on the separate thread
        val task = runAsync {
            val (phi1, psie) = buildeData();

            val nVector = nVector(phi1, psie, getA());
            val costhVector = costhVector(phi1, nVector);
            val sigmanVector = sigmanVector(phi1, psie, nVector, getA(), getPhiErr(), getPsiErr());
            val fitRes = calculate(nVector, costhVector, sigmanVector);
            //result, ndf, max cos^2
            Triple<NFitResult, Int, Double>(fitRes, phi1.size() - 2, costhVector.values.max()!!.sqr());
        } ui {
            val (no, noErr, ne, neErr, chi2) = it.first //destructuring declaration of NFitResult
            val func = { costh2: Double, no: Double, ne: Double -> 1.0 / Math.sqrt(costh2 / no.sqr() + (1 - costh2) / ne.sqr()) }
            message("\tПри A = ${f(getA() * 180 / Math.PI)}" +
                    ", phiErr = ${f(getPhiErr() * 180 / Math.PI)}" +
                    ", psiErr = ${f(getPsiErr() * 180 / Math.PI)} " +
                    "градусов получаем следующие результаты:\n" +
                    "\tno = ${f(no, 3)}, noErr = ${f(noErr, 3)}, ne = ${f(ne, 4)}, neErr = ${f(neErr, 4)}, chi2 = ${f(chi2)}, количество степеней свободы: ${it.second}.")

            eFit.clear();
            // link to formula in documentation

            var x = 0.0;
            while (x < it.third) {
                eFit.add(x, func(x, no, ne));
                x += 0.005
            }
            outputPane.isExpanded = true
        }

        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
    }

    private fun message(m: String) {
        output.appendText(m);
        output.appendText("\n")
    }

    private fun ln() {
        output.appendText("==========================\n")
    }

    /**
     * Format number using 2 digits precision
     * @param num
     * @return
     */
    private fun f(num: Number, digits: Int = 2): String {
        return String.format("%.${digits}f", num);
    }

    fun onLoadClick() {
        val chooser = FileChooser();
        val file = chooser.showOpenDialog(primaryStage.scene.window);
        clearDataPlots()
        dataTable.items.clear()
        try {
            loadFile(file.inputStream())
        } catch (ex: Exception) {
            val alert = Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Не удалось загруить файл с данными")
            alert.setContentText("Проверьте что загружается файл в правильном формате")
            alert.show();
        }
    }

    /**
     * Loading external file
     * @param stream
     */
    private fun loadFile(stream: InputStream) {
        stream.reader().forEachLine {
            if (!it.trim().startsWith("#") && !it.trim().isEmpty()) {
                val values = it.trim().split("\t")
                dataTable.items.add(TableData(phi1 = values[0].toDouble(), psio = values[1].toDouble(), psie = values[2].toDouble()));
            }
        }
        updateChartData()
    }

    fun onClearClick() {
        clearDataPlots()
        clearFits()
        dataTable.items.clear()
    }

}
