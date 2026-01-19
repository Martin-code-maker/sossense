package sossense.datubasea;

import java.awt.*;

public class SensorPlano {

    private String id;
    private int x;
    private int y;
    private int nivelHumo;
    private String ubicacion;

    public SensorPlano(String id, int x, int y, String ubicacion) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ubicacion = ubicacion;
        this.nivelHumo = 0;
    }

    public Color getColor() {
        if (nivelHumo >= 70)
            return Color.RED;
        if (nivelHumo >= 30)
            return Color.ORANGE;
        return Color.GREEN;
    }

    public String getInfo() {
        return "Sensor " + id + ": " + nivelHumo + "% humo - " + ubicacion;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNivelHumo() {
        return nivelHumo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setNivelHumo(int nivelHumo) {
        this.nivelHumo = Math.min(100, Math.max(0, nivelHumo));
    }

}
