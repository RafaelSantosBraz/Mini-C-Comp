/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import parser.*;
import parser.context.Context;
import parser.context.PointerContext;
import parser.context.PrimitiveContext;

/**
 *
 * @author rafael
 */
public class SemanticVisitor extends CGrammarBaseVisitor<Object> {

    //<editor-fold defaultstate="collapsed" desc="define">
    @Override
    public Object visitDefineNUM(CGrammarParser.DefineNUMContext ctx) {
        Util.getInstance().declareVar((Context) visit(ctx.num()));
        return null;
    }

    @Override
    public Object visitDefineChar(CGrammarParser.DefineCharContext ctx) {
        Util.getInstance().declareVar(new PrimitiveContext(Type.CHAR, true, ctx.CHARC().getSymbol()));
        return null;
    }

    @Override
    public Object visitDefineSTR(CGrammarParser.DefineSTRContext ctx) {
        Util.getInstance().declareVar(new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol()));
        return null;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="num">
    @Override
    public Object visitNumInt(CGrammarParser.NumIntContext ctx) {
        return new PrimitiveContext(Type.INT, true, ctx.NUMINT().getSymbol());
    }

    @Override
    public Object visitNumDouble(CGrammarParser.NumDoubleContext ctx) {
        return new PrimitiveContext(Type.DOUBLE, true, ctx.NDOUBLE().getSymbol());
    }
    //</editor-fold>
}
