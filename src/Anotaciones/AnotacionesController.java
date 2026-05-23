/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Anotaciones;

import com.sun.javafx.scene.CameraHelper;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import upv.ipc.sportlib.*;

/**
 * FXML Controller class
 *
 * @author Clara <clzahbal@etsinf.upv.es>
 */
public class AnotacionesController implements Initializable {

    @FXML
    private ToggleGroup tipoAnotacion;
    @FXML
    private RadioButton btnCirculo;
    @FXML
    private RadioButton btnPunto;
    @FXML
    private RadioButton btnLinea;
    @FXML
    private RadioButton btnTexto;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private TextArea descripcion;
    private boolean guardarPresionado = false;
    private Annotation ann;
    @FXML
    private ColorPicker btnColor;
    @FXML
    private Circle graphCirculo;
    @FXML
    private Circle graphPunto;
    @FXML
    private Line graphLinea;
    
    private GeoPoint geopunto;
    @FXML
    private Label graphTexto;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    /**
     * 
     * @param p GeoPoint del raton en el mapa
     */
    public void setGeoPoint(GeoPoint p){
        geopunto = p;
    }
    
    /**
     * Comprobar antes si se ha guardado
     * @return Anotacion creada
     */
    public Annotation getAnnotation(){
        return ann;
    }
    
    /**
     * 
     * @return boolean para saber si se ha guardado la anotación o no, true = presionado
     */
    public boolean guardarPressed() {
        return guardarPresionado;
    }
    
    @FXML
    private void Guardar(MouseEvent event) {

        if (btnCirculo.isSelected()) {        //Segun el boton de seleccion creamos el tipo de anotación
            ann = new Annotation(
                    AnnotationType.CIRCLE, // tipo de anotación
                    descripcion.getText(), // texto (puede ser vacío)
                    btnColor.getValue().toString(), // color en formato CSS hex
                    2.0, // grosor del trazo
                    List.of(geopunto) // puntos geográficos (ver tabla de tipos)
            );

        } else if (btnPunto.isSelected()) {
            ann = new Annotation(
                    AnnotationType.POINT, // tipo de anotación
                    descripcion.getText(), // texto (puede ser vacío)
                    btnColor.getValue().toString(), // color en formato CSS hex
                    2.0, // grosor del trazo
                    List.of(geopunto) // puntos geográficos (ver tabla de tipos)
            );
        } else if (btnLinea.isSelected()) {   //Linea atravesando la ruta en ese punto
            ann = new Annotation(
                    AnnotationType.LINE, // tipo de anotación
                    descripcion.getText(), // texto (puede ser vacío)
                    btnColor.getValue().toString(), // color en formato CSS hex
                    2.0, // grosor del trazo
                    List.of(new GeoPoint(geopunto.getLatitude() -15, geopunto.getLongitude()+15), new GeoPoint(geopunto.getLatitude() +15, geopunto.getLongitude()-15)) // puntos geográficos (ver tabla de tipos)
            );
        } else { //Texto
            ann = new Annotation(
                    AnnotationType.TEXT, // tipo de anotación
                    descripcion.getText(), // texto (puede ser vacío)
                    btnColor.getValue().toString(), // color en formato CSS hex
                    2.0, // grosor del trazo
                    List.of(geopunto) // puntos geográficos (ver tabla de tipos)
            );
        }
        guardarPresionado = true;
        descripcion.getScene().getWindow().hide();

    }

    @FXML
    private void Cancelar(MouseEvent event) {
        guardarPresionado = false;
        descripcion.getScene().getWindow().hide();
    }

    @FXML
    private void cambiarColor(ActionEvent event) {  //segun el color que se haya cambiado, los iconos cambian
        Color col = btnColor.getValue();            
        graphCirculo.setStroke(col);
        graphPunto.setFill(col);
        graphLinea.setStroke(col);
        graphTexto.setTextFill(col);
        //texto se podria con jlabel + foreground, pero hay que añadir otras librerias y mirar como implementar con  scenebuilder
    }
}
