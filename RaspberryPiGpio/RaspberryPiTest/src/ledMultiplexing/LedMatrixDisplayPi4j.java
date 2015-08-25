package ledMultiplexing;

import java.util.LinkedList;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class LedMatrixDisplayPi4j extends LedMatrixDisplayBase{
    private GpioController gpio;
    
    private GpioPinDigitalOutput[] colOutPins;
    private GpioPinDigitalOutput[] rowOutPins;
    
    //constructors
    public LedMatrixDisplayPi4j(Pin[] colPins, Pin[] rowPins, double targetFrameRate, boolean verbose) {
        super(colPins, rowPins, targetFrameRate, verbose);
    }

    public LedMatrixDisplayPi4j(Pin[] colPins, Pin[] rowPins, double frameRate) {
        this(colPins, rowPins, frameRate, false);
    }

    public LedMatrixDisplayPi4j(Pin[] colPins, Pin[] rowPins) {
        this(colPins, rowPins, DEFAULT_FRAMERATE);
    }
    //implemented methods
    @Override
    public int getWidth() {
        return colOutPins.length;
    }
    
    @Override    
    public int getHeight() {
        return rowOutPins.length;
    }
    
    @Override
    public void draw() {        
        while (isAlive) {            
            for (int x = 0; x < bitmap.length; x++) {
                //pull colPin to GND
                colOutPins[x].low();
                if (verbose) System.out.println(colOutPins[x].getName() + " turned on! Pin: " + colOutPins[x].getPin() + ", State: " + colOutPins[x].getState());
                
                LinkedList<GpioPinDigitalOutput> switchPinsList = new LinkedList<GpioPinDigitalOutput>();
                for (int y = 0; y < bitmap[0].length; y++) {
                    if (bitmap[y][x]) {
                        //set rowPin high
                        //rowOutPins[y].high();
                        //if (verbose) System.out.println(rowOutPins[y].getName() + " turned on! Pin: " + rowOutPins[y].getPin() + ", State: " + rowOutPins[y].getState());
                        
                        //add pins to list
                        switchPinsList.add(rowOutPins[y]);
                    }
                }
                
                //create array of pins to switch
                GpioPinDigitalOutput[] switchPinsArray = switchPinsList.toArray(new GpioPinDigitalOutput[switchPinsList.size()]);
                
                //switch pins on
                if (switchPinsArray.length > 0) {
                    gpio.setState(PinState.HIGH, switchPinsArray);
                    
                    //wait
                    try {
                        if (verbose) System.out.println("Sleeping for " + scanDelay + " ms...");
                        Thread.sleep(scanDelay);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
    
                    //switch pins off
                    gpio.setState(PinState.LOW, switchPinsArray);
                }
                
                //push rowPin to HIGH
                colOutPins[x].high();
                
                if (verbose) System.out.println("=== RESET FOR NEXT SCAN ===");
            }
    
            synchronized (this) {
                framesCount++;
                lastFrameTimeStamp = System.currentTimeMillis();
            }
        }
    }
    
    @Override
    protected void initGpio(Pin[] colPins, Pin[] rowPins) {
        gpio = GpioFactory.getInstance();
        
        rowOutPins = new GpioPinDigitalOutput[rowPins.length];
        colOutPins = new GpioPinDigitalOutput[colPins.length];
        
        for (int i = 0; i < rowPins.length; i++) {
            rowOutPins[i] = gpio.provisionDigitalOutputPin(rowPins[i], "Row " + i, PinState.LOW);
            rowOutPins[i].setShutdownOptions(true, PinState.LOW);
        }
        
        for (int i = 0; i < colPins.length; i++) {
            colOutPins[i] = gpio.provisionDigitalOutputPin(colPins[i], "Col " + i, PinState.HIGH);
            colOutPins[i].setShutdownOptions(true, PinState.LOW);
        }        
    }
}
