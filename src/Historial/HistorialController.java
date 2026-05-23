/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Historial;

import java.net.URL;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.SportActivityApp;
import  javafx.scene.control.ListCell;
import javafx.scene.control.Cell;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Clara <clzahbal@etsinf.upv.es>
 */
public class HistorialController implements Initializable {
    
    private List<Session> lista;
    private int horasT;
    private int minTot;
    private int actividades;
    private int vistas;
    private int anotaciones;
    private ObservableList<Session> listaObs;
    
    @FXML
    private ListView<Session> listaHist;
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
        horasT = 0;
        minTot = 0;
        actividades = 0;
        vistas = 0;
        anotaciones = 0;
        SportActivityApp app = SportActivityApp.getInstance();
        //recorro toda al lista para recalcular estadísticas globales y formar la listview
        lista = app.getSessionsByUser(app.getCurrentUser());
        for(int i =0; i < lista.size(); i++){
            addSesion(lista.get(i));
        }
        actualizarTotales();
    }    
    
    /**
    * Añade una sesión al historial de sesiones
    * @param Sesion a añadir
    */
    private void addSesion (Session s){
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
    
    class sesListCell extends ListCell<Session> {
        
        private final GridPane grid = new GridPane();
        private final Label numSesion = new Label();
        private final Label fechaSesion = new Label();
        private final Label durSesion = new Label();
        private final Label actSesion = new Label();
        private final Label vistasSesion = new Label();
        private final Label anotSesion = new Label();

        
        public sesListCell() {
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));

            grid.add(numSesion, 0, 0);
            grid.add(fechaSesion, 0, 1);
            grid.add(durSesion, 1, 1);
            grid.add(actSesion, 1, 2);
            grid.add(vistasSesion, 0, 1);
            grid.add(anotSesion, 1, 1);
            //GridPane.setHalignment(quantityLabel, HPos.RIGHT);
        }
        
        
        @Override
        protected void updateItem(Session item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                setGraphic(null);
//            }else {
//            view.setImage(item.getImagen());
//            setGraphic(view);
//            setText(item.getNombre());
        }
    }
        
    
}}
