package com.esper.app;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class MainTime_ext {
 
    public static class Tick {
        Double number;
        Long timeStamp;
 
        public Tick(double n, long t) {
            number = n;
            timeStamp = t;
        }

        public double getNumber() {
            return number;
        }
        public long getTimeStamp() {
            return timeStamp;
        }
 
        @Override
        public String toString() {
            return "Number: " + number.toString() + " time: " + timeStamp.toString();
        }
    }

    private static Random generator = new Random();
 
    public static void GenerateTicks(EPRuntime cepRT) throws ParseException {
        
        Tick tick1 = new Tick(generator.nextInt(10), 0);
        Tick tick2 = new Tick(generator.nextInt(10), 1000);
        Tick tick3 = new Tick(generator.nextInt(10), 6000);
        Tick tick4 = new Tick(generator.nextInt(10), 11000); 
        
//cepRT.sendEvent(new TimerControlEvent(TimerControlEvent.ClockType.CLOCK_EXTERNAL));
        
        System.out.println("Sending tick:" + tick1);
        cepRT.sendEvent(tick1);
        System.out.println("Sending tick:" + tick2);
        cepRT.sendEvent(tick2);
        System.out.println("Sending tick:" + tick3);
        cepRT.sendEvent(tick3);
        System.out.println("Sending tick:" + tick4);
        cepRT.sendEvent(tick4);
    }
 
    public static class CEPListener implements UpdateListener {
 
        public void update(EventBean[] newData, EventBean[] oldData) {
            System.out.println("Event received: " + newData[0].getUnderlying());
        }
    }
 
    public static void main(String[] args) throws ParseException {
 
        SimpleLayout layout = new SimpleLayout();
        ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());
        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel((Level) Level.WARN);
        
        //The Configuration is meant only as an initialization-time object.
        Configuration cepConfig = new Configuration();
        //disable the internal timer
        cepConfig.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        cepConfig.addEventType("StockTick", Tick.class.getName());
        EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
        EPRuntime cepRT = cep.getEPRuntime();
 
        EPAdministrator cepAdm = cep.getEPAdministrator();
        EPStatement cepStatement = cepAdm.createEPL("select count(number) as countOfNumbers " +
                "from StockTick.win:ext_timed(timeStamp, 5 seconds) ");
        cepStatement.addListener(new CEPListener());
        GenerateTicks(cepRT);
    }
}