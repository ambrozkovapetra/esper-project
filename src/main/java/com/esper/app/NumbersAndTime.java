package com.esper.app;

/**
 *
 * @author Petra
 */

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.espertech.esper.client.*;
import java.util.Random;
import java.util.Date;

public class NumbersAndTime {
 
    public static class Tick {
        Double number;
        Date timeStamp;
 
        public Tick(double n, long t) {
            number = n;
            timeStamp = new Date(t);
        }
        public double getNumber() {return number;}
        public Date getTimeStamp() {return timeStamp;}
 
        @Override
        public String toString() {
            return "Number: " + number.toString() + " time: " + timeStamp.toString();
        }
    }
 
    private static Random generator = new Random();
 
    public static void GenerateRandomTick(EPRuntime cepRT) {
 
        double number = (double) generator.nextInt(10);
        long timeStamp = System.nanoTime();//System.currentTimeMillis(); 
        Tick tick = new Tick(number, timeStamp);
        System.out.println("Sending tick:" + tick);
        cepRT.sendEvent(tick);
 
    }
 
    public static class CEPListener implements UpdateListener {
 
        public void update(EventBean[] newData, EventBean[] oldData) {
            System.out.println("Event received: " + newData[0].getUnderlying());
        }
    }
 
    public static void main(String[] args) {
 
        SimpleLayout layout = new SimpleLayout();
        ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());
        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel((Level) Level.WARN);
//The Configuration is meant only as an initialization-time object.
        Configuration cepConfig = new Configuration();
        cepConfig.addEventType("StockTick", Tick.class.getName());
        EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
        EPRuntime cepRT = cep.getEPRuntime();
 
        EPAdministrator cepAdm = cep.getEPAdministrator();
        EPStatement cepStatement = cepAdm.createEPL("select count(number) from " +
                "StockTick.win:time(2) ");
 
        cepStatement.addListener(new CEPListener());
 
       // We generate a few ticks...
        for (int i = 0; i < 10; i++) {
            GenerateRandomTick(cepRT);
        }
    }
}