package thomas.jansen.plantbase;

import java.io.Serializable;

public class Plant implements Serializable{

    private String name;
    private String latinName;
    private float maxTemp;
    private float maxpH;
    private float minTemp;
    private float minpH;
    private float watering;

    public Plant() {}

    public Plant(String name, String latinName, float maxTemp, float maxpH, float minTemp, float minpH, float watering) {
        this.name = name;
        this.latinName = latinName;
        this.maxTemp = maxTemp;
        this.maxpH = maxpH;
        this.minTemp = minTemp;
        this.minpH = minpH;
        this.watering = watering;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMaxpH() {
        return maxpH;
    }

    public void setMaxpH(float maxpH) {
        this.maxpH = maxpH;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMinpH() {
        return minpH;
    }

    public void setMinpH(float minpH) {
        this.minpH = minpH;
    }

    public float getWatering() {
        return watering;
    }

    public void setWatering(float watering) {
        this.watering = watering;
    }
}
