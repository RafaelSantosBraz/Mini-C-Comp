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
        super(type, constant, token, new Value(new HashMap<Integer, Value>()));
    }
    
    public PointerContext(Integer type, Boolean constant, Token token, Object value) {
        super(type, constant, token, new Value(new HashMap<Integer, Value>()));
        addPointRealValue(value, 0);
    }
    
    public PointerContext(Integer type, Boolean constant, Token token, Context context) {
        super(type, constant, token, context.getValue());
    }

    // métodos para tratar os valores do ponteiro
    public void addPointValue(Value value, Integer pos) {
        ((HashMap<Integer, Value>) getValue().getRealValue()).put(pos, value);
    }
    
    public void addPointRealValue(Object value, Integer pos) {
        if (((HashMap<Integer, Value>) getValue().getRealValue()).get(pos) != null) {
            ((HashMap<Integer, Value>) getValue().getRealValue()).get(pos).setRealValue(value);
        } else {
            addPointValue(new Value(value), pos);
        }
    }
    
    public Value getPointValue(Integer pos) {
        return ((HashMap<Integer, Value>) getValue().getRealValue()).get(pos);
    }
    
    public Object getPointRealValue(Integer pos) {
        return ((HashMap<Integer, Value>) getValue().getRealValue()).get(pos).getRealValue();
    }
    
    public void addPointValueListFromContext(ArrayList<Context> args) {
        for (int c = 0; c < args.size(); c++) {
            addPointValue(args.get(c).getValue(), c);
        }
    }
    
    public void addPointValueListFromCharArray(char args[]) {
        for (int c = 0; c < args.length; c++) {
            addPointRealValue(args[c], c);
        }
    }
}
