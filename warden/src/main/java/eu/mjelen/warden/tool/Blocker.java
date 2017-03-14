package eu.mjelen.warden.tool;

public class Blocker {

    public void block() {
        block(this);
    }

    public void block(Object obj) {
        synchronized (obj) {
            while(true) {
                try {
                    obj.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
