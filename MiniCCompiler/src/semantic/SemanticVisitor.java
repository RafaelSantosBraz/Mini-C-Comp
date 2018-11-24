/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import org.antlr.v4.runtime.Token;
import parser.*;

/**
 *
 * @author rafael
 */
public class SemanticVisitor extends CGrammarBaseVisitor<Object> {

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
            g.addSymbol(id, new Context(CGrammarLexer.STR, false, true, ctx.ID().getSymbol()));
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
            g.addSymbol(id, new Context(CGrammarLexer.CHAR, false, true, ctx.ID().getSymbol()));
        }
        return null;
    }

    @Override
    public Object visitNumInt(CGrammarParser.NumIntContext ctx) {
        return ctx.NUMINT().getSymbol();
    }

    @Override
    public Object visitNumDouble(CGrammarParser.NumDoubleContext ctx) {
        return ctx.NDOUBLE().getSymbol();
    }

}
