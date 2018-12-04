/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

import java.util.ArrayList;
import parser.CGrammarBaseVisitor;
import parser.CGrammarLexer;
import parser.CGrammarParser;
import parser.ErrorType;
import parser.Type;
import parser.Util;
import parser.context.Context;
import parser.context.FunctionContext;
import parser.context.PointerContext;
import parser.context.PrimitiveContext;
import parser.context.Value;

/**
 *
 * @author rafael
 */
public class InterpreterVisitor extends CGrammarBaseVisitor<Object> {

    //<editor-fold defaultstate="collapsed" desc="global">
    @Override
    public Object visitGlobal(CGrammarParser.GlobalContext ctx) {
        Util.getInstance().setCurrentFuncName(null);
        Util.getInstance().declareVar((Context) visit(ctx.decl()));
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="num">
    @Override
    public Object visitNumInt(CGrammarParser.NumIntContext ctx) {
        return new PrimitiveContext(Type.INT, true, ctx.NUMINT().getSymbol(), new Value(Util.getInstance().stringIntConvertion(ctx.NUMINT().getText())));
    }

    @Override
    public Object visitNumDouble(CGrammarParser.NumDoubleContext ctx) {
        return new PrimitiveContext(Type.DOUBLE, true, ctx.NDOUBLE().getSymbol(), new Value(Util.getInstance().stringIntConvertion(ctx.NDOUBLE().getText())));
    }
    //</editor-fold>

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
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclDeclatrib(CGrammarParser.DeclDeclatribContext ctx) {
        return visit(ctx.declatrib());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="declatrib">
    @Override
    public Object visitDeclatribValueSimple(CGrammarParser.DeclatribValueSimpleContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext = (Context) visit(ctx.expr());
        return new PrimitiveContext(typeContext.getType(), false, ctx.ID().getSymbol(), new Value(exprContext.getValue().getRealValue()));
    }

    @Override
    public Object visitDeclatribValuePointer(CGrammarParser.DeclatribValuePointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context id = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID(1).getSymbol()));
        if (ctx.ADRESS() != null) {
            PointerContext p = new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID(0).getSymbol());
            p.addPointValue(id.getValue().getRealValue(), 0);
            return p;
        }
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID(0).getSymbol(), id.getValue());
    }

    @Override
    public Object visitDeclatribValueArrayList(CGrammarParser.DeclatribValueArrayListContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        ArrayList<Context> funcArgsContext = (ArrayList<Context>) visit(ctx.funcargs());
        PointerContext p = new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
        p.addPointValueListFromContext(funcArgsContext);
        return p;
    }

    @Override
    public Object visitDeclatribValueArrayString(CGrammarParser.DeclatribValueArrayStringContext ctx) {
        PointerContext p = new PointerContext(Type.POINTER_CHAR, false, ctx.ID().getSymbol());
        p.addPointValueListFromCharArray(ctx.STR().getText().toCharArray());
        return p;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="type">
    @Override
    public Object visitTypeInt(CGrammarParser.TypeIntContext ctx) {
        return new PrimitiveContext(Type.INT, true, ctx.INT().getSymbol());
    }

    @Override
    public Object visitTypeChar(CGrammarParser.TypeCharContext ctx) {
        return new PrimitiveContext(Type.CHAR, true, ctx.CHAR().getSymbol());
    }

    @Override
    public Object visitTypeDouble(CGrammarParser.TypeDoubleContext ctx) {
        return new PrimitiveContext(Type.DOUBLE, true, ctx.DOUBLE().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="funcargs">
    @Override
    public Object visitFuncargsCompose(CGrammarParser.FuncargsComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        args.add((Context) visit(ctx.expr()));
        args.addAll((ArrayList<Context>) visit(ctx.funcargs()));
        return args;
    }

    @Override
    public Object visitFuncargsSingle(CGrammarParser.FuncargsSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        args.add((Context) visit(ctx.expr()));
        return args;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="expr">
    @Override
    public Object visitExprPlus(CGrammarParser.ExprPlusContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context termContext = (Context) visit(ctx.term());
        return Util.getInstance().sumOp(exprContext, termContext);
    }

    @Override
    public Object visitExprMin(CGrammarParser.ExprMinContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context termContext = (Context) visit(ctx.term());
        return Util.getInstance().minusOp(exprContext, termContext);
    }

    @Override
    public Object visitExprHided(CGrammarParser.ExprHidedContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context termContext = (Context) visit(ctx.term());
        return Util.getInstance().sumOp(exprContext, termContext);
    }

    @Override
    public Object visitExprTerm(CGrammarParser.ExprTermContext ctx) {
        return (Context) visit(ctx.term());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="term">
    @Override
    public Object visitTermMult(CGrammarParser.TermMultContext ctx) {
        Context termContext = (Context) visit(ctx.term());
        Context factContext = (Context) visit(ctx.fact());
        return Util.getInstance().multOp(termContext, factContext);
    }

    @Override
    public Object visitTermDiv(CGrammarParser.TermDivContext ctx) {
        Context termContext = (Context) visit(ctx.term());
        Context factContext = (Context) visit(ctx.fact());
        return Util.getInstance().divOp(termContext, factContext);
    }

    @Override
    public Object visitTermFact(CGrammarParser.TermFactContext ctx) {
        return visit(ctx.fact());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fact">
    @Override
    public Object visitFactNum(CGrammarParser.FactNumContext ctx) {
        return visit(ctx.num());
    }

    @Override
    public Object visitFactStr(CGrammarParser.FactStrContext ctx) {
        PointerContext p = new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol());
        p.addPointValueListFromCharArray(ctx.STR().getText().toCharArray());
        return p;
    }

    @Override
    public Object visitFactChar(CGrammarParser.FactCharContext ctx) {
        return new PrimitiveContext(Type.CHAR, true, ctx.CHARC().getSymbol(), new Value(ctx.CHARC().getText().charAt(1)));
    }

    @Override
    public Object visitFactCall(CGrammarParser.FactCallContext ctx) {
        return visit(ctx.funccall());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="funccall">
    @Override
    public Object visitFunccallReal(CGrammarParser.FunccallRealContext ctx) {
        return visit(ctx.funccallact());
    }

    @Override
    public Object visitFuncIsolate(CGrammarParser.FuncIsolateContext ctx) {
        return visit(ctx.downfact());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="downfact">
    @Override
    public Object visitDownfactId(CGrammarParser.DownfactIdContext ctx) {
        return Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
    }

    @Override
    public Object visitDownfactAdress(CGrammarParser.DownfactAdressContext ctx) {
        return Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
    }

    @Override
    public Object visitDownfactContent(CGrammarParser.DownfactContentContext ctx) {
        return ((PointerContext) Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()))).getPointValue(0);
    }

    @Override
    public Object visitDownfactArray(CGrammarParser.DownfactArrayContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        return ((PointerContext) Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()))).getPointValue(((Number) exprContext.getValue().getRealValue()).intValue());
    }

    @Override
    public Object visitDownfactExpr(CGrammarParser.DownfactExprContext ctx) {
        return visit(ctx.expr());
    }
    //</editor-fold>

}
