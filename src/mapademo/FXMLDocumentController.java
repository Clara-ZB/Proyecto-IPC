/*
 * ============================================================
 *  PROYECTO EJEMPLO – IPC 2026
 *  Asignatura: Interfaces Persona-Computador
 *  Universitat Politècnica de València
 * ============================================================
 *
 *  DESCRIPCIÓN GENERAL
 *  -------------------
 *  Este controlador gestiona la vista principal de la aplicación
 *  de puntos de interés (POI) sobre un mapa.
 *
 *  Funcionalidades implementadas:
 *   1. Carga y visualización de una imagen de mapa.
 *   2. Zoom interactivo mediante un Slider.
 *   3. Añadir POIs (texto) y anotaciones (círculos) con clic derecho.
 *   4. Listado de POIs en un ListView con CellFactory personalizada.
 *   5. Centrado animado del mapa al seleccionar un POI de la lista.
 *   6. Modo inserción: activar con botón y colocar POI con siguiente clic.
 *
 *  PATRÓN UTILIZADO: MVC (Model-View-Controller)
 *   - Modelo : clase Poi  (datos del punto de interés)
 *   - Vista  : FXMLDocument.fxml  (layout declarativo)
 *   - Control: esta clase (lógica de interacción)
 *
 * ============================================================
 */
package mapademo;
//Pruebaaaaaadaw
//Doss
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import Anotaciones.AnotacionesController;
import com.sun.javafx.collections.MapListenerHelper;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import upv.ipc.sportlib.Activity;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import upv.ipc.sportlib.Annotation;
import upv.ipc.sportlib.AnnotationType;
import upv.ipc.sportlib.GeoPoint;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.TrackPoint;


/**
 * Controlador principal de la aplicación de mapa con POIs.
 *
 * La anotación @FXML conecta automáticamente los campos de esta clase
 * con los elementos declarados en el fichero FXML mediante su atributo fx:id.
 *
 * Implementa {@link Initializable} para poder ejecutar código de
 * inicialización una vez que el FXML ha sido cargado completamente.
 */
public class FXMLDocumentController implements Initializable {

    // =========================================================
    //  ESTRUCTURA DE NODOS PARA ZOOM
    // =========================================================
    //
    //  El zoom se consigue escalando un Group (zoomGroup).
    //  Escalar un Group NO desplaza los nodos que contiene,
    //  lo que evita el "salto" visual al hacer zoom.
    //
    //  Jerarquía de nodos:
    //
    //  ScrollPane (map_scrollpane)
    //   └─ contentGroup          ← Group raíz dentro del ScrollPane
    //       └─ zoomGroup         ← se escala para el zoom
    //           └─ mapPane       ← Pane con la imagen y los POIs
    //               ├─ ImageView ← imagen del mapa
    //               ├─ Text      ← etiquetas de POIs
    //               └─ Circle    ← anotaciones circulares
    //
    // =========================================================

    /** Group que se escala para aplicar el zoom. */
    private Group zoomGroup;
    
    /**
     * Pane que actúa como lienzo del mapa.
     * Contiene la imagen de fondo y todos los elementos superpuestos
     * (textos, círculos, etc.). Sus dimensiones coinciden con las de
     * la imagen cargada.
     */
    private Pane mapPane;
    
    private final SportActivityApp app = SportActivityApp.getInstance();

    
    /** Menú contextual reutilizable para el clic derecho sobre el mapa. */
    private ContextMenu mapContextMenu;
    

    /**
     * Indica si el controlador está en modo inserción de POI.
     * {@code true} → el próximo clic izquierdo sobre el mapa abre el diálogo.
     */
    private boolean insertionMode = false;

    // =========================================================
    //  ELEMENTOS FXML  (inyectados automáticamente por el cargador)
    // =========================================================

    /** Lista lateral que muestra todos los POIs añadidos al mapa. */
    @FXML
    private ListView<Activity> map_listview;

    /** ScrollPane que envuelve el mapa y permite desplazarlo. */
    @FXML
    private ScrollPane map_scrollpane;
    

    /**
     * Slider de zoom.
     * Rango: [0.5 – 1.5]. Valor inicial: 1.0 (sin zoom).
     * Cada cambio de valor llama al método zoom().
     */
    @FXML
    private Slider zoom_slider;

    /**
     * Botón de pin visible sobre el mapa.
     * Se desplaza hasta la posición del POI seleccionado en la lista.
     */
    private MenuButton map_pin;

