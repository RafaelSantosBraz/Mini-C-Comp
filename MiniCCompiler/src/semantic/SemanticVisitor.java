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
        if (ctx.listargs() != null) {
            argsResult = (ArrayList<Integer>) visit(ctx.listargs());
            if (!Util.getInstance().listcompatibilityTypeTest(type, argsResult)) {
                Util.getInstance().error(5,
                        ctx.getToken(CGrammarLexer.OPB, 0).getSymbol(),
                        ctx.getToken(CGrammarLexer.CLB, 0).getSymbol()
                );
            }
        }
        return new Context(type, true, false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclValueArrayString(CGrammarParser.DeclValueArrayStringContext ctx) {
        ArrayList<Object> exprResult;
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
        return new Context(ctx.CHAR().getSymbol().getType(), true, false, ctx.ID().getSymbol());
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

    //<editor-fold defaultstate="collapsed" desc="expr">   
    @Override
    public Object visitExprPlus(CGrammarParser.ExprPlusContext ctx) {
        ArrayList<Object> exprResult = (ArrayList<Object>) visit(ctx.expr());
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        ArrayList<Object> result = new ArrayList<>();
        if (!Util.getInstance().compatibilityTypeTest((Integer) exprResult.get(0), (Integer) termResult.get(0))) {
            Util.getInstance().error(6, (Integer) exprResult.get(0), (Integer) termResult.get(0));
        }
        result.add(Util.getInstance().upperTypeResult((Integer) exprResult.get(0), (Integer) termResult.get(0)));
        result.add(((Boolean) exprResult.get(1) == true && (Boolean) termResult.get(1) == true));
        result.add(((Boolean) exprResult.get(2) == true && (Boolean) termResult.get(2) == true));
        return result;
    }

    @Override
    public Object visitExprMin(CGrammarParser.ExprMinContext ctx) {
        ArrayList<Object> exprResult = (ArrayList<Object>) visit(ctx.expr());
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        ArrayList<Object> result = new ArrayList<>();
        if (!Util.getInstance().compatibilityTypeTest((Integer) exprResult.get(0), (Integer) termResult.get(0))) {
            Util.getInstance().error(6, (Integer) exprResult.get(0), (Integer) termResult.get(0));
        }
        result.add(Util.getInstance().upperTypeResult((Integer) exprResult.get(0), (Integer) termResult.get(0)));
        result.add(((Boolean) exprResult.get(1) == true && (Boolean) termResult.get(1) == true));
        result.add(((Boolean) exprResult.get(2) == true && (Boolean) termResult.get(2) == true));
        return result;
    }

    @Override
    public Object visitExprHided(CGrammarParser.ExprHidedContext ctx) {
        ArrayList<Object> exprResult = (ArrayList<Object>) visit(ctx.expr());
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        ArrayList<Object> result = new ArrayList<>();
        if (!Util.getInstance().compatibilityTypeTest((Integer) exprResult.get(0), (Integer) termResult.get(0))) {
            Util.getInstance().error(6, (Integer) exprResult.get(0), (Integer) termResult.get(0));
        }
        result.add(Util.getInstance().upperTypeResult((Integer) exprResult.get(0), (Integer) termResult.get(0)));
        result.add(((Boolean) exprResult.get(1) == true && (Boolean) termResult.get(1) == true));
        result.add(((Boolean) exprResult.get(2) == true && (Boolean) termResult.get(2) == true));
        return result;
    }

    @Override
    public Object visitExprTerm(CGrammarParser.ExprTermContext ctx) {
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        ArrayList<Object> result = new ArrayList<>();
        result.add((Integer) termResult.get(0));
        result.add((Boolean) termResult.get(1));
        result.add((Boolean) termResult.get(2));
        return result;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="term">   
    @Override
    public Object visitTermMult(CGrammarParser.TermMultContext ctx) {
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        ArrayList<Object> factResult = (ArrayList<Object>) visit(ctx.fact());
        ArrayList<Object> result = new ArrayList<>();
        if (!Util.getInstance().compatibilityTypeTest((Integer) termResult.get(0), (Integer) factResult.get(0))) {
            Util.getInstance().error(6, (Integer) termResult.get(0), (Integer) factResult.get(0));
        }
        result.add(Util.getInstance().upperTypeResult((Integer) termResult.get(0), (Integer) factResult.get(0)));
        result.add(((Boolean) termResult.get(1) == true && (Boolean) factResult.get(1) == true));
        result.add(((Boolean) termResult.get(2) == true && (Boolean) factResult.get(2) == true));
        return result;
    }

    @Override
    public Object visitTermDiv(CGrammarParser.TermDivContext ctx) {
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        ArrayList<Object> factResult = (ArrayList<Object>) visit(ctx.fact());
        ArrayList<Object> result = new ArrayList<>();
        if (!Util.getInstance().compatibilityTypeTest((Integer) termResult.get(0), (Integer) factResult.get(0))) {
            Util.getInstance().error(6, (Integer) termResult.get(0), (Integer) factResult.get(0));
        }
        result.add(Util.getInstance().upperTypeResult((Integer) termResult.get(0), (Integer) factResult.get(0)));
        result.add(((Boolean) termResult.get(1) == true && (Boolean) factResult.get(1) == true));
        result.add(((Boolean) termResult.get(2) == true && (Boolean) factResult.get(2) == true));
        return result;
    }

    @Override
    public Object visitTermFact(CGrammarParser.TermFactContext ctx) {
        ArrayList<Object> factResult = (ArrayList<Object>) visit(ctx.fact());
        ArrayList<Object> result = new ArrayList<>();
        result.add((Integer) factResult.get(0));
        result.add((Boolean) factResult.get(1));
        result.add((Boolean) factResult.get(2));
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fact">   
    @Override
    public Object visitFactNum(CGrammarParser.FactNumContext ctx) {
        Integer numType = (Integer) visit(ctx.num());
        ArrayList<Object> result = new ArrayList<>();
        result.add(numType);
        result.add(true);
        result.add((numType == CGrammarLexer.NUMINT));
        return result;
    }

    @Override
    public Object visitFactStr(CGrammarParser.FactStrContext ctx) {
        ArrayList<Object> result = new ArrayList<>();
        result.add(CGrammarLexer.STR);
        result.add(true);
        result.add(false);
        return result;
    }

    @Override
    public Object visitFactChar(CGrammarParser.FactCharContext ctx) {
        ArrayList<Object> result = new ArrayList<>();
        result.add(CGrammarLexer.CHARC);
        result.add(true);
        result.add(false);
        return result;
    }

    @Override
    public Object visitFactCall(CGrammarParser.FactCallContext ctx) {
        ArrayList<Object> funcResult = (ArrayList<Object>) visit(ctx.funccall());
        ArrayList<Object> result = new ArrayList<>();
        result.add(funcResult.get(0));
        result.add(false);
        result.add(false);
        return result;
    }
    //</editor-fold>

    @Override
    public Object visitFunccallReal(CGrammarParser.FunccallRealContext ctx) {
        ArrayList<Object> result = new ArrayList<>();
        ArrayList<Integer> argTypes = new ArrayList<>();
        if (ctx.funcargs() != null) {
            argTypes.addAll((ArrayList<Integer>) visit(ctx.funcargs()));
        }

        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="funcargs">   
    @Override
    public Object visitFuncargsCompose(CGrammarParser.FuncargsComposeContext ctx) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add((Integer) visit(ctx.expr()));
        result.addAll((ArrayList<Integer>) visit(ctx.funcargs()));
        return result;
    }

    @Override
    public Object visitFuncargsSingle(CGrammarParser.FuncargsSingleContext ctx) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add((Integer) visit(ctx.expr()));
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="listargs">   
    @Override
    public Object visitListargsCompose(CGrammarParser.ListargsComposeContext ctx) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add((Integer) visit(ctx.expr()));
        result.addAll((ArrayList<Integer>) visit(ctx.listargs()));
        return result;
    }

    @Override
    public Object visitListargsSingle(CGrammarParser.ListargsSingleContext ctx) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add((Integer) visit(ctx.expr()));
        return result;
    }
    //</editor-fold>

}
