package ru.mipt.physics.birefringence.app

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
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
import ru.mipt.physics.birefringence.Evaluator
import ru.mipt.physics.birefringence.Vector
import java.awt.BasicStroke
import java.awt.Color


/**
 * Created by darksnake on 21-May-16.
 */
class BirefView implements Initializable {

    @FXML
    private Spinner<Double> aField;
    @FXML
    private Spinner<Double> phiErrField;
    @FXML
    private Spinner<Double> psiErrField;

    @FXML
    private TableView<TableData> dataTable;
    @FXML
    private TableColumn<TableData, Double> phi1Column;
    @FXML
    private TableColumn<TableData, Double> psioColumn;
    @FXML
    private TableColumn<TableData, Double> psieColumn;
    @FXML
    private AnchorPane chartPane;
    @FXML
    private TextArea output;
    @FXML
    private TitledPane outputPane;
    @FXML
    private ProgressIndicator progressIndicator;

    private Evaluator ev = new Evaluator();

    private XYIntervalSeries oData = new XYIntervalSeries("oData");
    private XYIntervalSeries eData = new XYIntervalSeries("eData");
    private XYSeries oFit = new XYSeries("oFit");
    private XYSeries oConstant = new XYSeries("oConstant");
    private XYSeries eFit = new XYSeries("eFit");

    @Override
    void initialize(URL location, ResourceBundle resources) {
        //Action to clear output
        MenuItem clearOutAction = new MenuItem("Очистить");
        clearOutAction.setOnAction { event -> output.clear() }
        output.setContextMenu(new ContextMenu(clearOutAction));


        phi1Column.setCellValueFactory(new PropertyValueFactory<TableData, Double>("phi1"));
        phi1Column.setCellFactory(TextFieldTableCell.<TableData, Double> forTableColumn(new DoubleStringConverter()));
        phi1Column.onEditCommit = { TableColumn.CellEditEvent<TableData, Double> event ->
            event.getTableView().getItems().get(event.getTablePosition().getRow())
                    .setPhi1(event.getNewValue());
            updateChartData()
            if (dataTable.items[dataTable.items.size() - 1].phi1) {
                dataTable.items.add(new TableData())
            }
        }
        psioColumn.setCellValueFactory(new PropertyValueFactory<TableData, Double>("psio"));
        psioColumn.setCellFactory(TextFieldTableCell.<TableData, Double> forTableColumn(new DoubleStringConverter()));
        psioColumn.onEditCommit = { TableColumn.CellEditEvent<TableData, Double> event ->
            event.getTableView().getItems().get(event.getTablePosition().getRow())
                    .setPsio(event.getNewValue());
            updateChartData()

        }
        psieColumn.setCellValueFactory(new PropertyValueFactory<TableData, Double>("psie"));
        psieColumn.setCellFactory(TextFieldTableCell.<TableData, Double> forTableColumn(new DoubleStringConverter()));
        psieColumn.onEditCommit = { TableColumn.CellEditEvent<TableData, Double> event ->
            event.getTableView().getItems().get(event.getTablePosition().getRow())
                    .setPsie(event.getNewValue());
            updateChartData()
        }
        dataTable.getSelectionModel().setCellSelectionEnabled(true);

        ChangeListener<Double> listener = new ChangeListener<Double>() {
            @Override
            void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                updateChartData()
            }
        }

        aField.valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(20d, 45d, 38d, 0.05d)
        aField.valueProperty().addListener(listener);
//        aErrField.textProperty().addListener(listener);
        phiErrField.valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 3, 1, 0.05d)
        phiErrField.valueProperty().addListener(listener);
        psiErrField.valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 3, 1, 0.05d)
        psiErrField.valueProperty().addListener(listener);

        //initialize chart
        NumberAxis xAxis = new NumberAxis("cos^2(theta)");
        NumberAxis yAxis = new NumberAxis("n");
        yAxis.setAutoRangeIncludesZero(false)
        XYPlot plot = new XYPlot(null, xAxis, yAxis, new XYErrorRenderer());
        JFreeChart chart = new JFreeChart(plot);
        ChartViewer viewer = new ChartViewer(chart);
        chartPane.getChildren().add(viewer);
        AnchorPane.setBottomAnchor(viewer, 0d);
        AnchorPane.setTopAnchor(viewer, 0d);
        AnchorPane.setLeftAnchor(viewer, 0d);
        AnchorPane.setRightAnchor(viewer, 0d);

        XYIntervalSeriesCollection dataSeries = new XYIntervalSeriesCollection()
        dataSeries.addSeries(oData);
        dataSeries.addSeries(eData);

        XYSeriesCollection fitSeries = new XYSeriesCollection();
        fitSeries.addSeries(oFit)
        fitSeries.addSeries(eFit)
        fitSeries.addSeries(oConstant)
        XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(1, lineRenderer)
        lineRenderer.setSeriesPaint(0, Color.red)
        lineRenderer.setSeriesStroke(0, new BasicStroke(2))
        lineRenderer.setSeriesPaint(1, Color.blue)
        lineRenderer.setSeriesStroke(1, new BasicStroke(2))
        lineRenderer.setSeriesPaint(2, Color.black)
        lineRenderer.setSeriesStroke(2, new BasicStroke(2))



        plot.setDataset(0, dataSeries);
        plot.setDataset(1, fitSeries)
        //Debug mode
        loadFile(getClass().getResourceAsStream("/data.txt"));
    }

    private double getA() {
        return aField.getValue() * Math.PI / 180;
    }

