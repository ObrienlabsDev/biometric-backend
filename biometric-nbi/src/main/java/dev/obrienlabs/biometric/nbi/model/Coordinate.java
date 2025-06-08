package dev.obrienlabs.biometric.nbi.model;

public class Coordinate {
    private Double lattitude;
    private Double longitude;

    public Coordinate(Double lattitude, Double longitude) {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public Coordinate() {}

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}