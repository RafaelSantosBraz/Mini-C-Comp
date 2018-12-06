/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.context;

import java.util.ArrayList;
import org.antlr.v4.runtime.Token;
import parser.CGrammarParser;
import util.Util;

/**
 *
 * @author rafael
 */
public class FunctionContext extends Context implements Cloneable {

    private CGrammarParser.FunctionContext treeNode;

    public FunctionContext(Integer returntype, Token token) {
        super(returntype, false, token, new Value(new ArrayList<Context>()));
    }

    public FunctionContext(Integer returntype, Token token, ArrayList<Context> args) {
        super(returntype, false, token, new Value(args));
    }

    public FunctionContext(Integer returntype, Token token, CGrammarParser.FunctionContext treeNode) {
        super(returntype, false, token, new Value(new ArrayList<Context>()));
        this.treeNode = treeNode;
    }

    public FunctionContext(Integer returntype, Token token, ArrayList<Context> args, CGrammarParser.FunctionContext treeNode) {
        super(returntype, false, token, new Value(args));
        this.treeNode = treeNode;
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

    @Override
    public FunctionContext clone()  {
        try {
            return (FunctionContext) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public CGrammarParser.FunctionContext getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(CGrammarParser.FunctionContext treeNode) {
        this.treeNode = treeNode;
    }

}
