/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Historial;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.SportActivityApp;
import  javafx.scene.control.ListCell;
import javafx.scene.control.Cell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Clara <clzahbal@etsinf.upv.es>
 */
public class HistorialController implements Initializable {
    private final SportActivityApp app = SportActivityApp.getInstance();
    private List<Session> lista;
    private int horasT;
    private int minTot;
    private int actividades;
    private int vistas;
    private int anotaciones;
    private ObservableList<Session> listaObs;
    private int id;
    
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
        listaHist.getItems().clear();
        listaHist.setCellFactory(c-> new sesListCell()); 
        //recorro toda al lista para recalcular estadísticas globales 
        lista = app.getSessionsByUser(app.getCurrentUser());
        
        for(id =0; id < lista.size(); id++){
            addSesion(lista.get(id));
       }
        listaHist.getItems().setAll(app.getSessionsByUser(app.getCurrentUser()));
        actualizarTotales();
    }   
    
    
    
    /**
    * Añade una sesión al historial de sesiones
    * @param Sesion a añadir
    */
    private void addSesion (Session s){
        java.time.Duration dur = s.getDuration();
        int act = s.getImportedActivities(), vista = s.getViewedActivities(), anot = s.getAnnotationsCreated();
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
    
      private class sesListCell extends ListCell<Session> {
        
        private final GridPane grid = new GridPane();
        private final Label numSesion = new Label();
        private final Label fechaSesion = new Label();
        private final Label durSesion = new Label();
        private final Label actSesion = new Label();
        private final Label vistasSesion = new Label();
        private final Label anotSesion = new Label();

        
        public sesListCell() {                      //contructor del elemento en lista, un grid con labels
            super();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10));    //margenes internos
            
            grid.getColumnConstraints().add(new ColumnConstraints(290)); // column 0 is 100 wide
            grid.getColumnConstraints().add(new ColumnConstraints(240)); // column 1 is 200 wide
            grid.add(numSesion, 0, 0);
            grid.add(fechaSesion, 0, 1);
            grid.add(durSesion, 1, 0);
            grid.add(actSesion, 1, 1);
            grid.add(vistasSesion, 1, 2);
            grid.add(anotSesion, 1, 3);
            GridPane.setHalignment(durSesion, HPos.RIGHT);
            GridPane.setHalignment(actSesion, HPos.RIGHT);
            GridPane.setHalignment(vistasSesion, HPos.RIGHT);
            GridPane.setHalignment(anotSesion, HPos.RIGHT);
        }
        
        
        @Override
        protected void updateItem(Session item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {//no hay nada que añadir
                setText(null);
                setGraphic(null);
            }else {
            setGraphic(grid);
            numSesion.setText("Sesión ");
            fechaSesion.setText(item.getStartTime().toString().substring(0, 10)  + " a las " + item.getStartTime().toString().substring(11, 16));
            
            durSesion.setText(item.getDuration().toHours() + " h " + (item.getDuration().toMinutes()%60) + " m " + (item.getDuration().toSeconds()%60) + " s ");
            actSesion.setText(item.getImportedActivities() + " actividades importadas");
            vistasSesion.setText(item.getViewedActivities() + " actividades vistas");
            anotSesion.setText(item.getAnnotationsCreated() + " anotaciones creadas");
        }
     } 
    
      }
}     
    