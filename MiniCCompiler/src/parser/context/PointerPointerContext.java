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
public class PointerPointerContext extends Context {

    public PointerPointerContext(Integer type, Boolean constant, Token token) {
        super(type, constant, token, new Value(new ArrayList<ArrayList<Context>>()));
    }

    // métodos para tratar os valores do ponteiro do ponteiro
}
