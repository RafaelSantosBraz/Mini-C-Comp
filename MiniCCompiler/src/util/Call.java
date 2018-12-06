/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.HashMap;
import parser.context.Context;

/**
 *
 * @author rafael
 */
public class Call {

    private boolean lock;
    private HashMap<String, Context> symbols;

    public Call(boolean lock){
        this.lock = lock;
        symbols = new HashMap<>();
    }
    
    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public Context getVar(String varName) {
        return symbols.get(varName);
    }

    public void addVar(String varName, Context var) {
        symbols.put(varName, var);
    }
    
    public boolean isThere(String var){
        return symbols.get(var) != null;
    }
        
}
