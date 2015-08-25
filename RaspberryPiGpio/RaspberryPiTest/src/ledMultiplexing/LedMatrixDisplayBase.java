package ledMultiplexing;

import java.util.LinkedList;

import com.pi4j.io.gpio.*;

public abstract class LedMatrixDisplayBase {
    public static int DEFAULT_FRAMERATE = 30;
    
    protected double targetFrameRate;
    protected long scanDelay;
    
    protected boolean isAlive = true;
    
    protected boolean[][] bitmap;
    
    private Thread clock;
    protected long framesCount;
    protected long frameStatsTimestamp;
    protected long lastFrameTimeStamp; 
    
    protected boolean verbose = false;
    
    //constructors
    public LedMatrixDisplayBase(Pin[] colPins, Pin[] rowPins, double targetFrameRate, boolean verbose) {
        if (colPins == null || colPins.length < 1 || rowPins == null || rowPins.length < 1) {
            throw new IllegalArgumentException("Pins not defined properly!");
        }
        
        if (targetFrameRate <= 0) {
            throw new IllegalArgumentException("Frame Rate must be greater than 0!");
        }
        
        //init pi4j-gpio
        initGpio(colPins, rowPins);
        
        //pass arguments
        this.verbose = verbose;
        setFramerate(targetFrameRate);

        //init bitmap and turn all leds off
        bitmap = new boolean[colPins.length][rowPins.length];
        setOff();
        
        //start clock and drawing
        clock = new LedMatrixDisplayScanClock(this);
        clock.start();
    }

    public LedMatrixDisplayBase(Pin[] colPins, Pin[] rowPins, double frameRate) {
        this(colPins, rowPins, frameRate, false);
    }

    public LedMatrixDisplayBase(Pin[] colPins, Pin[] rowPins) {
        this(colPins, rowPins, DEFAULT_FRAMERATE);
    }
    
    //getters
    public abstract int getWidth();    
    public abstract int getHeight();
    
    public double getTargetFrameRate() {
        return targetFrameRate;
    }
    
    public synchronized double getActualFrameRate() {
        long totalTime = lastFrameTimeStamp - frameStatsTimestamp;     
        return (double) framesCount / totalTime * 1000d;
    }
    
    public synchronized String getPerformanceStats() {
        String s = "Target Framerate: " + getTargetFrameRate() + " fps \n";
        s += "Actual Framerate: " + getActualFrameRate() + " fps\n";
        s += "Scan Delay: " + scanDelay + " ms\n";
        s += "Total Frames: " + framesCount + " frames \n";
        s += "Total Time: " + (lastFrameTimeStamp - frameStatsTimestamp)  + " ms\n";
        
        return s;
    }
    
    //setters
    public synchronized void setFramerate(double frameRate) {
        this.targetFrameRate = frameRate;     
        scanDelay = Math.round(1000 / (targetFrameRate * getWidth()));
        
        if (verbose) System.out.println("Framerate changed: " + frameRate + " fps");
        
        resetBenchmark();
    }
    
    public void setVerbose(boolean v) {
        verbose = v;
    }
    
    //public methods
    public abstract void draw();
    
    public void setBitmap(boolean[][] bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Bitmap is null!");
        }
        
        if (bitmap.length < 1 || bitmap[0].length < 1 || 
                bitmap.length != this.bitmap.length || bitmap[0].length != this.bitmap[0].length) {
            throw new IllegalArgumentException("Bitmap has wrong size!");
        }
        
        this.bitmap = bitmap;
        
        if (verbose) System.out.println("Bitmap changed!");
        
        resetBenchmark();
    }
    
    public void setOn() {
        setAllLeds(true);
    }
    
    public void setOff() {
        setAllLeds(false);
    }
    
    public void shutdown() {
        isAlive = false;
        clock.interrupt();
    }
    
    public void resetBenchmark() {
        framesCount = 0;
        frameStatsTimestamp = System.currentTimeMillis();
        lastFrameTimeStamp = frameStatsTimestamp;
    }
    
    //private methods
    protected abstract void initGpio(Pin[] colPins, Pin[] rowPins);
    
    private void setAllLeds(boolean on) {
        for (int x = 0; x < bitmap.length; x++) {
            for (int y = 0; y < bitmap[x].length; y++) {
                bitmap[x][y] = on;
            }
        }
        
        resetBenchmark();
    }
}
