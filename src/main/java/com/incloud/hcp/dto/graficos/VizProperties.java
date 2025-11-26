package com.incloud.hcp.dto.graficos;

public class VizProperties {

    private DataLabel dataLabel;

    public DataLabel getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(DataLabel dataLabel) {
        this.dataLabel = dataLabel;
    }

    @Override
    public String toString() {
        return "VizProperties{" +
                "dataLabel=" + dataLabel +
                '}';
    }


}
