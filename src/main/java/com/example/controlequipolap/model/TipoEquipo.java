package com.example.controlequipolap.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_equipo", schema = "inventario")
public class TipoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "text")
    private String descripcion;

    public TipoEquipo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // El ComboBox muestra el nombre del tipo.
    @Override
    public String toString() {
        return nombre;
    }
}
