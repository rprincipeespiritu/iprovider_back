package com.incloud.hcp.dto.graficos;

public class DataLabel {

    private Boolean visible = true;
    private Boolean hideWhenOverlap = true;

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getHideWhenOverlap() {
        return hideWhenOverlap;
    }

    public void setHideWhenOverlap(Boolean hideWhenOverlap) {
        this.hideWhenOverlap = hideWhenOverlap;
    }

    @Override
    public String toString() {
        return "DataLabel{" +
                "visible=" + visible +
                ", hideWhenOverlap=" + hideWhenOverlap +
                '}';
    }
}
