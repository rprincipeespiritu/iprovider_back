package com.incloud.hcp.dto.graficos;

public class DataGrafico {

    private String indicador;
    private String measure1;
    private String dim0;
    private String dim1;
    private Integer id;

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public String getMeasure1() {
        return measure1;
    }

    public void setMeasure1(String measure1) {
        this.measure1 = measure1;
    }

    public String getDim0() {
        return dim0;
    }

    public void setDim0(String dim0) {
        this.dim0 = dim0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDim1() {
        return dim1;
    }

    public void setDim1(String dim1) {
        this.dim1 = dim1;
    }


    @Override
    public String toString() {
        return "DataGrafico{" +
                "indicador='" + indicador + '\'' +
                ", measure1='" + measure1 + '\'' +
                ", dim0='" + dim0 + '\'' +
                ", id=" + id +
                ", dim1='" + dim1 + '\'' +
                '}';
    }
}
