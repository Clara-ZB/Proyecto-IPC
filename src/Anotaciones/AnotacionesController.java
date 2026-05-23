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
    @FXML
    private ColorPicker btnColor;
    @FXML
    private Circle graphCirculo;
    @FXML
    private Circle graphPunto;
    @FXML
    private Line graphLinea;
    @FXML
    private TextField graphText;
    
    private int tipo;  //0 = circulo, 1 = punto, 2 = linea, 3 = texto
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void Guardar(MouseEvent event) {
        
        if(btnCirculo.isSelected()){        //Segun el boton de seleccion creamos el tipo de anotación
            tipo = 0;
        } else if (btnPunto.isSelected()){
            tipo = 1;
        } else if (btnLinea.isSelected()){   //Linea atravesando la ruta en ese punto
            tipo = 2;
        }else{
            tipo = 3;
        }
       
//        ann = new Annotation(
//        tipo, // tipo de anotación
//        "Zona peligrosa", // texto (puede ser vacío)
//        "#E74C3C", // color en formato CSS hex
//        2.0, // grosor del trazo
//        List.of() // puntos geográficos (ver tabla de tipos)
//     );

        guardarPresionado = true;
        
    }

    @FXML
    private void Cancelar(MouseEvent event) {
        guardarPresionado = false;
        descripcion.getScene().getWindow().hide();
    }
    
    /**
     * 
     * @return boolean para saber si se ha guardado la anotación o no, true = presionado
     */
    public boolean guardarPressed() {
        return guardarPresionado;
    }
    
    
    /**
     * 
     * @return 0 = circulo, 1 = punto, 2 = linea, 3 = texto
     */
    public int getTipo(){
        return tipo;
    }
    
    
    /**
     * 
     * @return descripción de la anotación
     */
    public String getDesc(){
        return descripcion.getText();
    }
    
    /**
     * 
     * @return texto asociado al tipo 3
     */
    public String getTexto(){
        return graphText.getText();
    }
    
    
    /**
     * 
     * @return color seleccionado
     */
    public Color getColor(){
        return btnColor.getValue();
    }

    @FXML
    private void cambiarColor(ActionEvent event) {  //segun el color que se haya cambiado, los iconos cambian
        Color col = btnColor.getValue();            
        graphCirculo.setStroke(col);
        graphPunto.setFill(col);
        graphLinea.setStroke(col);
        //texto se podria con jlabel + foreground, pero hay que añadir otras librerias y mirar como implementar con  scenebuilder
    }
}
