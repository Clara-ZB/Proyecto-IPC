/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Historial;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import upv.ipc.sportlib.Session;

/**
 * FXML Controller class
 *
 * @author Clara <clzahbal@etsinf.upv.es>
 */
public class HistorialController implements Initializable {
    
    private int horasT = 0;
    private int minTot = 0;
    private int actividades = 0;
    private int vistas = 0;
    private int anotaciones = 0;
    
    @FXML
    private ListView<?> listaSes;
    @FXML
    private Label totalTxt;
    @FXML
    private Label actividadesTxt;
    @FXML
    private Label vistasTxt;
    @FXML
    private Label anotTxt;

    /**
     * Inicializa las estadísticas totales
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actualizarTotales();
    }    
    
    /**
    * Añade una sesión al historial de sesiones
    * @param Sesion a añadir
    */
    public void addSesion (Session s){
        java.time.Duration dur = s.getDuration();
        int act = s.getImportedActivities(), vista = s.getViewedActivities(), anot = s.getAnnotationsCreated();
        /*CREAR ELEMENTO EN LISTA*/
        
        
        /*ACTUALIZAR TOTALES*/
        horasT += dur.toHours();
        minTot += dur.toMinutes();
        if(dur.toSeconds()>30) minTot++;
        
        actividades += act;
        vistas += vista;
        anotaciones += anot;
    }
    
    private void actualizarTotales (){
        totalTxt.setText("Duración total: " + horasT + " h, " + minTot + " min");
        actividadesTxt.setText(actividades + " actividades importadas");
        vistasTxt.setText(vistas + " vistas");
        anotTxt.setText(anotaciones + " anotaciones creadas");
    }
    
}
