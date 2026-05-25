/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Modificar;

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
public class ModificarController implements Initializable {

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
    private Text errorEmail;
    @FXML
    private Text errorFechaNacimiento;
    @FXML
    private Text errorContraseña;
    @FXML
    private DatePicker txtFechaNacimiento;
    @FXML
    private Text errorUsuario;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnRegistrar;
    @FXML
    private ImageView avatar;
    private Image avatarDefault;
    private String avatarPath = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtUsuario.setDisable(true);
        avatarDefault = new Image(getClass().getResource("/resources/user.jpg").toExternalForm());

        Circle circulo = new Circle();
        circulo.setRadius(60);
        circulo.setCenterX(60);
        circulo.setCenterY(60);
        avatar.setClip(circulo);

        SportActivityApp app = SportActivityApp.getInstance();

        User usuario = app.getCurrentUser();
        txtUsuario.setText(usuario.getNickName());
        txtEmail.setText(usuario.getEmail());
        txtFechaNacimiento.setValue(usuario.getBirthDate());
        avatarPath = usuario.getAvatarPath();

        if(usuario.getAvatar() != null){
            avatar.setImage(usuario.getAvatar());
        }else{
            avatar.setImage(avatarDefault);
        }
    }

    @FXML
    private void handleBorrar(ActionEvent event) {
        avatar.setImage(avatarDefault);
        avatarPath = null;
    }

    @FXML
    private void handleSeleccionar(ActionEvent event) {

        FileChooser seleccionarAvatar = new FileChooser();
        seleccionarAvatar.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagenes","*.png","*.jpg","*.jpeg"));
        File archivo = seleccionarAvatar.showOpenDialog(avatar.getScene().getWindow() );

        if(archivo != null){
            avatarPath = archivo.getAbsolutePath();
            Image imagen = new Image(archivo.toURI().toString());
            avatar.setImage(imagen);
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage ventana = (Stage)((Node)event.getSource()).getScene().getWindow();
        ventana.close();
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtContraseña.getText();
        LocalDate fechaNacimiento =txtFechaNacimiento.getValue();

        boolean valido = true;

        errorEmail.setText("");
        errorContraseña.setText("");
        errorFechaNacimiento.setText("");

        if(!User.checkEmail(email)){
            errorEmail.setText("Formato email incorrecto");
            valido = false;
        }
        
        if(!password.isEmpty() && !User.checkPassword(password)){
            errorContraseña.setText("Contraseña incorrecta");
            valido = false;
        }

        if(fechaNacimiento == null || !User.isOlderThan(fechaNacimiento, 12)){
            errorFechaNacimiento.setText("Debes tener más de 12 años");
            valido = false;
        }

        if(!valido){
            return;
        }

        SportActivityApp app = SportActivityApp.getInstance();
        User usuario = app.getCurrentUser();

        if(password.isEmpty()){
            password = usuario.getPassword();
        }

        app.updateCurrentUser(email, password, fechaNacimiento, avatarPath);
        System.out.println("Perfil modificado correctamente");
        Stage ventana = (Stage)((Node)event.getSource()).getScene().getWindow();
        ventana.close();
    }
}