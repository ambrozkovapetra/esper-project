package com.esper.app;
/**
 *
 * @author Petra
 */
import com.espertech.esper.client.*;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class MainStrings {
 
    public static class Tick {
        Random r = new Random();
        String letter;
 
        public Tick(String l) {
            letter = l;
        }
        
        public String getLetter() {return letter;}
 
        @Override
        public String toString() {
            return "Character " + letter.toString();
        }
    }
 
    private static Random generator = new Random();
 
    public static void GenerateRandomTick(EPRuntime cepRT) {
        String alphabet = "abc";
        int length = 1;
        String letter = RandomStringUtils.random(length, alphabet.toCharArray());  
        Tick tick = new Tick(letter);
        System.out.println("Sending tick: " + tick);
        cepRT.sendEvent(tick);
    }
 
    public static class CEPListener implements UpdateListener {
 
        public void update(EventBean[] newData, EventBean[] oldData) {
            System.out.println("Event received: " + newData[0].getUnderlying());
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
        EPStatement cepStatement = cepAdm.createEPL("select letter as hereIsMyLetter from " +
                "StockTick where letter='a'");
                
        cepStatement.addListener(new CEPListener());
 
       // We generate a few ticks...
        for (int i = 0; i < 100; i++) {
            GenerateRandomTick(cepRT);
        }
    }
}