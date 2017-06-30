package com.example.eider.bracadatabase;

/**
 * Created by Eider on 05/04/2017.
 */

public class metodosDB {
    private static final String NOMBRETBLCONTACTOS = "tblContactos";
    private static final String CAMPO_ID= "idContacto";
    private static final String CAMPO_NOMBRE = "nombreContacto";
    private static final String CAMPO_TELEFONO = "telefonoContacto";
    private static final String CAMPO_EMAIL= "emailContacto";
    public static final String QUERYCREARTBLCONTACTOS = "CREATE TABLE "+NOMBRETBLCONTACTOS+"( "+
            CAMPO_ID+"INTEGER PRIMARY KEY AUTOINCREMENT, "+
            CAMPO_TELEFONO+" TEXT NOT NULL, "+
            CAMPO_NOMBRE+" TEXT NOT NULL, "+
            CAMPO_EMAIL+"TEXT);";

    public metodosDB() {
    }

    private int id;
    private String nombre;
    private String telefono;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
