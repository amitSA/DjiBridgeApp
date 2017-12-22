package test.com.bridge;

/**
 * Created by Amit on 7/11/2017.
 */

/**
 * This class just represents a compound data-structure that's only purpose is to
 * hold 3 values: voltage, current, and temperature
 */
public class BatteryTuple {
    private int voltage;
    private int current;
    private float temperature;

    /**
     * Constructor to create a BatteryTuple object with its three fields initialized to
     * the correlating arguments
     * @param v voltage
     * @param c current
     * @param t temperature
     */
    public BatteryTuple(int v, int c, float t){
        this.voltage = v;
        this.current = c;
        this.temperature = t;
    }
    /**
     * Constructor to create a BatteryTuple object with 0 initialized to all its fields
     */
    public BatteryTuple(){
        voltage = 0;
        current = 0;
        temperature = 0;
    }

    //The following 6 methods are getters and setters for all of a BatteryTuple's fields

    /**
     * Get voltage value
     * @return voltage
     */
    public int getVoltage(){
        return voltage;
    }

    /**
     * Get current value
     * @return current
     */
    public int getCurrent(){
        return current;
    }

    /**
     * Get temperature
     * @return temperature
     */
    public float getTemperature(){
        return temperature;
    }

    /**
     * Set a new voltage
     * @param v the new voltage to set
     * @return this BatteryTuple instance
     */
    public BatteryTuple setVoltage(int v){
        voltage = v;
        return this;
    }
    /**
     * Set a new current
     * @param c the new current to set
     * @return this BatteryTuple instance
     */
    public BatteryTuple setCurrent(int c){
        current = c;
        return this;
    }
    /**
     * Set a new temperature
     * @param t the new temperature to set
     * @return this BatteryTuple instance
     */
    public BatteryTuple setTemperature(float t){
        temperature = t;
        return this;
    }
}
