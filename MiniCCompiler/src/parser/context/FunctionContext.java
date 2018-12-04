/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.context;

import java.util.ArrayList;
import org.antlr.v4.runtime.Token;
import parser.Util;

/**
 *
 * @author rafael
 */
public class FunctionContext extends Context {

    public FunctionContext(Integer returntype, Token token) {
        super(returntype, false, token, new Value(new ArrayList<Context>()));
    }

    public FunctionContext(Integer returntype, Token token, ArrayList<Context> args) {
        super(returntype, false, token, new Value(args));
    }

    // métodos para tratar funções
    public Boolean paramsCheck(ArrayList<Context> args) {
        ArrayList<Context> params = (ArrayList<Context>) getValue().getRealValue();
        if (params.size() != args.size()) {
            return false;
        }
        for (int c = 0; c < params.size(); c++) {
            if (!Util.getInstance().declAtribCompatibilityCheck(params.get(c), args.get(c))) {
                return false;
            }
        }
        return true;
    }

}