//    private double getAErr() {
//        return aErrField.getText().toDouble() * Math.PI / 180;
//    }

    private double getPhiErr() {
        return phiErrField.getValue() * Math.PI / 180;
    }

    private double getPsiErr() {
        return psiErrField.getValue() * Math.PI / 180;
    }

    void updateChartData() {
        clearFits()
        clearDataPlots()
        double a = getA();
//        double aErr = getAErr()
        double phiErr = getPhiErr()
        double psiErr = getPsiErr();
        dataTable.items.forEach { TableData it ->
            Double phi1 = it.phi1 * Math.PI / 180
            Double psio = it.psio * Math.PI / 180
            Double psie = it.psie * Math.PI / 180

            if (phi1) {
                if (psio) {
                    double no = ev.n(phi1, psio, a);
                    double noErr = ev.sigman(phi1, psio, no, a, phiErr, psiErr);
                    double costho2 = (ev.costh(phi1, no)**2d).doubleValue();
                    oData.add(costho2, costho2, costho2, no, no - noErr, no + noErr);
                }
                if (psie) {
                    double ne = ev.n(phi1, psie, a);
                    double neErr = ev.sigman(phi1, psie, ne, a, phiErr, psiErr);
                    double costhe2 = (ev.costh(phi1, ne)**2d).doubleValue();
                    eData.add(costhe2, costhe2, costhe2, ne, ne - neErr, ne + neErr);
                }
            }
        }
    }

    private void clearDataPlots() {
        oData.clear();
        eData.clear();
    }


    private void clearFits() {
        oFit.clear();
        eFit.clear();
        oConstant.clear()
    }

    /**
     * Convert table to Data
     * @return
     */
    private Tuple2 buildoData() {

        List phi1Vals = new ArrayList()
        List psioVals = new ArrayList()
        for (TableData td : dataTable.items) {
            if (td.phi1 && td.psio) {
                phi1Vals << td.phi1 * Math.PI / 180
                psioVals << td.psio * Math.PI / 180
            }
        }
        return new Tuple2(new Vector(phi1Vals), new Vector(psioVals));
    }

    /**
     * Convert table to Data
     * @return
     */
    private Tuple2 buildeData() {

        List phi1Vals = new ArrayList()
        List psieVals = new ArrayList()
        for (TableData td : dataTable.items) {
            if (td.phi1 && td.psie) {
                phi1Vals << td.phi1 * Math.PI / 180
                psieVals << td.psie * Math.PI / 180
            }
        }
        return new Tuple2(new Vector(phi1Vals), new Vector(psieVals));
    }

    @FXML
    public void onCalibrateClick(ActionEvent actionEvent) {
        //TODO check if calculation is in progress
        ln();
        message("Начинаем проверку калибровки по обыкновенной волне...")

        Vector phi1;
        Vector psio;
        (phi1, psio) = buildoData();

        Vector nVector = ev.nVector(phi1, psio, getA());
        Vector costhVector = ev.costhVector(phi1, nVector);
        Vector sigmanVector = ev.sigmanVector(phi1, psio, nVector, getA(), getPhiErr(), getPsiErr());
        //calculating on the separate thread
        progressIndicator.setVisible(true)
        new Thread({
            def fitResult = ev.fitLine(costhVector**2, nVector, sigmanVector)
            def base = fitResult.base;
            def slope = fitResult.slope;
            def chi2 = fitResult.chi2;

            Platform.runLater({
                message("\tПри A = ${f(getA() * 180 / Math.PI)}" +
                        ", phiErr = ${f(getPhiErr() * 180 / Math.PI)}" +
                        ", psiErr = ${f(getPsiErr() * 180 / Math.PI)} " +
                        "градусов получаем следующие параметры зависимости:\n" +
                        "\tсмещение: ${f(base, 3)}, наклон: ${f(slope, 3)}, chi2 = ${f(chi2)}, количество степеней свободы: ${phi1.size() - 2}.")
                if (Math.abs(slope) >= 0.02) {
                    message("\tПлохая калибровка!");
                }

                if (chi2 / (phi1.size() - 2) < 0.5) {
                    message("\tОшибки завышены!");
                } else if (chi2 / (phi1.size() - 2) > 2) {
                    message("\tОшибки занижены!");
                }

                oFit.clear();
                for (double x = 0; x < costhVector.values().toList().max()**2; x += 0.005) {
                    oFit.add(x, x * slope + base);
                }

                def noConst;
                def noErr;
                ln();
                message("Вычисляем no как средне взвешенное для соответствющей серии измерений:")
                (noConst, noErr) = ev.average(nVector, sigmanVector);
                def chi2const = (((nVector - base) / sigmanVector)**2).values().sum();
                message("\tno = ${f(noConst, 3)} \u00b1 ${f(noErr, 3)}, chi2 = ${f(chi2const)}, количество степеней свободы: ${phi1.size() - 1}.")

                oConstant.clear();
                for (double x = 0; x < costhVector.values().toList().max()**2; x += 0.005) {
                    oConstant.add(x, noConst);
                }

                outputPane.setExpanded(true)
                progressIndicator.setVisible(false)
            })
        }).start()
    }

    @FXML
    public void onAnalyzeClick(ActionEvent actionEvent) {
        ln();
        message("Начинаем расчет пe по необыкновенной волне...")


        Vector phi1;
        Vector psie;
        (phi1, psie) = buildeData();

        Vector nVector = ev.nVector(phi1, psie, getA());
        Vector costhVector = ev.costhVector(phi1, nVector);
        Vector sigmanVector = ev.sigmanVector(phi1, psie, nVector, getA(), getPhiErr(), getPsiErr());
        //calculating on the separate thread
        progressIndicator.setVisible(true)
        new Thread({
            def func = { double costh2, double no, double ne -> 1 / Math.sqrt(costh2 / no**2 + (1 - costh2) / ne**2) }

            def fit = ev.calculate(nVector, costhVector, sigmanVector)
            def no = fit.no;
            def ne = fit.ne;
            def noErr = fit.noErr;
            def neErr = fit.neErr;

            def chi2 = fit.chi2;

            Platform.runLater({
                message("\tПри A = ${f(getA() * 180 / Math.PI)}" +
                        ", phiErr = ${f(getPhiErr() * 180 / Math.PI)}" +
                        ", psiErr = ${f(getPsiErr() * 180 / Math.PI)} " +
                        "градусов получаем следующие результаты:\n" +
                        "\tno = ${f(no, 3)}, noErr = ${f(noErr, 3)}, ne = ${f(ne, 4)}, neErr = ${f(neErr, 4)}, chi2 = ${f(chi2)}, количество степеней свободы: ${phi1.size() - 2}.")

                eFit.clear();
                // link to formula in documentation
                for (double x = 0; x < costhVector.values().toList().max()**2; x += 0.005) {
                    eFit.add(x, func(x, no, ne));
                }
                outputPane.setExpanded(true)
                progressIndicator.setVisible(false)
            })
        }).start()
    }

    private void message(String m) {
        output.appendText(m);
        output.appendText("\n")
    }

    private void ln() {
        output.appendText("==========================\n")
    }

    /**
     * Format number using 2 digits precision
     * @param num
     * @return
     */
    private String f(num, int digits = 2) {
        return String.format("%.${digits}f", num);
    }

    @FXML
    public void onLoadClick(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        if (file) {
            clearDataPlots()
            dataTable.items.clear()
            try {
                loadFile(new FileInputStream(file))
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Не удалось загруить файл с данными")
                alert.setContentText("Проверьте что загружается файл в правильном формате")
                alert.show();
            }
        }
    }

    /**
     * Loading external file
     * @param stream
     */
    private void loadFile(InputStream stream) {
        stream.eachLine {
            if (!it.trim().startsWith("#") && !it.trim().isEmpty()) {
                def values = it.trim().tokenize();
                dataTable.items.add(new TableData(phi1: values.get(0).toDouble(), psio: values.get(1).toDouble(), psie: values.get(2).toDouble()));
            }
        }
        updateChartData()
    }

    @FXML
    public void onClearClick(ActionEvent actionEvent) {
        clearDataPlots()
        clearFits()
        dataTable.items.clear()
    }

    /**
     * Support class for data table display
     */
    public class TableData {
        Double phi1 = 0;
        Double psio = 0;
        Double psie = 0;
    }


}
