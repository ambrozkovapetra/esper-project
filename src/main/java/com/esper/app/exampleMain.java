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

public class exampleMain {
 
    public static class Tick {
        Random r = new Random();
        Character alph;
 
        public Tick(char a) {
            alph = a;
        }
        
        public double getAplh() {return alph;}
 
        @Override
        public String toString() {
            return "Character " + alph.toString();
        }
    }
 
    private static Random generator = new Random();
 
    public static void GenerateRandomTick(EPRuntime cepRT) {
        String alphabet = "abc";
        char alph = (char) alphabet.charAt(generator.nextInt(alphabet.length()));
        Tick tick = new Tick(alph);
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
        EPStatement cepStatement = cepAdm.createEPL("select * from " +
                "StockTick");
                
        cepStatement.addListener(new CEPListener());
 
       // We generate a few ticks...
        for (int i = 0; i < 10; i++) {
            GenerateRandomTick(cepRT);
        }
    }
}