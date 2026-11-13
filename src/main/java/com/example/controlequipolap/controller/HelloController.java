package com.example.controlequipolap.controller;

import com.example.controlequipolap.dao.EquipoDAO;
import com.example.controlequipolap.dao.TipoEquipoDAO;
import com.example.controlequipolap.model.Equipo;
import com.example.controlequipolap.model.TipoEquipo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HelloController {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<TipoEquipo> cmbTipoEquipo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<TipoEquipo> cmbFiltroTipo;
    @FXML private TableView<Equipo> tablaEquipos;
    @FXML private TableColumn<Equipo, Integer> colId;
    @FXML private TableColumn<Equipo, String> colCodigo;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, Integer> colCantidad;
    @FXML private TableColumn<Equipo, String> colTipo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiarFiltros;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final TipoEquipoDAO tipoDAO = new TipoEquipoDAO();
    private Integer idSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipo.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue().getTipoEquipo().getNombre()));
        cmbEstado.getItems().addAll(
                "Disponible", "En mantenimiento", "Fuera de servicio");
        tablaEquipos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        seleccionarEquipo(seleccionado);
                    } else {
                        idSeleccionado = null;
                    }
                });
        try {
            cargarTipos();
            cargarEquipos();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void cargarTipos() {
        TipoEquipo filtroAnterior = cmbFiltroTipo.getValue();
        List<TipoEquipo> tipos = tipoDAO.listar();
        cmbTipoEquipo.setItems(FXCollections.observableArrayList(tipos));
        cmbFiltroTipo.setItems(FXCollections.observableArrayList(tipos));
        if (filtroAnterior != null) {
            for (TipoEquipo tipo : tipos) {
                if (tipo.getId().equals(filtroAnterior.getId())) {
                    cmbFiltroTipo.setValue(tipo);
                    break;
                }
            }
        }
    }

    private void cargarEquipos() {
        List<Equipo> equipos = equipoDAO.buscar(txtBuscar.getText(), cmbFiltroTipo.getValue());
        limpiarCampos();
        tablaEquipos.setItems(FXCollections.observableArrayList(equipos));
    }

    private void seleccionarEquipo(Equipo equipo) {
        idSeleccionado = equipo.getId();
        txtCodigo.setText(equipo.getCodigo());
        txtNombre.setText(equipo.getNombre());
        txtMarca.setText(equipo.getMarca());
        txtCantidad.setText(String.valueOf(equipo.getCantidad()));
        cmbEstado.setValue(equipo.getEstado());
        // Buscamos el mismo tipo en la lista del ComboBox.
        for (TipoEquipo tipo : cmbTipoEquipo.getItems()) {
            if (tipo.getId().equals(equipo.getTipoEquipo().getId())) {
                cmbTipoEquipo.setValue(tipo);
                break;
            }
        }
    }

    private Equipo obtenerEquipoFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty()) {
            throw new IllegalArgumentException("El codigo y el nombre son obligatorios.");
        }
        if (codigo.length() > 30 || nombre.length() > 100 || marca.length() > 100) {
            throw new IllegalArgumentException("Maximos: codigo 30, nombre 100 y marca 100 caracteres.");
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La cantidad debe ser un numero entero valido.");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        if (cmbTipoEquipo.getValue() == null) {
            throw new IllegalArgumentException("Selecciona el tipo de equipo.");
        }
        String estado = cmbEstado.getValue();
        if (!"Disponible".equals(estado) && !"En mantenimiento".equals(estado)
                && !"Fuera de servicio".equals(estado)) {
            throw new IllegalArgumentException("Selecciona un estado permitido.");
        }
        Equipo equipo = new Equipo();
        equipo.setId(idSeleccionado);
        equipo.setCodigo(codigo);
        equipo.setNombre(nombre);
        equipo.setMarca(marca);
        equipo.setCantidad(cantidad);
        equipo.setEstado(estado);
        equipo.setTipoEquipo(cmbTipoEquipo.getValue());
        return equipo;
    }

    @FXML
    private void guardarEquipo() {
        try {
            Equipo equipo = obtenerEquipoFormulario();
            equipo.setId(null); // El ID nuevo lo genera PostgreSQL.
            equipoDAO.guardar(equipo);
        } catch (Exception ex) {
            mostrarError(ex);
            return;
        }
        despuesDeGuardar("Equipo guardado correctamente.");
    }

    @FXML
    private void actualizarEquipo() {
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un equipo para actualizar.");
            return;
        }
        try {
            equipoDAO.actualizar(obtenerEquipoFormulario());
        } catch (Exception ex) {
            mostrarError(ex);
            return;
        }
        despuesDeGuardar("Equipo actualizado correctamente.");
    }

    @FXML
    private void eliminarEquipo() {
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un equipo para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar equipo");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar el equipo " + txtCodigo.getText() + "?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        try {
            equipoDAO.eliminar(idSeleccionado);
        } catch (Exception ex) {
            mostrarError(ex);
            return;
        }
        despuesDeGuardar("Equipo eliminado correctamente.");
    }

    private void despuesDeGuardar(String mensaje) {
        // Este metodo solo se llama cuando el DAO ya confirmo el commit.
        limpiarCampos();
        try {
            cargarEquipos();
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    mensaje + " No se pudo recargar la tabla. Intenta Buscar de nuevo.");
            return;
        }
        mostrarAlerta(Alert.AlertType.INFORMATION, mensaje);
    }

    @FXML
    private void buscarEquipos() {
        try {
            // Permite recuperar la conexion si PostgreSQL estaba apagado al iniciar.
            cargarTipos();
            cargarEquipos();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    @FXML
    private void limpiarCampos() {
        idSeleccionado = null;
        tablaEquipos.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();
        cmbTipoEquipo.setValue(null);
        cmbEstado.setValue(null);
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroTipo.setValue(null);
        buscarEquipos();
    }

    private void mostrarError(Exception ex) {
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            mostrarAlerta(Alert.AlertType.WARNING, ex.getMessage());
            return;
        }
        // Hibernate envuelve el error de PostgreSQL; buscamos la causa.
        Throwable causa = ex;
        while (causa != null) {
            if (causa instanceof SQLException) {
                String estado = ((SQLException) causa).getSQLState();
                if ("23505".equals(estado)) {
                    mostrarAlerta(Alert.AlertType.ERROR, "El codigo ya existe. Usa otro codigo.");
                    return;
                }
                if ("23514".equals(estado) || "23503".equals(estado) || "23502".equals(estado)) {
                    mostrarAlerta(Alert.AlertType.ERROR,
                            "La base de datos rechazo los datos. Revisa cantidad, estado y tipo.");
                    return;
                }
            }
            causa = causa.getCause();
        }
        mostrarAlerta(Alert.AlertType.ERROR,
                "No se pudo completar la operacion. Revisa PostgreSQL, la conexion y DB_PASSWORD.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Control de equipos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
