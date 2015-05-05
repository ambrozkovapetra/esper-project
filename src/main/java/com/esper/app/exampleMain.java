package com.esper.app;

/**
 *
 * @author Petra
 */
import com.espertech.esper.client.*;
import java.util.Random;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class ExampleMain {
 
    public static class Tick {
        Random r = new Random();
        Double number;
 
        public Tick(double n) {
            number = n;
        }
        
        public double getNumber() {return number;}
 
        @Override
        public String toString() {
            return "Number " + number.toString();
        }
    }
 
    private static Random generator = new Random();
 
    public static void GenerateRandomTick(EPRuntime cepRT) {
        double number = (double) generator.nextInt(10);
        Tick tick = new Tick(number);
        System.out.println("Sending tick:" + tick);
        cepRT.sendEvent(tick);
    }
 
    public static class CEPListener implements UpdateListener {
 
        public void update(EventBean[] newData, EventBean[] oldData) {
            System.out.println("Event: " + newData[0].getUnderlying());
        }
    }
 
    public static void main(String[] args) {
        
        //logovani
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
        EPStatement cepStatement = cepAdm.createEPL("select sum(number) from " +
                "StockTick.win:time(5)");
                
        cepStatement.addListener(new CEPListener());
 
       // We generate a few ticks...
        for (int i = 0; i < 10; i++) {
            GenerateRandomTick(cepRT);
        }
    }
}
