import Generator.ArbiterGen;
import Generator.DepthGen;
import PLTL.*;
import PLTL.Future.*;
import PLTL.Past.*;
import Parser.ContextConverter;
import Parser.pltlGrammarLexer;
import Parser.pltlGrammarParser;
import Parser.pltlGrammarParser.FormulaContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class main {

    public static void main(String[] args){

        try {
            DepthGen d = new DepthGen();
            //InputStream input = new FileInputStream("test.txt");
            //InputStream input = new ByteArrayInputStream(ArbiterGen.PastArbiter(2).getBytes(StandardCharsets.UTF_8));
            InputStream input = new ByteArrayInputStream(d.Generate(3,3, 10, 50).getBytes(StandardCharsets.UTF_8));
            assert input != null;
            BufferedInputStream in = new BufferedInputStream(input);
            Parser.pltlGrammarLexer lexer = new pltlGrammarLexer(CharStreams.fromStream(in));
            TokenStream tokenStream = new CommonTokenStream(lexer);
            Parser.pltlGrammarParser parser = new pltlGrammarParser(tokenStream);

            @SuppressWarnings("unused")
            FormulaContext formulaContext = parser.formula();

            String result = MainLoop.Process(Objects.requireNonNull(ContextConverter.Conversion(formulaContext)), true);
            System.out.print(result);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }



}
