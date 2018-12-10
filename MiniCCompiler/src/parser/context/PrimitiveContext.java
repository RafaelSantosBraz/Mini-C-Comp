/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.context;

import org.antlr.v4.runtime.Token;

/**
 *
 * @author rafael
 */
public class PrimitiveContext extends Context {

    public PrimitiveContext(Integer type, Boolean constant, Token token) {
        super(type, constant, token);
    }

    public PrimitiveContext(Integer type, Boolean constant, Token token, Object value) {
        super(type, constant, token, new Value(value));
    }

}
