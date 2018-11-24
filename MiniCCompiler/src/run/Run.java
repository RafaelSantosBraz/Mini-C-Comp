/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import java.awt.HeadlessException;
import java.io.IOException;
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
import semantic.SemanticVisitor;

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
        if (args.length >= 1) {
            filename = args[0];
        }     
        CharStream stream = null;
        if (filename != null) {
            stream = new ANTLRFileStream(filename);
        } else {
            stream = new ANTLRInputStream(System.in);
        }
        CGrammarLexer lexer = new CGrammarLexer(stream);            //Lexer
        TokenStream tokens = new CommonTokenStream(lexer);  //nextToken 
        CGrammarParser parser = new CGrammarParser(tokens);         //Parser
        CGrammarParser.ProgContext prog
                = parser.prog();        //Exec Parser prog
        exibir(prog);
        showParseTreeFrame(prog, parser);
//        System.out.println(SymbolTable.getInstance().dumpTable());
        SemanticVisitor pv = new SemanticVisitor();
        pv.visit(prog);
    }
    
    public static void exibir(ParseTree tree){
        for (int c = 0; c < tree.getChildCount(); c++){
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
