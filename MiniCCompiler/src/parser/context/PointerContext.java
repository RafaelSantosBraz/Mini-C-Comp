/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.context;

import java.util.ArrayList;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author rafael
 */
public class PointerContext extends Context {

    public PointerContext(Integer type, Boolean constant, Token token) {
        super(type, constant, token, new ArrayList<Object>());
    }
    
    public PointerContext(Integer type, Boolean constant, Token token, Object value) {
        super(type, constant, token, value);
    }

    // métodos para tratar os valores do ponteiro
}
