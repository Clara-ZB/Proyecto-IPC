/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Anotaciones;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void Guardar(MouseEvent event) {
    }

    @FXML
    private void Cancelar(MouseEvent event) {
    }
    
}
