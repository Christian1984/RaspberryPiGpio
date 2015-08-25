package ledMultiplexing;

public class LedMatrixDisplayScanClock extends Thread {
    private LedMatrixDisplayBase display;
    
    public LedMatrixDisplayScanClock(LedMatrixDisplayBase display) {
        if (display == null) {
            throw new NullPointerException("Display must not be null!");
        }
        
        this.display = display;
    }
    
    @Override
    public void run() {
        display.draw();
    }
}
