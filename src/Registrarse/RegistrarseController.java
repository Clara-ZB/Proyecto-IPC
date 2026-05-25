/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Registrarse;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

/**
 * FXML Controller class
 *
 * @author marta
 */
public class RegistrarseController implements Initializable {

    @FXML
    private AnchorPane pgRegistrar;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnSeleccionar;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtContraseña;
    @FXML
    private Text errorUsuario;
    @FXML
    private DatePicker txtFechaNacimiento;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnRegistrar;
    @FXML
    private ImageView avatar;
    private Image avatarDefault;
    @FXML
    private Text errorEmail;
    @FXML
    private Text errorFechaNacimiento;
    @FXML
    private Text errorContraseña;
    private String avatarPath = null;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        avatarDefault = new Image(
                getClass().getResource("/resources/user.jpg").toExternalForm()
        );
        
        
        avatar.setImage(avatarDefault);
        
        Circle circulo = new Circle();
        
        circulo.setRadius(60);
        circulo.setCenterX(60);
        circulo.setCenterY(60);
        
        avatar.setClip(circulo);
           
    }    

    @FXML
    private void handleBorrar(ActionEvent event) {
        avatar.setImage(avatarDefault);
         
    }

    @FXML
    private void handleSeleccionar(ActionEvent event) {
        FileChooser seleccionarAvatar = new FileChooser();
        
        seleccionarAvatar.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg")
        );
        
        File archivo = seleccionarAvatar.showOpenDialog(avatar.getScene().getWindow());
        
        if(archivo != null){
            avatarPath = archivo.getAbsolutePath();
            Image imagen = new Image(archivo.toURI().toString());
            avatar.setImage(imagen);
        }
        
        
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage registro = (Stage)((Node)event.getSource()).getScene().getWindow();
        registro.close();
        
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        
        String usuario = txtUsuario.getText();
        String email = txtEmail.getText();
        String contraseña = txtContraseña.getText();
        LocalDate fecha_nacimiento = txtFechaNacimiento.getValue();
        Image avatarImage = avatar.getImage();
        boolean valido = true;
        
        errorUsuario.setText("");
        errorEmail.setText("");
        errorContraseña.setText("");
        errorFechaNacimiento.setText("");
        
        if(!User.checkNickName(usuario)){
            errorUsuario.setText("Usuario inválido:entre 6 y 15 caracteres, solo letras, dígitos, guion o subguion.  ");
            valido = false;
        }
        if(!User.checkEmail(email)){
            errorEmail.setText("Formato inválido: usuario@dominio.");
            valido = false;
        }
        if(!User.checkPassword(contraseña)){
            errorContraseña.setText("Contraseña inválida:entre 8 y 20 caracteres, " 
                    + "con al menos una mayúscula, una minúscula, un \n" +
                    "dígito y un símbolo (!@#$%&*()-+=)");
            valido = false;
        }
        if(fecha_nacimiento == null || !User.isOlderThan(fecha_nacimiento, 12)){
            errorFechaNacimiento.setText("Debes tener más de 12 años");
            valido = false;
        }
        if(!valido){
            
            return;
        }
        //errorUsuario.setText("");
        //errorEmail.setText("");
        //errorContraseña.setText("");
        SportActivityApp app = SportActivityApp.getInstance();
        boolean registrado = app.registerUser(usuario, email, contraseña, fecha_nacimiento, avatarPath);
        
        if(registrado){
            System.out.println("Usuario registrado correctamente");
            Stage registro = (Stage)((Node)event.getSource()).getScene().getWindow();
            registro.close();
        }else{
            errorUsuario.setText("El usuario ya existe");
        }
    }
    
    
}
