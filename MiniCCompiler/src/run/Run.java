/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import interpreter.InterpreterVisitor;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import parser.*;
import preprocessor.PreProcessor;
import semantic.SemanticVisitor;
import util.Call;
import util.CallStack;
import util.FuncTable;
import util.Util;

/**
 *
 * @author wellington
 */
public class Run {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {       
        String filename = "test.c";
        PreProcessor prep = new PreProcessor(filename);
        prep.doPrep();
        if (args.length >= 1) {
            filename = args[0];
        }
        CharStream stream = null;
        if (filename != null) {
            stream = new ANTLRFileStream(filename + PreProcessor.PART + PreProcessor.DONE);
        } else {
            stream = new ANTLRInputStream(System.in);
        }
        CGrammarLexer lexer = new CGrammarLexer(stream);            //Lexer
        TokenStream tokens = new CommonTokenStream(lexer);  //nextToken 
        CGrammarParser parser = new CGrammarParser(tokens);         //Parser
        CGrammarParser.ProgContext prog
                = parser.prog();        //Exec Parser prog
        //exibir(prog);
        showParseTreeFrame(prog, parser);
        //System.out.println(SymbolTable.getInstance().dumpTable());
        SemanticVisitor pv = new SemanticVisitor();
        // base da pilha -> tabela de símbolos global
        CallStack.getInstance().setCall(new Call(true));
        pv.visit(prog);
        if (FuncTable.getInstance().getFunc("main") == null) {
            ArrayList<Object> params = new ArrayList<>();
            Util.getInstance().error(ErrorType.MAIN_DOES_NOT_EXIST, params);
        } else {
            CallStack.getInstance().reset(prog);            
            InterpreterVisitor interpreter = new InterpreterVisitor();
            System.out.println("*** INÍCIO DA EXECUÇÃO! ***");
            interpreter.visit(FuncTable.getInstance().getFunc("main").getTreeNode());
            System.out.println("*** FIM DA EXECUÇÃO! ***");
        }        
        //CallStack t = CallStack.getInstance();
    }

    public static void exibir(ParseTree tree) {
        for (int c = 0; c < tree.getChildCount(); c++) {
            System.out.println(tree.getChild(c).getPayload());
            exibir(tree.getChild(c));
        }
    }

    private static void showParseTreeFrame(ParseTree tree, CGrammarParser parser) throws HeadlessException {
        JFrame frame = new JFrame("SRC: " + tree.getText());
        JPanel panel = new JPanel();
        TreeViewer viewr = new TreeViewer(Arrays.asList(
                parser.getRuleNames()), tree);
        viewr.setScale(1);
        panel.add(viewr);
        frame.add(panel);
        frame.setSize(1000, 600);
        frame.setState(JFrame.MAXIMIZED_HORIZ);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
