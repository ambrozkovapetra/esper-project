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

public class MainTime {
 
    public static class Tick {
        Double number;
        Date timeStamp;
 
        public Tick(double n, Date t) {
            number = n;
            timeStamp = t;
        }

        public double getNumber() {
            return number;
        }
        public Date getTimeStamp() {
            return timeStamp;
        }
 
        @Override
        public String toString() {
            return "Number: " + number.toString() + " time: " + timeStamp.toString();
        }
    }

    private static Random generator = new Random();
 
    public static void GenerateTicks(EPRuntime cepRT) throws ParseException {
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss SSS");
        Tick tick1 = new Tick(generator.nextInt(10),
                format.parse("2015 01 01 00:00:00 000"));
        Tick tick2 = new Tick(generator.nextInt(10), 
                format.parse("2015 01 01 00:00:09 000"));
        Tick tick3 = new Tick(generator.nextInt(10), 
                format.parse("2015 01 01 00:00:12 000"));
        Tick tick4 = new Tick(generator.nextInt(10), 
                format.parse("2015 01 01 00:00:19 000"));  
        
        //cepRT.sendEvent(new TimerControlEvent(TimerControlEvent.ClockType.CLOCK_EXTERNAL));
        
        cepRT.sendEvent(new CurrentTimeEvent(tick1.getTimeStamp().getTime()));
        System.out.println("Sending tick:" + tick1);
        cepRT.sendEvent(tick1);
        cepRT.sendEvent(new CurrentTimeEvent(tick2.getTimeStamp().getTime()));
        System.out.println("Sending tick:" + tick2);
        cepRT.sendEvent(tick2);
        cepRT.sendEvent(new CurrentTimeEvent(tick3.getTimeStamp().getTime()));
        System.out.println("Sending tick:" + tick3);
        cepRT.sendEvent(tick3);
        cepRT.sendEvent(new CurrentTimeEvent(tick4.getTimeStamp().getTime()));
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
                "from StockTick.win:time(6 seconds) ");
        cepStatement.addListener(new CEPListener());
        GenerateTicks(cepRT);
    }
}