package sossense.datubasea;

public class SensorLayout {

    private final String id;
    private final int x;
    private final int y;
    private final String ubicacion;

    public SensorLayout(String id, int x, int y, String ubicacion) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ubicacion = ubicacion;
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

    public String getUbicacion() {
        return ubicacion;
    }
}
