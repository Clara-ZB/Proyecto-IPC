/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Anotaciones;

import com.sun.javafx.scene.CameraHelper;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import upv.ipc.sportlib.*;

/**
 * FXML Controller class
 *
 * @author Liz
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
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void Guardar(MouseEvent event) {
        AnnotationType tipo;
     
        if(btnCirculo.isSelected()){        //Segun el boton de seleccion creamos el tipo de anotación
            tipo = AnnotationType.CIRCLE;
            List<GeoPoint> puntos = List.of();
        } else if (btnPunto.isSelected()){
            tipo = AnnotationType.POINT;
            List<GeoPoint> puntos = List.of();
        } else if (btnLinea.isSelected()){   //Linea atravesando la ruta en ese punto
            tipo = AnnotationType.LINE;
            List<GeoPoint> puntos = List.of();
        }else{
            tipo = AnnotationType.TEXT;
            List<GeoPoint> puntos = List.of();
        }
        
        
        ann = new Annotation(
        tipo, // tipo de anotación
        "Zona peligrosa", // texto (puede ser vacío)
        "#E74C3C", // color en formato CSS hex
        2.0, // grosor del trazo
        List.of() // puntos geográficos (ver tabla de tipos)
     );
        guardarPresionado = true;
        
    }

    @FXML
    private void Cancelar(MouseEvent event) {
        descripcion.getScene().getWindow().hide();
    }
    
    
    public boolean guardarPressed() {
        return guardarPresionado;
    }
    
    public Annotation getAnn (){
        return ann;
    }
}
