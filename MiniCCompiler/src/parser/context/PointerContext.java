/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.context;

import java.util.ArrayList;
import java.util.HashMap;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author rafael
 */
public class PointerContext extends Context {
    
    public PointerContext(Integer type, Boolean constant, Token token) {
        super(type, constant, token, new Value(new HashMap<Integer, Object>()));
    }
    
    public PointerContext(Integer type, Boolean constant, Token token, Value value) {
        super(type, constant, token, value);
    }

    // métodos para tratar os valores do ponteiro
    public void addPointValue(Object value, Integer pos) {
        ((HashMap<Integer, Object>) getValue().getRealValue()).put(pos, value);
    }
    
    public Object getPointValue(Integer pos) {
        return ((HashMap<Integer, Object>) getValue().getRealValue()).get(pos);
    }
    
    public void addPointValueListFromContext(ArrayList<Context> args) {
        for (int c = 0; c < args.size(); c++) {
            addPointValue(args.get(c).getValue().getRealValue(), c);
        }
    }
    
     public void addPointValueListFromCharArray(char args[]) {
        for (int c = 0; c < args.length; c++) {
            addPointValue(args[c], c);
        }
    }
}
