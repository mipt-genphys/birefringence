package ru.mipt.physics.birefringence.app

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
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
import org.jfree.data.xy.XYIntervalSeries
import org.jfree.data.xy.XYIntervalSeriesCollection
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import ru.mipt.physics.birefringence.Evaluator


/**
 * Created by darksnake on 21-May-16.
 */
class BirefView implements Initializable {

    @FXML
    private TextField aField;
    @FXML
    private TextField aErrField;
    @FXML
    private TextField phiErrField;
    @FXML
    private TextField psiErrField;

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


    private Evaluator ev = new Evaluator();

    private XYIntervalSeries oData = new XYIntervalSeries("oData");
    private XYIntervalSeries eData = new XYIntervalSeries("eData");
    private XYSeries oFit = new XYSeries("oFit");
    private XYSeries eFit = new XYSeries("eFit");

    @Override
    void initialize(URL location, ResourceBundle resources) {
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

        ChangeListener<String> listener = { ObservableValue<? extends String> observable, String oldValue, String newValue -> updateChartData() };

        aField.textProperty().addListener(listener);
        aErrField.textProperty().addListener(listener);
        phiErrField.textProperty().addListener(listener);
        psiErrField.textProperty().addListener(listener);

        //initialize chart
        NumberAxis xAxis =new NumberAxis("n");
        NumberAxis yAxis =new NumberAxis("cos^2(theta)");
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


        plot.setDataset(0, dataSeries);
        plot.setDataset(1, fitSeries)
        //Debug mode
        loadFile(getClass().getResourceAsStream("/data.txt"));
    }

    private double getA() {
        return aField.getText().toDouble() * Math.PI / 180;
    }

    private double getAErr() {
        return aErrField.getText().toDouble() * Math.PI / 180;
    }

    private double getPhiErr() {
        return phiErrField.getText().toDouble() * Math.PI / 180;
    }

    private double getPsiErr() {
        return psiErrField.getText().toDouble() * Math.PI / 180;
    }

    void updateChartData() {
        clearDataPlots()
        double a = getA();
        double aErr = getAErr()
        double phiErr = getPhiErr()
        double psiErr = getPsiErr();
        dataTable.items.forEach { TableData it ->
            Double phi1 = it.phi1* Math.PI / 180
            Double psio = it.psio* Math.PI / 180
            Double psie = it.psie* Math.PI / 180

            if (phi1) {
                if (psio) {
                    double no = ev.n(phi1, psio, a);
                    double noErr = ev.sigman(phi1, psio, no, a, aErr, phiErr, psiErr);
                    double costho2 = (ev.costh(phi1, no)**2d).doubleValue();
                    oData.add(costho2, costho2, costho2, no, no - noErr, no + noErr);
                }
                if (psie) {
                    double ne = ev.n(phi1, psie, a);
                    double neErr = ev.sigman(phi1, psie, ne, a, aErr, phiErr, psiErr);
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

    }

    @FXML
    public void onCalibrateClick(ActionEvent actionEvent) {

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
    public void onAnalyzeClick(ActionEvent actionEvent) {}

    @FXML
    public void onClearClick(ActionEvent actionEvent) {
        clearDataPlots()
        dataTable.items.clear()
    }

    public class TableData {
        Double phi1 = 0;
        Double psio = 0;
        Double psie = 0;
    }


}
