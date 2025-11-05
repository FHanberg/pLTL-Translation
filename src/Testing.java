import Generator.ArbiterGen;
import PLTL.PLTLExp;
import Parser.ContextConverter;
import Parser.pltlGrammarLexer;
import Parser.pltlGrammarParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Testing {
    public static void ArbiterTesting(int lower, int upper){
        for(int x = lower; x <= upper ; x++){
            try{
                System.out.println("arbiterpast" + x);
                String result = MainLoop.Process(generateArbiter(true, x), true);

                try(PrintWriter out = new PrintWriter("output/arbiter/past/past" + x + ".txt")) {
                    out.println(result);
                }
                System.out.println("done");

                System.out.println("arbiterfuture" + x);
                result = MainLoop.Process(generateArbiter(false, x), true);

                try(PrintWriter out = new PrintWriter("output/arbiter/future/future" + x + ".txt")) {
                    out.println(result);
                }
                System.out.println("done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static PLTLExp generateArbiter(boolean past, int num) throws IOException {
        InputStream input;
        if(past){
            input = new ByteArrayInputStream(ArbiterGen.PastArbiter(num).getBytes(StandardCharsets.UTF_8));
        }else{
            input = new ByteArrayInputStream(ArbiterGen.FutureArbiter(num).getBytes(StandardCharsets.UTF_8));
        }
        BufferedInputStream in = new BufferedInputStream(input);
        Parser.pltlGrammarLexer lexer = new pltlGrammarLexer(CharStreams.fromStream(in));
        TokenStream tokenStream = new CommonTokenStream(lexer);
        Parser.pltlGrammarParser parser = new pltlGrammarParser(tokenStream);

        @SuppressWarnings("unused")
        pltlGrammarParser.FormulaContext formulaContext = parser.formula();
        return ContextConverter.Conversion(formulaContext);
    }
}
