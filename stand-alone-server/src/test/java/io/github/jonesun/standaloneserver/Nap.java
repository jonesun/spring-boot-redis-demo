package io.github.jonesun.standaloneserver;

import java.util.concurrent.TimeUnit;

/**
 * @author jone.sun
 * @date 2021/1/29 16:50
 */
public class Nap {
    public Nap(double t) { // Seconds
        try {
            TimeUnit.MILLISECONDS.sleep((int)(1000 * t));
        } catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public Nap(double t, String msg) {
        this(t);
        System.out.println(msg);
    }
}
