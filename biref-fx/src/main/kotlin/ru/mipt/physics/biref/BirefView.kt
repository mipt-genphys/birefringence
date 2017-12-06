package ru.mipt.physics.biref

import javafx.beans.binding.Bindings
import javafx.beans.value.ChangeListener
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
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
import tornadofx.*
import java.awt.BasicStroke
import java.awt.Color
import java.io.InputStream
import java.util.concurrent.Callable
import java.util.regex.Pattern

/**
 * Created by darksnake on 28-Dec-16.
 */
class BirefView : View("Biref"), BirefUI {
    override var data: List<DataPoint>
        get() = tableView.items.map { DataPoint(it.phi1 * D2R, it.psio * D2R, it.psie * D2R) }
        set(value) = runLater {
            tableView.items.setAll(value.map { TableData(it.phi1 / D2R, it.psio / D2R, it.psie / D2R) })
        }
    override var alpha: Double
        get() = aField.value * D2R
        set(value) = runLater {
            aField.editor.text = (value / D2R).toString()
        }
    override var phiErr: Double
        get() = phiErrField.value * D2R
        set(value) = runLater {
            phiErrField.editor.text = (value / D2R).toString()
        }
    override var psiErr: Double
        get() = psiErrField.value * D2R
        set(value) = runLater {
            psiErrField.editor.text = (value / D2R).toString()
        }


    override val root: Parent by fxml(location = "/fxml/Biref.fxml")

    /**
     * Support class for data table display
     */
    class TableData(phi1: Double = 0.0, psio: Double = 0.0, psie: Double = 0.0) {
        var phi1 by property(phi1)
        var psio by property(psio)
        var psie by property(psie)
    }

    private val aField: Spinner<Double> by fxid();
    private val phiErrField: Spinner<Double> by fxid();
    private val psiErrField: Spinner<Double> by fxid();
    private val tableView: TableView<TableData> by fxid();

    private val chartPane: AnchorPane by fxid();
    private val output: TextArea by fxid();
    private val outputPane: TitledPane by fxid();
    private val progressIndicator: ProgressIndicator by fxid();

    private val oData: XYIntervalSeries = XYIntervalSeries("oData");
    private val eData: XYIntervalSeries = XYIntervalSeries("eData");
    private val oFit: XYSeries = XYSeries("oFit");
    private val oConstant: XYSeries = XYSeries("oConstant");
    private val eFit: XYSeries = XYSeries("eFit");

    private val calibrateButton: Button by fxid()
    private val analyzeButton: Button by fxid()


    init {
        primaryStage.minHeight = 600.0;
        primaryStage.minWidth = 800.0;
        title = "Лабораторная работа \"Двулучепреломление\""

        //Action to clear output
        val clearOutAction = MenuItem("Очистить");
        clearOutAction.setOnAction { event -> output.clear() }
        output.contextMenu = ContextMenu(clearOutAction);

        with(tableView) {
            isEditable = true
            column("phi1", TableData::phi1).useTextField(DoubleStringConverter()) {
                it.rowValue.phi1 = it.newValue;
                if (tableView.items[tableView.items.size - 1].phi1 > 0) {
                    tableView.items.add(TableData())
                }
                updatePlot();
            }
            column("psio", TableData::psio).useTextField(DoubleStringConverter()) {
                it.rowValue.psio = it.newValue;
                updatePlot();
            }
            column("psie", TableData::psie).useTextField(DoubleStringConverter()) {
                it.rowValue.psie = it.newValue;
                updatePlot();
            }
            columnResizePolicy = SmartResize.POLICY
            selectionModel.isCellSelectionEnabled = true;
        }

        val listener = ChangeListener<Double> { _, _, _ -> updatePlot() }

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
        initData()
        calibrateButton.disableProperty().bind(Bindings.createBooleanBinding(Callable<Boolean> { data.size < 2 }, tableView.items));
        analyzeButton.disableProperty().bind(Bindings.createBooleanBinding(Callable<Boolean> { data.size < 2 }, tableView.items));
    }

    override fun cleatPlots() {
        runLater {
            oData.clear();
            eData.clear();
        }
    }


