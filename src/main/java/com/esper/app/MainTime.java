package com.esper.app;


/**
 *
 * @author Petra
 */


import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.CurrentTimeSpanEvent;
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
        public double getNumber() {return number;}
        public Date getTimeStamp() {return timeStamp;}
 
        @Override
        public String toString() {
            return "Number: " + number.toString() + " time: " + timeStamp.toString();
        }
    }
 
    private static Random generator = new Random();
 
    public static void GenerateRandomTick(EPRuntime cepRT) throws ParseException {
 
        double number = (double) generator.nextInt(10);
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss SSS");
        Date timeStamp = format.parse("2010 01 01 00:00:00 000");
        Tick tick = new Tick(number, timeStamp);
        System.out.println("Sending tick:" + tick);
        cepRT.sendEvent(new CurrentTimeEvent(timeStamp.getTime()));
        cepRT.sendEvent(tick);
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
        cepConfig.addEventType("StockTick", Tick.class.getName());
        EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
        EPRuntime cepRT = cep.getEPRuntime();
 
        EPAdministrator cepAdm = cep.getEPAdministrator();
        EPStatement cepStatement = cepAdm.createEPL("select *" +
                "from StockTick ");
 
        cepStatement.addListener(new CEPListener());
 
       // We generate a few ticks...
        for (int i = 0; i < 10; i++) {
            GenerateRandomTick(cepRT);
        }
    }
}