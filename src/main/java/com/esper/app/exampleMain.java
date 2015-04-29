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

public class exampleMain {
 
    public static class Tick {
        Random r = new Random();
        String symbol;
        Character alph;
        
 
        public Tick(String s, char a) {
            symbol = s;
            alph = a;
        }
        
        public double getAplh() {return alph;}
        public String getSymbol() {return symbol;}
 
        @Override
        public String toString() {
            return "Character " + alph.toString();
        }
    }
 
    private static Random generator = new Random();
 
    public static void GenerateRandomTick(EPRuntime cepRT) {
        String alphabet = "abc";
        char alph = (char) alphabet.charAt(generator.nextInt(alphabet.length()));
        String symbol = "AAPL";
        Tick tick = new Tick(symbol, alph);
        cepRT.sendEvent(tick);
    }
 
    public static class CEPListener implements UpdateListener {
 
        public void update(EventBean[] newData, EventBean[] oldData) {
            System.out.println("Event: " + newData[0].getUnderlying());
        }
    }
 
    public static void main(String[] args) {
           
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