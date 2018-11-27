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
public class FunctionContext extends Context{
    
    public FunctionContext(Integer returntype, Token token) {
        super(returntype, false, token, new ArrayList<Context>());
    }
    
    public FunctionContext(Integer returntype, Token token, ArrayList<Context> args) {
        super(returntype, false, token, args);
    }
    
    // métodos para tratar funções
    
}