    // FIX 5 — Eliminadas las variables sin uso:
    //   · 'mousePosistion' (errata + duplicado de mousePosition)
    //   · 'pin_info'       (inyectada pero nunca actualizada)

    /** Etiqueta en la barra de estado que muestra las coordenadas del ratón. */
    @FXML
    private Label mousePosition;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Button btnActividadMensual;
    @FXML
    private MenuButton menuUsuario;
    @FXML
    private VBox sidePanel;
    @FXML
    private Button btnAddActividad;
    @FXML
    private Label lblDistancia;
    @FXML
    private Label lblDuracion;
    @FXML
    private Label lblVelMedia;
    @FXML
    private Label lblRitmo;
    @FXML
    private Label lblDesnivelPos;
    @FXML
    private Label lblDesnivelNeg;
    @FXML
    private Label lblAltMin;
    @FXML
    private Label lblAltMax;
    @FXML
    private Button botonAltura;
    @FXML
    private LineChart<Number, Number> mapaAltura;
    @FXML
    private Label velLabel;
    
    List<GeoPoint> puntosRuta = null;
    
 

    // =========================================================
    //  MANEJADORES DE ZOOM
    // =========================================================

    /**
     * Aumenta el zoom en 0.1 unidades al pulsar el botón "+".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + 0.1);
    }

    /**
     * Reduce el zoom en 0.1 unidades al pulsar el botón "–".
     *
     * @param event evento de acción del botón
     */
    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal - 0.1);
    }

    /**
     * Aplica el factor de escala al {@code zoomGroup}.
     *
     * Este método es invocado automáticamente cada vez que cambia el
     * valor del slider, gracias al listener registrado en {@link #initialize}.
     *
     * Truco: guardamos y restauramos los valores de scroll para que el
     * contenido visible no salte al cambiar la escala.
     *
     * @param scaleValue nuevo factor de escala (p. ej. 1.2 → 120 %)
     */
    private void zoom(double scaleValue) {
        // Guardamos la posición del scroll antes de escalar
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        // Aplicamos el zoom escalando el Group en ambos ejes
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        // Restauramos la posición del scroll para que el centro visual
        // permanezca estable durante el zoom
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    
    /**
 * Refresca el MenuButton del usuario según el estado de sesión.
 * - Sin sesión: muestra "Identificarse" con opciones de login y registro.
 * - Con sesión: muestra el nickname con opciones de perfil, historial y logout.
 */
private void refreshUserMenu() {
    User u = app.getCurrentUser();
    menuUsuario.getItems().clear();
    if (u == null) {
        // ── Estado: NO HAY SESIÓN ─────────────────────────────────
        menuUsuario.setText("Identificarse");
        MenuItem miLogin    = new MenuItem("Iniciar sesión");
        MenuItem miRegister = new MenuItem("Registrarse");
        miLogin.setOnAction(e -> openLoginDialog());
        miRegister.setOnAction(e -> openRegisterDialog());
        menuUsuario.getItems().addAll(miLogin, miRegister);
        // Deshabilitamos botones que requieren sesión
        btnAddActividad.setDisable(true);
        btnActividadMensual.setDisable(true);
        map_listview.getItems().clear();
        clearStats();
    } else {
        // ── Estado: SESIÓN INICIADA ───────────────────────────────
        menuUsuario.setText(u.getNickName());
        MenuItem miPerfil    = new MenuItem("Modificar perfil");
        MenuItem miHistorial = new MenuItem("Historial de sesiones");
        MenuItem miLogout    = new MenuItem("Cerrar sesión");
        miPerfil.setOnAction(e -> openPerfilDialog());
        miHistorial.setOnAction(e -> openHistorialDialog());
        miLogout.setOnAction(e -> {
            app.logout();
            refreshUserMenu();   
        });
        menuUsuario.getItems().addAll(miPerfil, miHistorial, miLogout);
        // Habilitamos botones de sesión
        btnAddActividad.setDisable(false);
        btnActividadMensual.setDisable(false);
        cargarActividadesUsuario();
    }
}

    private void initListActividades() {
        map_listview.setCellFactory(lv -> new ListCell<Activity>() {
            @Override
            protected void updateItem(Activity a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    String fecha = a.getStartTime()
                    .atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                    setText(a.getName() + " . " + fecha);
                }
            }
        });
        
        //Ahora al seleccionar una actividad
        map_listview.getSelectionModel().selectedItemProperty().addListener((obs, oldAct, newAct) -> {
            if (newAct != null) {
                if (oldAct != null) mapPane.getChildren().subList(1, mapPane.getChildren().size()-1).clear();
                mostrarEstadisticas(newAct);
                pintarRutaEnMapa(newAct);
                pintarPerfilAltitud(newAct);
            } else {
                clearStats();
            }
        });
    }
    
    private void mostrarEstadisticas(Activity a) {
        lblDistancia.setText(String.format("%.2f km", a.getTotalDistance() / 1000.0));
        lblDuracion.setText(formatDuration(a.getDuration()));
        lblVelMedia.setText(String.format("%.1f km/h", a.getAverageSpeed()));
        lblRitmo.setText(String.format("%.2f min/km", a.getAveragePace()));
        lblDesnivelPos.setText(String.format("+%.0f m", a.getElevationGain()));
        lblDesnivelNeg.setText(String.format("-%.0f m", a.getElevationLoss()));
        lblAltMin.setText(String.format("%.0f m", a.getMinElevation()));
        lblAltMax.setText(String.format("%.0f m", a.getMaxElevation()));
    }

    //Métodos auxiliares
    private void openLoginDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Autentificarse/Iniciar Sesión.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar sesión");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshUserMenu();
        } catch (IOException ex) {
            showError("No se pudo abrir el diálogo de login.");
        }
    }

    private void openRegisterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Registrarse/Registrarse.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrarse");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshUserMenu();
        } catch (IOException ex) {
            showError("No se pudo abrir el diálogo de registro.");
        }
    }

    private void openPerfilDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modificar/Modificar.fxml"));
            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root, 400, 600);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modificar perfil");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshUserMenu();
        } catch (IOException ex) {
            showError("No se pudo abrir el diálogo de perfil.");
        }
    }

    private void openHistorialDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Historial/Historial.fxml"));
            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Historial de sesiones");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshUserMenu();
        } catch (IOException ex) {
            showError("No se pudo abrir el historial.");
        }
    }
    
    private void cargarActividadesUsuario() {
        map_listview.getItems().setAll(app.getUserActivities());
    }
    private void clearStats() {
        lblDistancia.setText("-");
        lblDuracion.setText("-");
        lblVelMedia.setText("-");
        lblRitmo.setText("-");
        lblDesnivelPos.setText("-");
        lblDesnivelNeg.setText("-");
        lblAltMin.setText("-");
        lblAltMax.setText("-");
    }
    
    private String formatDuration(Duration d) {
        long h = d.toHours();
        long m = d.toMinutesPart();
        long s = d.toSecondsPart();
        return String.format("%dh %02dm %02ds", h, m, s);
    }

    // =========================================================
    //  CONSTRUCCIÓN DEL MAPA
    // =========================================================

    /**
     * Carga una imagen y construye la jerarquía de nodos del mapa.
     *
     * Este método puede llamarse varias veces (p. ej. al cambiar el mapa),
     * ya que sustituye completamente el contenido del ScrollPane.
     *
     * @param imgFile fichero de imagen a cargar como fondo del mapa
     */
    private void buildMap(File imgFile) {
        // Comprobación defensiva: si el fichero no existe mostramos un aviso
        if (!imgFile.exists()) {
            map_scrollpane.setContent(
                new Label("Imagen no encontrada: " + imgFile.getPath()));
            return;
        }

        // Cargamos la imagen y obtenemos sus dimensiones reales en píxeles
        Image img = new Image(imgFile.toURI().toString());
        double W = img.getWidth();
        double H = img.getHeight();

        // ── mapPane: lienzo del mapa ───────────────────────────────────
        // Usamos un Pane (y no un Group) para poder posicionar los nodos
        // hijos con coordenadas absolutas (setLayoutX / setLayoutY).
        mapPane = new Pane();
        mapPane.setPrefSize(W, H); // tamaño preferido = tamaño de la imagen
        mapPane.setMinSize(W, H);  // impedimos que el layout lo encoja
        mapPane.setMaxSize(W, H);  // impedimos que el layout lo agrande

        // Añadimos la imagen como fondo del Pane
        ImageView iv = new ImageView(img);
        iv.setFitWidth(W);
        iv.setFitHeight(H);
        mapPane.getChildren().add(iv);

        // ── Manejador de clics sobre el mapa ──────────────────────────
        // Gestionamos el clic derecho (menú contextual) y el clic izquierdo
        // en modo inserción (FIX 2).
        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // Clic derecho → mostrar menú contextual
                onMapRightClick(e.getX(), e.getY());

            } else if (e.getButton() == MouseButton.PRIMARY && insertionMode) {
                // FIX 2: clic izquierdo en modo inserción → añadir POI y desactivar modo
                insertionMode = false;
                mapPane.setStyle(""); // Restauramos el cursor normal
                addPoi(e.getX(), e.getY());
            }
        });

        // ── Jerarquía de Groups para el zoom ──────────────────────────
        // contentGroup es el nodo raíz que recibe el ScrollPane.
        // zoomGroup es el que se escala; anidar un Group dentro de otro
        // evita que el ScrollPane reajuste su contenido durante el escalado.
        zoomGroup = new Group();
        Group contentGroup = new Group();
        zoomGroup.getChildren().add(mapPane);
        contentGroup.getChildren().add(zoomGroup);

        // Aplicamos el zoom actual (valor actual del slider)
        double zoom = zoom_slider.getValue();
        zoomGroup.setScaleX(zoom);
        zoomGroup.setScaleY(zoom);

        // Asignamos el contentGroup como contenido del ScrollPane
        map_scrollpane.setContent(contentGroup);

        
    }

    // =========================================================
    //  MENÚ CONTEXTUAL (clic derecho sobre el mapa)
    // =========================================================

    /**
     * Muestra el menú contextual reutilizable en la posición del clic.
     *
     * Las acciones de los MenuItem se actualizan con las coordenadas
     * del clic actual antes de mostrar el menú.
     *
     * @param x coordenada X del clic en el sistema local del mapPane
     * @param y coordenada Y del clic en el sistema local del mapPane
     */
    private void onMapRightClick(double x, double y) {
        // FIX 6: cerramos el menú si ya estaba visible (evita instancias flotantes)
        mapContextMenu.hide();

        // Actualizamos las acciones de los items con las coordenadas actuales.
        // Usamos variables final para que el lambda pueda capturarlas.
        final double clickX = x;
        final double clickY = y;
        mapContextMenu.getItems().get(0).setOnAction(e -> addAnnotation(clickX, clickY));

        // Mostramos el menú en coordenadas de pantalla
        mapContextMenu.show(
            mapPane.getScene().getWindow(),
            mapPane.localToScreen(x, y).getX(),
            mapPane.localToScreen(x, y).getY()
        );
    }

    // =========================================================
    //  INICIALIZACIÓN DEL CONTROLADOR
    // =========================================================

    /**
     * Método llamado automáticamente por el FXMLLoader tras inyectar
     * todos los elementos {@code @FXML}.
     *
     * Aquí configuramos:
     *  - El slider de zoom y su listener.
     *  - El ContextMenu reutilizable (FIX 6).
     *  - La CellFactory del ListView (FIX 4).
     *  - La carga del mapa inicial.
     *
     * @param url  URL del documento FXML (no usado aquí)
     * @param rb   paquete de recursos de internacionalización (no usado aquí)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initListActividades();
        clearStats();
        refreshUserMenu();
        
        // ── Configuración del slider de zoom ──────────────────────────
        zoom_slider.setMin(0.5);   // zoom mínimo: 50 %
        zoom_slider.setMax(1.5);   // zoom máximo: 150 %
        zoom_slider.setValue(1.0); // valor inicial: 100 %

        // Listener que invoca zoom() cada vez que el slider cambia de valor.
        // Usamos una expresión lambda en lugar de una clase anónima por brevedad.
        zoom_slider.valueProperty().addListener(
            (observable, oldVal, newVal) -> zoom((Double) newVal)
        );

        // Los items se crean aquí sin acción; las acciones se asignan
        // en onMapRightClick() con las coordenadas correctas de cada clic.
        MenuItem miAnnotation = new MenuItem("📋 Añadir anotación");
        mapContextMenu = new ContextMenu(miAnnotation);

               //  setCellFactory() define cómo se renderiza cada celda
        //  de forma independiente al modelo Poi.
        //  Aquí mostramos "CÓDIGO – Nombre" en cada fila.
        /*map_listview.setCellFactory(listView -> new ListCell<Poi>() {
            @Override
            protected void updateItem(Poi poi, boolean empty) {
                // Siempre llamar a super primero (requerido por JavaFX)
                super.updateItem(poi, empty);

                if (empty || poi == null) {
                    // Celda vacía: limpiamos texto y gráfico
                    setText(null);
                    setGraphic(null);
                } else {
                    // Mostramos código y nombre separados por un guión largo
                    setText(poi.getCode() + " – " + poi.getPosition());
                }
            }
        });*/

        // ── Carga del mapa inicial ─────────────────────────────────────
        // El fichero se busca relativo al directorio de trabajo del proyecto.
        buildMap(new File("maps/upv.jpg"));
        refreshUserMenu();
    }

    // =========================================================
    //  INDICADOR DE POSICIÓN DEL RATÓN
    // =========================================================

    /**
     * Actualiza la etiqueta {@code mousePosition} con las coordenadas
     * actuales del ratón, tanto en el sistema de la escena como en el
     * sistema local del nodo sobre el que se mueve.
     *
     * Útil para depuración y para que los alumnos comprendan la diferencia
     * entre coordenadas de escena y coordenadas locales.
     *
     * @param event evento de movimiento del ratón
     */
    @FXML
    private void showPosition(MouseEvent event) {
        mousePosition.setText(
            "sceneX: " + (int) event.getSceneX() +
            ", sceneY: " + (int) event.getSceneY() + "\n" +
            "         X: " + (int) event.getX() +
            ",          Y: " + (int) event.getY()
        );
        
        
        //Felicidades!!! has encontrado un easter egg
        //Este codigo no hace nada y por lo tanto, la Label que lo utilizaba se ha dejado invisible. Pero el codigo sigue ahi como un recordatorio de nuestro intento ;)
        if (puntosRuta == null) return;
        GeoPoint posActual = new GeoPoint(event.getSceneX(), event.getSceneY());
        if (puntosRuta.contains(posActual)) {
            /**Esto es probablemente la linea de codigo mas larga que he escrito en mi vida asique sientate que te intento explicar como funciona
            *Actualizo la etiqueta vel usando la velocidad entre el punto actual del ratón y el siguiente punto de la ruta
            *Podemos sacar el indice del punto actual en la List<TrackPoint> porque al definir la lista de Geopoints se hizo de forma que los indices coincidieran
            *Por lo que si hago un valueOf() del posActual me devuelve el indice donde en la List<TrackPoint> esta el punto donde puedo consultar la velocidad
            */
            velLabel.setText("vel: " + map_listview.getSelectionModel().getSelectedItem().getTrackPoints().get(puntosRuta.indexOf(posActual)).speedTo(map_listview.getSelectionModel().getSelectedItem().getTrackPoints().get(puntosRuta.indexOf(posActual)+1)));
        } else {
            velLabel.setText("vel:      ");
        }
        
    }

    // =========================================================
    //  DIÁLOGO "ACERCA DE"
    // =========================================================

    /**
     * Muestra un diálogo informativo con datos de la asignatura.
     *
     * Nota: accedemos al Stage del diálogo para poder personalizar
     * su icono, ya que Alert no expone directamente esa propiedad.
     *
     * @param event evento de acción del menú
     */
    @FXML
    private void about(ActionEvent event) {
        
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);

        // Personalizamos el icono de la ventana del diálogo
        Stage dialogStage = (Stage) mensaje.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(
            new Image(getClass().getResourceAsStream("/resources/logo.png"))
        );

        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("Creado por: \n"
                + "     Clara Lorena Zaharia Balán 🌟 \n"
                + "     Marcos Yerbes Martínez ✍️🔥🔥 \n"
                + "     Marta Bauzá Medrano 🎢\n"
                + "     Javier López Bellver 🙏");
        mensaje.showAndWait(); // Bloquea hasta que el usuario cierra el diálogo
        
