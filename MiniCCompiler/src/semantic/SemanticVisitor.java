/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import java.util.ArrayList;
import org.antlr.v4.runtime.Token;
import parser.*;

/**
 *
 * @author rafael
 */
public class SemanticVisitor extends CGrammarBaseVisitor<Object> {

    //<editor-fold defaultstate="collapsed" desc="define">
    @Override
    public Object visitDefineNUM(CGrammarParser.DefineNUMContext ctx) {
        SymbolTable g = SemanticTable.getInstance().getGlobalSymbolTable();
        String id = ctx.ID().getText();
        if (g.isThere(id)) {
            Util.getInstance().error(0, ctx.ID().getSymbol(), g.getSymbol(id).getToken());
        } else {
            Token n = (Token) visit(ctx.num());
            g.addSymbol(id, new Context(n.getType(), false, true, ctx.ID().getSymbol()));
        }
        return null;
    }

    @Override
    public Object visitDefineSTR(CGrammarParser.DefineSTRContext ctx) {
        SymbolTable g = SemanticTable.getInstance().getGlobalSymbolTable();
        String id = ctx.ID().getText();
        if (g.isThere(id)) {
            Util.getInstance().error(0, ctx.ID().getSymbol(), g.getSymbol(id).getToken());
        } else {
            g.addSymbol(id, new Context(ctx.STR().getSymbol().getType(), false, true, ctx.ID().getSymbol()));
        }
        return null;
    }

    @Override
    public Object visitDefineChar(CGrammarParser.DefineCharContext ctx) {
        SymbolTable g = SemanticTable.getInstance().getGlobalSymbolTable();
        String id = ctx.ID().getText();
        if (g.isThere(id)) {
            Util.getInstance().error(0, ctx.ID().getSymbol(), g.getSymbol(id).getToken());
        } else {
            g.addSymbol(id, new Context(ctx.CHARC().getSymbol().getType(), false, true, ctx.ID().getSymbol()));
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="num">
    @Override
    public Object visitNumInt(CGrammarParser.NumIntContext ctx) {
        return ctx.NUMINT().getSymbol();
    }

    @Override
    public Object visitNumDouble(CGrammarParser.NumDoubleContext ctx) {
        return ctx.NDOUBLE().getSymbol();
    }
    //</editor-fold>

    @Override
    public Object visitGlobal(CGrammarParser.GlobalContext ctx) {
        Context c = (Context) visit(ctx.decl());
        SymbolTable g = SemanticTable.getInstance().getGlobalSymbolTable();
        if (g.isThere(c.getToken().getText())) {
            Util.getInstance().error(0, c.getToken(), g.getSymbol(c.getToken().getText()).getToken());
        } else {
            g.addSymbol(c.getToken().getText(), c);
        }
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="decl">
    @Override
    public Object visitDeclPointer(CGrammarParser.DeclPointerContext ctx) {
        Integer type = (Integer) visit(ctx.type());
        return new Context(type, true, false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclArray(CGrammarParser.DeclArrayContext ctx) {
        Integer type = (Integer) visit(ctx.type());
        ArrayList<Object> exprResult = (ArrayList<Object>) visit(ctx.expr());
        if ((Integer) exprResult.get(0) != CGrammarLexer.NUMINT) {
            Util.getInstance().error(1,
                    ctx.getToken(CGrammarLexer.OPBR, 0).getSymbol(),
                    ctx.getToken(CGrammarLexer.CLBR, 0).getSymbol()
            );
        }
        return new Context(type, true, false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValueSimple(CGrammarParser.DeclValueSimpleContext ctx) {
        Integer type = (Integer) visit(ctx.type());
        ArrayList<Object> exprResult = (ArrayList<Object>) visit(ctx.expr());
        if (!Util.getInstance().compatibilityTypeTest(type, (Integer) exprResult.get(0))) {
            Util.getInstance().error(2, type, exprResult);
        }
        return new Context(type, false, false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclSimple(CGrammarParser.DeclSimpleContext ctx) {
        Integer type = (Integer) visit(ctx.type());
        return new Context(type, false, false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValuePointer(CGrammarParser.DeclValuePointerContext ctx) {
        Integer type = (Integer) visit(ctx.type());
        ArrayList<Object> exprResult = (ArrayList<Object>) visit(ctx.expr());
        if (!Util.getInstance().compatibilityTypeTest(type, (Integer) exprResult.get(0))) {
            Util.getInstance().error(2, type, exprResult);
        }
        return new Context(type, true, false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValueArrayList(CGrammarParser.DeclValueArrayListContext ctx) {
        Integer type = (Integer) visit(ctx.type());
        ArrayList<Object> exprResult;
        ArrayList<Integer> argsResult;
        if (ctx.expr() != null) {
            exprResult = (ArrayList<Object>) visit(ctx.expr());
            if ((Integer) exprResult.get(0) != CGrammarLexer.NUMINT) {
                Util.getInstance().error(1,
                        ctx.getToken(CGrammarLexer.OPBR, 0).getSymbol(),
                        ctx.getToken(CGrammarLexer.CLBR, 0).getSymbol()
                );
            }
            if (!(Boolean) exprResult.get(1)) {
                Util.getInstance().error(3,
                        ctx.getToken(CGrammarLexer.OPBR, 0).getSymbol(),
                        ctx.getToken(CGrammarLexer.CLBR, 0).getSymbol()
                );
            }
            if (!(Boolean) exprResult.get(2)) {
                Util.getInstance().error(4,
                        ctx.getToken(CGrammarLexer.OPBR, 0).getSymbol(),
                        ctx.getToken(CGrammarLexer.CLBR, 0).getSymbol()
                );
            }
        }
        if (ctx.funcargs() != null) {
            argsResult = (ArrayList<Integer>) visit(ctx.funcargs());
            if (!Util.getInstance().listcompatibilityTypeTest(type, argsResult)){
                Util.getInstance().error(5,
                        ctx.getToken(CGrammarLexer.OPB, 0).getSymbol(),
                        ctx.getToken(CGrammarLexer.CLB, 0).getSymbol()
                );
            }           
        }
        return new Context(type, true, false, ctx.ID().getSymbol());
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="type">
    @Override
    public Object visitTypeInt(CGrammarParser.TypeIntContext ctx) {
        return ctx.INT().getSymbol().getType();
    }

    @Override
    public Object visitTypeChar(CGrammarParser.TypeCharContext ctx) {
        return ctx.CHAR().getSymbol().getType();
    }

    @Override
    public Object visitTypeDouble(CGrammarParser.TypeDoubleContext ctx) {
        return ctx.DOUBLE().getSymbol().getType();
    }
    //</editor-fold>

}
