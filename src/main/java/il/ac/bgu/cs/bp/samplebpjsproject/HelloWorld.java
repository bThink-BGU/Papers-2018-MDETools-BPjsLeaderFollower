package il.ac.bgu.cs.bp.samplebpjsproject;

import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.SingleResourceBProgram;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.listeners.StreamLoggerListener;

/**
 * Simple class running a BPjs program that selects "hello world" events.
 * @author michael
 */
public class HelloWorld {
    
    public static void main(String[] args) throws InterruptedException {
        // This will load the program file  <Project>/src/main/resources/HelloBPjsWorld.js
        final SingleResourceBProgram bprog = new SingleResourceBProgram("HelloBPjsWorld.js");
        
        // Print program events to the console
        bprog.addListener( new StreamLoggerListener() );
        
        // go!
        bprog.start();
    }
    
}
