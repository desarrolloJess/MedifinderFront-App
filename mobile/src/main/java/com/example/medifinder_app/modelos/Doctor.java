package com.example.medifinder_app.modelos;

public class Doctor {
    private String nombre;
    private String especialidad;
    private String direccion;

    public Doctor(String nombre, String especialidad, String direccion) {
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
