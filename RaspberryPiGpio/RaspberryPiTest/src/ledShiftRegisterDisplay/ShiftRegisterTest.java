package ledShiftRegisterDisplay;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
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

public class ShiftRegisterTest {
    
    
    public static void main(String[] args) {
        //System.out.println("ShiftRegister-Example started!");
        long delayBetweenFrames = 0;
        long delayBetweenShiftOps = 0;
        
        ShiftRegister sr = new ShiftRegister(RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_04, delayBetweenShiftOps);
        
        /*boolean[] b = new boolean[16];
        
        for (int i = 0; i < b.length; i++) {
            b[i] = true;
        }*/
        
        boolean[] b0 = new boolean[] {
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false
        };
        
        boolean[] b1 = new boolean[] {
                true, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false
        };
        
        boolean[] b2 = new boolean[] {
                false, true, false, false,
                true, true, false, false,
                false, false, false, false,
                false, false, false, false
        };
        
        boolean[] b3 = new boolean[] {
                false, false, true, false,
                false, false, true, false,
                true, true, true, false,
                false, false, false, false
        };
        
        boolean[] b4 = new boolean[] {
                false, false, false, true,
                false, false, false, true,
                false, false, false, true,
                true, true, true, true
        };
        
        boolean[][] bs = new boolean[][] {b0, b1, b2, b3, b4};
        
        while(true) {
            for (boolean[] b : bs) {
                sr.shiftInBits(b);
                //System.out.println("ShiftRegister loaded!");
                
                sr.store();
                //System.out.println("Bits should show! (Sleeping for " + delayBetweenFrames + " milliseconds)"); 
                
                sleep(delayBetweenFrames);
            }
        }
        
        //System.out.println("Example terminates!");
    }
    
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