    override fun clearFits() {
        runLater {
            oFit.clear();
            eFit.clear();
            oConstant.clear()
        }
    }

    fun onCalibrateClick() {
        //calculating on the separate thread
        val task = runAsync {
            calibrate(this@BirefView)
        }
        outputPane.isExpanded = true
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
    }


    fun onAnalyzeClick() {
        //calculating on the separate thread
        val task = runAsync {
            analyze(this@BirefView)
        }
        outputPane.isExpanded = true
        progressIndicator.visibleProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
    }

    override fun message(m: String) {
        runLater {
            output.appendText(m);
            output.appendText("\n")
        }
    }

    override fun ln() {
        runLater {
            output.appendText("==========================\n")
        }
    }


    fun onLoadClick() {
        val chooser = FileChooser();
        val file = chooser.showOpenDialog(primaryStage.scene.window);
        if (file != null) {
            cleatPlots()
            tableView.items.clear()
            try {
                runAsync {
                    load(file.inputStream())
                }
            } catch (ex: Exception) {
                val alert = Alert(Alert.AlertType.ERROR);
                alert.headerText = "Не удалось загруить файл с данными"
                alert.contentText = "Проверьте что загружается файл в правильном формате"
                alert.show();
            }
        }
    }

    fun onSaveClick() {
        val chooser = FileChooser();
        chooser.initialFileName = "biref.txt"
        val file = chooser.showSaveDialog(primaryStage.scene.window);
        if (file != null) {
            runAsync {
                file.writer().use { wr ->
                    wr.appendln("#\tphi1\tpsio\tpsie");
                    tableView.items.forEach { item ->
                        wr.appendln("\t${item.phi1}\t${item.psio}\t${item.psie}")
                    }
                }
            } ui {
                val alert = Alert(Alert.AlertType.INFORMATION);
                alert.headerText = "Данные сохранены"
                alert.show();
            }
        }
    }

    /**
     * Loading external file
     * @param stream
     */
    fun load(stream: InputStream) {
        stream.reader().forEachLine {
            if (!it.trim().startsWith("#") && !it.trim().isEmpty()) {
                val values = it.trim().split(Pattern.compile("\\s+"))
                tableView.items.add(TableData(phi1 = values[0].toDouble(), psio = values[1].toDouble(), psie = values[2].toDouble()));
            }
        }
        updatePlot()
    }

    fun onClearClick() {
        cleatPlots()
        clearFits()
        tableView.items.clear()
        initData()
    }

    private fun initData() {
        tableView.items.add(TableData())
    }

    fun onHelpClick() {
        HelpFragment().openWindow(owner = primaryStage.scene.window, escapeClosesWindow = true)
    }

    override fun plotOConst(a: Double, b: Double, const: Double) {
        runLater {
            oConstant.clear();
            var x = a;
            while (x < b) {
                oConstant.add(x, const);
                x += 0.01
            }
        }
    }

    private fun updatePlot() {
        clearFits()
        updateDataPlot(this)
    }

    override fun plotOData(data: List<XYErrPoint>) {
        runLater {
            oData.clear()
            data.forEach {
                oData.add(it.x, it.x - it.xErr, it.x + it.xErr, it.y, it.y - it.yErr, it.y + it.yErr)
            }
        }
    }

    override fun plotEData(data: List<XYErrPoint>) {
        runLater {
            eData.clear()
            data.forEach {
                eData.add(it.x, it.x - it.xErr, it.x + it.xErr, it.y, it.y - it.yErr, it.y + it.yErr)
            }
        }
    }

    override fun plotOFit(a: Double, b: Double, func: (Double) -> Double) {
        runLater {
            oFit.clear();
            var x = a;
            while (x < b) {
                oFit.add(x, func(x));
                x += 0.005
            }
        }
    }

    override fun plotEFit(a: Double, b: Double, func: (Double) -> Double) {
        runLater {
            eFit.clear();
            // link to formula in documentation

            var x = a;
            while (x < b) {
                eFit.add(x, func(x));
                x += 0.005
            }
        }
    }
}
