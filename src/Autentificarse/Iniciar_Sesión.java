/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Autentificarse;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author marta
 */
public class Iniciar_Sesión implements Initializable {

    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtContraseña;
    @FXML
    private Text errorUsuario;
    @FXML
    private Text errorContraseña;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnIS;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage registro = (Stage)((Node)event.getSource()).getScene().getWindow();
        registro.close();
    }

    @FXML
    private void handleIS(ActionEvent event) {
        
    }
    
}
