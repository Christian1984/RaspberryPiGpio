package ledMultiplexing;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class LedMatrixBenchmark {
    public static void main(String args[]) {
        try {
            Pin[] rowPins = new Pin[] {RaspiPin.GPIO_08, RaspiPin.GPIO_09, RaspiPin.GPIO_07, RaspiPin.GPIO_00};
            Pin[] colPins = new Pin[] {RaspiPin.GPIO_13, RaspiPin.GPIO_12, RaspiPin.GPIO_03, RaspiPin.GPIO_02};
            
            /*
             * - X - -
             * X X X X
             * - X - -
             * X X X X
             */
            boolean[][] bitmap = new boolean [][] {
                    {false, true, false, false}, 
                    {true, true, true, true}, 
                    {false, true, false, false}, 
                    {true, true, true, true}};
            
            String s = "";
            System.out.println("Starting Benchmark... Stand by!");
            
            LedMatrixDisplayBase display = new LedMatrixDisplayPi4j(colPins, rowPins, 1d, false);
            display.setOn();
            Thread.sleep(10000);
            s += "All on, 1 fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setBitmap(bitmap);
            Thread.sleep(10000);
            s += "Bitmap, 1 fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setVerbose(false);
            display.setFramerate(10d);
            Thread.sleep(10000);
            s += "Bitmap, 10 fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setFramerate(LedMatrixDisplayBase.DEFAULT_FRAMERATE);
            Thread.sleep(10000);
            s += "Bitmap, 30 fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setFramerate(60d);
            Thread.sleep(10000);
            s += "Bitmap, 30 fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setFramerate(120d);
            Thread.sleep(10000);
            s += "Bitmap, 120 fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setOn();
            Thread.sleep(10000);
            s += "All on, 120 fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setFramerate(Double.POSITIVE_INFINITY);
            display.setOn();
            Thread.sleep(10000);
            s += "All on, inf fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            display.setOff();
            Thread.sleep(10000);
            s += "All Off, inf fps: \n" + display.getPerformanceStats() + "\n==================\n";
            
            System.out.println(s + "DONE!!!");
            
            display.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
