/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import parser.context.Context;
import java.util.HashMap;

/**
 *
 * @author rafael
 */
public class SymbolTable {
    
    private final HashMap<String, Context> memory;

    public SymbolTable() {
        memory = new HashMap<>();
    }

    public Boolean isThere(String Symbol) {
        return memory.containsKey(Symbol);
    }

    public void addSymbol(String varName, Context value) {
        memory.put(varName, value);
    }

    public Context getSymbol(String varName) {
        return memory.get(varName);
    }

//    public String dumpTable() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("---Symbols---\n");
//        for (Map.Entry<String, Number> e : memory.entrySet()) {
//            Number aux = e.getValue();
//            sb.append(String.format(" %s -> %f \n", e.getKey(), Util.typeConvertion(Util.getTokenType(aux), aux)));
//        }
//        sb.append("-------------\n");
//        return sb.toString();
//
//    }

}
