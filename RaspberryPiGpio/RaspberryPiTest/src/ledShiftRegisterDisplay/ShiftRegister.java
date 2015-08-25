package ledShiftRegisterDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinDirection;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.event.PinEventType;

public class ShiftRegister {
    private GpioPinDigitalOutput dataPin;
    private GpioPinDigitalOutput shiftPin;
    private GpioPinDigitalOutput storePin;
    
    private long delay;   
    
    public ShiftRegister(Pin dataPinId, Pin shiftPinId, Pin storePinId, long delay) {
        this.delay = delay;
        
        //create gpio controller
        GpioController gpio = GpioFactory.getInstance();
        
        //init pins
        dataPin = gpio.provisionDigitalOutputPin(dataPinId, "DataPin", PinState.LOW);
        shiftPin = gpio.provisionDigitalOutputPin(shiftPinId, "ShiftPin", PinState.LOW);
        storePin = gpio.provisionDigitalOutputPin(storePinId, "StorePin", PinState.LOW);
        
        //set shutdown state for this pin
        dataPin.setShutdownOptions(true, PinState.LOW);
        shiftPin.setShutdownOptions(true, PinState.LOW);
        storePin.setShutdownOptions(true, PinState.LOW);
    }
    
    public void shiftInBit(boolean b) {
        if (b) {
            dataPin.high();
        }
        
        shiftPin.high();
        
        sleep();

        shiftPin.low();
        
        sleep();
        
        dataPin.low();
    }
    
    public void shiftInBits(boolean[] bArray) {
        for (boolean b : bArray) {
            shiftInBit(b);
        }
    }
    
    public void store() {
        sleep();
        storePin.high();
        sleep();
        storePin.low();
    }
    
    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
