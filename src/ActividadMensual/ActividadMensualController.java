/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package ActividadMensual;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import upv.ipc.sportlib.Activity;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.time.Duration;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author SARE
 */
public class ActividadMensualController implements Initializable {

    private final SportActivityApp app = SportActivityApp.getInstance();
    
    @FXML
    private Label lblMes;
    @FXML
    private Label lblNumActividades;
    @FXML
    private Label lblDistancia;
    @FXML
    private Label lblTiempo;
    @FXML
    private Label lblAscenso;
    @FXML
    private Label lblDescenso;
    @FXML
    private Button btnCerrar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        YearMonth mesActual = YearMonth.now();
        ZoneId zone = ZoneId.systemDefault();

        // Poner el mes en el label
        String mesNombre = mesActual.getMonth()
            .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        lblMes.setText(mesNombre + " " + mesActual.getYear());

        // Calcular estadísticas
        double distTotalM = 0;
        Duration tiempoTotal = Duration.ZERO;
        double ascensoTotal = 0;
        double descensoTotal = 0;
        int n = 0;

        for (Activity a : app.getUserActivities()) {
            YearMonth ym = YearMonth.from(a.getStartTime().atZone(zone));
            if (ym.equals(mesActual)) {
                distTotalM    += a.getTotalDistance();
                tiempoTotal    = tiempoTotal.plus(a.getDuration());
                ascensoTotal  += a.getElevationGain();
                descensoTotal += a.getElevationLoss();
                n++;
            }
        }
        
        lblNumActividades.setText(String.valueOf(n));
        lblDistancia.setText(String.format("%.2f km", distTotalM / 1000.0));
        lblTiempo.setText(formatDuration(tiempoTotal));
        lblAscenso.setText(String.format("+%.0f m", ascensoTotal));
        lblDescenso.setText(String.format("-%.0f m", descensoTotal));
    }    

    @FXML
    private void onCerrar(ActionEvent event) {
        ((Stage) lblMes.getScene().getWindow()).close();
    }
    
    private String formatDuration(Duration d) {
        long h = d.toHours();
        long m = d.toMinutesPart();
        long s = d.toSecondsPart();
        return String.format("%dh %02dm %02ds", h, m, s);
    }
    
}
