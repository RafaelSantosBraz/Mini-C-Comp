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
        if (SemanticTable.getInstance().getGlobalSymbolTable().isThere(ctx.ID().getText())) {
            Util.getInstance().error(
                    0,
                    ctx.ID().getSymbol(),
                    SemanticTable.getInstance().getGlobalSymbolTable().getSymbol(ctx.ID().getText()).getToken()
            );
        } else {
            Token n = (Token) visit(ctx.num());
            SemanticTable.getInstance().getGlobalSymbolTable().addSymbol(
                    ctx.ID().getText(),
                    new Context(n.getType(), false, true, n)
            );
        }
        return null;
    }

}
