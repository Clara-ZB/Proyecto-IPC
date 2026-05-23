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
import upv.ipc.sportlib.SportActivityApp;

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
    private Button btnCancelar;
    @FXML
    private Button btnIS;
    @FXML
    private Text errorIS;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage ventana = (Stage)((Node)event.getSource()).getScene().getWindow();
        ventana.close();
    }

    @FXML
    private void handleIS(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String contraseña = txtContraseña.getText();
        
        errorIS.setText("");
        
        SportActivityApp app = SportActivityApp.getInstance();
        boolean ISCorrecto = app.login(usuario, contraseña);
        
        if(ISCorrecto){
            System.out.println("Sesión iniciada correctamente");
             Stage ventana = (Stage)((Node)event.getSource()).getScene().getWindow();
            ventana.close();
        }else{
            errorIS.setText("Usuario o contraseña incorrectos");
        }
        
    }
    
}
