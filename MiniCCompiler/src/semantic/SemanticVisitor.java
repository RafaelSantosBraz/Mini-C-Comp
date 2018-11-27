/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import java.util.ArrayList;
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
        Context numContext = (Context) visit(ctx.num());
        Util.getInstance().declareVar(new PrimitiveContext(numContext.getType(), numContext.getConstant(), ctx.ID().getSymbol()));
        return null;
    }

    @Override
    public Object visitDefineChar(CGrammarParser.DefineCharContext ctx) {
        Util.getInstance().declareVar(new PrimitiveContext(Type.CHAR, true, ctx.ID().getSymbol()));
        return null;
    }

    @Override
    public Object visitDefineSTR(CGrammarParser.DefineSTRContext ctx) {
        Util.getInstance().declareVar(new PointerContext(Type.POINTER_CHAR, true, ctx.ID().getSymbol()));
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
    
    @Override
    public Object visitGlobal(CGrammarParser.GlobalContext ctx) {
        Context c = (Context) visit(ctx.decl());
        Util.getInstance().declareVar(c);
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="decl">
    @Override
    public Object visitDeclSimple(CGrammarParser.DeclSimpleContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        return new PrimitiveContext(typeContext.getType(), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclPointer(CGrammarParser.DeclPointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclArray(CGrammarParser.DeclArrayContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext = (Context) visit(ctx.expr());
        Util.getInstance().indexCheck(exprContext);
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValueSimple(CGrammarParser.DeclValueSimpleContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext = (Context) visit(ctx.expr());
        Util.getInstance().declAtribCompatibilityCheck(typeContext, exprContext);
        return new PrimitiveContext(typeContext.getType(), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValuePointer(CGrammarParser.DeclValuePointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        typeContext = new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, typeContext.getToken());
        Context exprContext = (Context) visit(ctx.expr());
        Util.getInstance().declAtribCompatibilityCheck(typeContext, exprContext);
        return new PointerContext(typeContext.getType(), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValueArrayList(CGrammarParser.DeclValueArrayListContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext;
        ArrayList<Context> funcArgsContext;
        if (ctx.expr() != null) {
            exprContext = (Context) visit(ctx.expr());
            Util.getInstance().constIndexCheck(exprContext);
        }
        if (ctx.funcargs() != null) {
            funcArgsContext = (ArrayList<Context>) visit(ctx.funcargs());
            Util.getInstance().declAtribCompatibilityCheck(typeContext, funcArgsContext);
        }
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValueArrayString(CGrammarParser.DeclValueArrayStringContext ctx) {
        Context exprContext;
        if (ctx.expr() != null) {
            exprContext = (Context) visit(ctx.expr());
            Util.getInstance().constIndexCheck(exprContext);
        }
        return new PointerContext(Type.POINTER_CHAR, false, ctx.ID().getSymbol());
    }
    //</editor-fold>

    
}
