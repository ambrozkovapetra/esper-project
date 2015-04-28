package com.esper.app;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 *
 * @author Petra
 */
import com.espertech.esper.client.*;
import java.util.Random;
import java.util.Date;

public class exampleMain {
 
    public static class Tick {
        Random r = new Random();
        String symbol;
        Double price;
        Date timeStamp;
        Character alph;
        
 
        public Tick(String s, char a, long t) {
            symbol = s;
            alph = a;
            timeStamp = new Date(t);
            
        }
        public double getAplh() {return alph;}
        public String getSymbol() {return symbol;}
        public Date getTimeStamp() {return timeStamp;}
 
        @Override
        public String toString() {
            return "Character: " + alph.toString() + " time: " + timeStamp.toString();
        }
    }
 
    private static Random generator = new Random();
 
    public static void GenerateRandomTick(EPRuntime cepRT) {
        String alphabet = "abc";
        //double price = (double) generator.nextInt(10);
        char alph = (char) alphabet.charAt(generator.nextInt(alphabet.length()));
        long timeStamp = System.currentTimeMillis();
        String symbol = "AAPL";
        Tick tick = new Tick(symbol, alph, timeStamp);
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
        EPStatement cepStatement = cepAdm.createEPL("select * from " +
                "StockTick(symbol='AAPL').win:firstlength(2)");
                
 
        cepStatement.addListener(new CEPListener());
 
       // We generate a few ticks...
        for (int i = 0; i < 5; i++) {
            GenerateRandomTick(cepRT);
        }
    }
}