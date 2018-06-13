package thomas.jansen.plantbase;

import java.io.Serializable;
import java.util.Date;

public class MyPlant implements Serializable{

    private String name;
    private String latinName;
    private float maxTemp;
    private float maxpH;
    private float minTemp;
    private float minpH;
    private float watering;
    private Date startdate;
    private boolean alive;
    private boolean connected;
    private String arduinoName;
    private int waternotify;

    public MyPlant(){}

    public MyPlant(String name, String latinName, float maxTemp, float maxpH, float minTemp, float minpH,
                   float watering, Date startdate, boolean alive, boolean connected, String arduinoName, int waternotify) {
        this.name = name;
        this.latinName = latinName;
        this.maxTemp = maxTemp;
        this.maxpH = maxpH;
        this.minTemp = minTemp;
        this.minpH = minpH;
        this.watering = watering;
        this.startdate = startdate;
        this.alive = alive;
        this.connected = connected;
        this.arduinoName = arduinoName;
        this.waternotify = waternotify;
    }

    public int getWaternotify() {
        return waternotify;
    }

    public void setWaternotify(int waternotify) {
        this.waternotify = waternotify;
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

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getArduinoName() {
        return arduinoName;
    }

    public void setArduinoName(String arduinoName) {
        this.arduinoName = arduinoName;
    }
}
