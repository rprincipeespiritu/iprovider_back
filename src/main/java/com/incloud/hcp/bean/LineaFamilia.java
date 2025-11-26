package com.incloud.hcp.bean;

/**
 * Created by Administrador on 28/08/2017.
 */
public class LineaFamilia {
    private int id;
    private String descripcion;
    private String indBien;

    public LineaFamilia() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIndBien() {
        return indBien;
    }

    public void setIndBien(String indBien) {
        this.indBien = indBien;
    }

    @Override
    public String toString() {
        return "LineaFamilia{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", indBien='" + indBien + '\'' +
                '}';
    }
}