//        // Prueba rapida Historial Versiones
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Historial/Historial.fxml"));
//            Parent root = (Parent) fxmlLoader.load(getClass().getResource("/Historial/Historial.fxml"));
//            Scene scene = new Scene(root, 600, 450);
//            Stage ventanaHistorial = new Stage();
//            ventanaHistorial.setScene(scene);
//            ventanaHistorial.setTitle("Historial de sesiones");
//            ventanaHistorial.initModality(Modality.APPLICATION_MODAL);
//            ventanaHistorial.showAndWait();        } catch (IOException e) {
//        };
     
    }

    // =========================================================
    //  AÑADIR UN POI (texto) AL MAPA
    // =========================================================

    /**
     * Muestra un diálogo para introducir el nombre del nuevo POI,
     * lo añade al ListView y dibuja su etiqueta sobre el mapa.
     *
     * @param x coordenada X del clic en el sistema local del mapPane
     * @param y coordenada Y del clic en el sistema local del mapPane
     */
    private void addPoi(double x, double y) {

        // ── Construcción del diálogo personalizado ────────────────────
        Dialog<Poi> poiDialog = new Dialog<>();
        poiDialog.setTitle("Nuevo POI");
        poiDialog.setHeaderText("Introduce un nuevo POI");

        // Personalizamos el icono de la ventana del diálogo
        Stage dialogStage = (Stage) poiDialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(
            new Image(getClass().getResourceAsStream("/resources/logo.png"))
        );

        // Botones del diálogo: Aceptar y Cancelar
        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        poiDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        // Campo de texto para el nombre del POI
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del POI");

        // Layout del contenido del diálogo (VBox con espaciado de 10 px)
        VBox vbox = new VBox(10, new Label("Nombre:"), nameField);
        poiDialog.getDialogPane().setContent(vbox);

        // ResultConverter: transforma la selección del botón en un objeto Poi.
        // FIX 1: ya no usamos coordenadas provisionales (0,0); pasamos (x,y)
        // directamente al constructor para que el modelo sea coherente desde el inicio.
        poiDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Poi(nameField.getText().trim(), x, y);
            }
            return null;
        });

        // Mostramos el diálogo y esperamos la respuesta del usuario
        Optional<Poi> result = poiDialog.showAndWait();

        if (result.isPresent()) {
            Poi poi = result.get();

            // FIX 1: confirmamos la posición como Point2D para compatibilidad
            // con getPosition(), usando las mismas coordenadas (x, y).
            poi.setPosition(new Point2D(x, y));

            // Añadimos el POI al ListView (la CellFactory mostrará nombre y código)
            //map_listview.getItems().add(poi);

            // FIX 1: usamos (x, y) tanto para el modelo como para el Text,
            // garantizando que la etiqueta aparezca exactamente donde se hizo clic.
            Label texto = new Label(poi.getCode());
            texto.setBackground(Background.fill(Color.WHITE));
            texto.setLayoutX(x);
            texto.setLayoutY(y);
            texto.setBorder(Border.stroke(Color.BLACK));
            texto.setPadding(new Insets(5, 5, 5, 5));
            texto.setOpacity(0.75);
            Text text = new Text(poi.getCode());
            text.setX(x);
            text.setY(y);
            mapPane.getChildren().add(texto);
        }
    }

    // =========================================================
    //  CAMBIAR EL MAPA (selector de fichero)
    // =========================================================

    /**
     * Abre un selector de fichero para que el usuario elija una imagen
     * diferente como mapa y reconstruye toda la vista.
     *
     * FIX 3: se comprueba que imgFile no sea null antes de usarlo,
     * evitando NullPointerException cuando el usuario cierra el FileChooser
     * sin seleccionar ningún fichero.
     *
     * @param event evento de acción del menú
     * @throws IOException si hay un problema al obtener la ruta canónica
     */
    @FXML
    private void cambiarMapa(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(".")); // Empezamos en el directorio del proyecto

        File imgFile = fc.showOpenDialog(zoom_slider.getScene().getWindow());

        // FIX 3: showOpenDialog() devuelve null si el usuario cancela la selección
        if (imgFile != null) {
            System.out.println("Mapa seleccionado: " + imgFile.getCanonicalPath());
            buildMap(imgFile); // Reconstruimos la vista con la nueva imagen
            map_listview.getItems().clear(); // Borramos los datos del mapa anterior
        }
    }


    // =========================================================
    //  AÑADIR UN CÍRCULO AL MAPA
    // =========================================================

    /**
     * Dibuja un círculo rojo de radio 10 px en la posición indicada.
     *
     * Ejemplo sencillo de cómo añadir formas vectoriales (Shape) sobre el mapa.
     * Los alumnos pueden extenderlo para:
     *  - Elegir color dinámicamente.
     *  - Asociar información al círculo (tooltip, popup, etc.).
     *  - Permitir moverlo con arrastrar y soltar (drag and drop).
     *
     * @param x coordenada X en el sistema local del mapPane
     * @param y coordenada Y en el sistema local del mapPane
     */
    private void addCircle(Annotation anot) {
        Circle circle = new Circle(10, Color.valueOf(anot.getColor())); // radio = 10 px, color = rojo
        double x = anot.getGeoPoints().get(0).getLatitude();
        double y = anot.getGeoPoints().get(0).getLongitude();
        
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.valueOf(anot.getColor()));
        circle.setStrokeWidth(3);
        
        Label texto = crearLabelAnn(x, y, anot);
        texto.setVisible(false);
        mapPane.getChildren().add(texto);
        
        circle.setOnMouseClicked(event -> {
                texto.setVisible(!texto.visibleProperty().get());
            }
        );
        mapPane.getChildren().add(circle); // Se añade sobre el mapa como cualquier nodo
    }
    
    /**
     * Dibuja unn punto en las coordenadas con el color marcado en la anotación
     * 
     * @param x: coordenada x del click de ratón en el mapPane
     * @param y: coordenada y del click de ratón en en mapPane
     * @param anot: anotación que contiene todos los datos sobre la linea
     */
    private void addPoint(Annotation anot) {
        Circle punto = new Circle(5, Color.valueOf(anot.getColor())); // radio = 10 px, color = rojo
        double x = anot.getGeoPoints().get(0).getLatitude();
        double y = anot.getGeoPoints().get(0).getLongitude();
        
        punto.setCenterX(anot.getGeoPoints().get(0).getLatitude());
        punto.setCenterY(anot.getGeoPoints().get(0).getLongitude());
        
        Label texto = crearLabelAnn(x, y, anot);
        texto.setVisible(false);
        mapPane.getChildren().add(texto);
        
        punto.setOnMouseClicked(event -> {
                texto.setVisible(!texto.visibleProperty().get());
            }
        );
        mapPane.getChildren().add(punto); // Se añade sobre el mapa como cualquier nodo
    }

    /**
     * Dibuja una linea horizontal de 20px de longitud con el color marcado en la anotación
     * 
     * @param x: coordenada x del click de ratón en el mapPane
     * @param y: coordenada y del click de ratón en en mapPane
     * @param anot: anotación que contiene todos los datos sobre la linea
     */
    private void addLine(Annotation anot) {
        Line linea = new Line();
        double x1 = anot.getGeoPoints().get(0).getLatitude();
        double y1 = anot.getGeoPoints().get(0).getLongitude();
        double x2 = anot.getGeoPoints().get(1).getLatitude();
        double y2 = anot.getGeoPoints().get(1).getLongitude();
        
        linea.setStartX(x1);
        linea.setStartY(y1);
        linea.setEndX(x2);
        linea.setEndY(y2);
        linea.setStroke(Color.valueOf(anot.getColor()));
        
        Label texto = crearLabelAnn(x2, y2, anot);
        texto.setVisible(false);
        mapPane.getChildren().add(texto);
        
        linea.setOnMouseClicked(event -> {
                texto.setVisible(!texto.visibleProperty().get());
            }
        );
        mapPane.getChildren().add(linea);
    }
    
    /**
     * Dibuja un texto sobre el mapa con los parametros de la anotación
     * 
     * @param x: coordenada x del click de ratón en el mapPane
     * @param y: coordenada y del click de ratón en en mapPane
     * @param anot: anotación que contiene todos los datos sobre la linea
     */
    private void addText(Annotation anot) {
        double x = anot.getGeoPoints().get(0).getLatitude();
        double y = anot.getGeoPoints().get(0).getLongitude();
        
        Poi punto = new Poi(anot.getText(), x, y);
        punto.setPosition(new Point2D(x, y));
        
        Label texto = crearLabelAnn(x, y, anot);
        texto.setTextFill(Color.valueOf(anot.getColor()));
        mapPane.getChildren().add(texto);
        
    }
    
    /**
     * Crea una ventana de Anotaciones para que le usuario cree su anotación
     * y la añade al mapa en la posición elegida. 
     * 
     * @param x: coordenada x del click de ratón en el mapPane
     * @param y: coordenada y del click de ratón en el mapPane
     */
    private void addAnnotation(double x, double y) {
        try {
            SportActivityApp app = SportActivityApp.getInstance();
            if (app.getCurrentUser() == null || map_listview.getSelectionModel().getSelectedItem() == null) {
                Alert notLogged = new Alert(Alert.AlertType.ERROR, "No puedes añadir actividades sin identificarte y seleccionar una actividad", ButtonType.CLOSE);
                notLogged.setHeaderText("Problema al añadir anotación");
                notLogged.showAndWait();
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Anotaciones/Anotaciones.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage ventanaAnotación = new Stage();
            ventanaAnotación.setScene(scene);
            ventanaAnotación.setTitle("Nueva anotación");
            ventanaAnotación.initModality(Modality.APPLICATION_MODAL);
            AnotacionesController anot = fxmlLoader.getController();
            anot.setGeoPoint(new GeoPoint(x, y));
            ventanaAnotación.showAndWait();
            
            
            Annotation anotacionActual = anot.getAnnotation();
            app.addAnnotation(map_listview.getSelectionModel().getSelectedItem(), anotacionActual);
            switch(anotacionActual.getType()) {
                case AnnotationType.CIRCLE: addCircle(anotacionActual); break;
                case AnnotationType.POINT: addPoint(anotacionActual); break;
                case AnnotationType.LINE: addLine(anotacionActual); break;
                case AnnotationType.TEXT: addText(anotacionActual); break;
            }
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Crea una label en las coordenadas indicadas usando los parrámetros de la anotación
     * !!! Método puramente de utilidad, solo lo uso para no repetir mucho codigo
     * 
     * @param x: posición x del click de ratón
     * @param y: posición y del click de ratón
     * @param anot: anotación que contiene todos los datos
     * @return anotación creada
     */
    private Label crearLabelAnn(double x, double y, Annotation anot) {
        Label texto = new Label(anot.getText());
        texto.setBackground(Background.fill(Color.WHITE));
        texto.setLayoutX(x);
        texto.setLayoutY(y);
        texto.setBorder(Border.stroke(Color.BLACK));
        texto.setPadding(new Insets(5, 5, 5, 5));
        texto.setOpacity(0.75);
        return texto;
    } 

    @FXML
    private void onAddActividad(ActionEvent event) {
        FileChooser fc = new FileChooser();
    fc.setTitle("Selecciona un fichero GPX");
    fc.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Ficheros GPX", "*.gpx"));
    fc.setInitialDirectory(new File("."));

    File gpx = fc.showOpenDialog(btnAddActividad.getScene().getWindow());
    if (gpx == null) return;

    try {
        Activity nueva = app.importActivity(gpx);
        if (nueva != null) {
            map_listview.getItems().add(0, nueva);
            map_listview.getSelectionModel().select(nueva);
        } else {
            showError("No se pudo importar el fichero GPX.");
        }
    } catch (Exception ex) {
        showError("Error al procesar el GPX: " + ex.getMessage());
    }
    }
    
    private void showError(String msg) {
    Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
    a.setHeaderText(null);
    a.showAndWait();
}

    @FXML
    private void onActividadMensual(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ActividadMensual/ActividadMensual.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Actividad mensual");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    } catch (IOException ex) {
        showError("No se pudo abrir la actividad mensual.");
    }
    }

    @FXML
    private void mostrarMapaAltura(ActionEvent event) {
        mapaAltura.setVisible(!mapaAltura.visibleProperty().get());
        if (mapaAltura.getHeight() < 50) { mapaAltura.setPrefHeight(200); } else mapaAltura.setPrefHeight(1);

    }
    
    private void pintarRutaEnMapa(Activity actividadActual) {
    MapRegion region = actividadActual.getSuggestedMap();
    System.out.println("Imagen mapa: " + region.getImagePath());
    Image img = new Image(new File(region.getImagePath()).toURI().toString());
    System.out.println("Tamaño imagen: " + img.getWidth() + "x" + img.getHeight());
    MapProjection proj = new MapProjection(region, img.getWidth(), img.getHeight());

    Polyline ruta = new Polyline();
    for (TrackPoint tp : actividadActual.getTrackPoints()) {
        Point2D p = proj.project(tp);
        ruta.getPoints().addAll(p.getX(), p.getY());
    }
    System.out.println("Total puntos ruta: " + ruta.getPoints().size());
    System.out.println("Tamaño mapPane: " + mapPane.getWidth() + "x" + mapPane.getHeight());
    mapPane.getChildren().add(ruta);

    List<Annotation> anotaciones = actividadActual.getAnnotations();
    for (Annotation actual : anotaciones) {
        switch(actual.getType()) {
            case AnnotationType.CIRCLE: addCircle(actual); break;
            case AnnotationType.POINT: addPoint(actual); break;
            case AnnotationType.LINE: addLine(actual); break;
            case AnnotationType.TEXT: addText(actual); break;
        }
    }

    //Felicidades!!! Has encontrado la segunda parte del Easter egg
    //Este codigo se usaba para definir la lista que se utilizaba en showPosition, pero ahora está aqui cogiendo polvo D:
    puntosRuta = new java.util.ArrayList<>();
    for (TrackPoint tp : actividadActual.getTrackPoints()) {
        puntosRuta.add(new GeoPoint(
            (int) tp.getLatitude(),
            (int) tp.getLongitude()
        ));
    }
}
    
    private void pintarPerfilAltitud(Activity actividadActual) {
    mapaAltura.getData().clear();
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    List<TrackPoint> puntos = actividadActual.getTrackPoints();
    for (int i = 0; i < puntos.size(); i++) {
        series.getData().add(new XYChart.Data<>(i, puntos.get(i).getElevation()));
    }
    mapaAltura.getData().add(series);
}
}
