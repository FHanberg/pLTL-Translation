import Generator.ArbiterGen;
import Generator.DepthGen;
import PLTL.PLTLExp;
import PLTL.*;
import Parser.ContextConverter;
import Parser.pltlGrammarLexer;
import Parser.pltlGrammarParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class Testing {
    public static void ArbiterTesting(int lower, int upper){
        for(int x = lower; x <= upper ; x++){
            try{
                System.out.println("arbiterpast " + x);
                PLTLExp primary = generateArbiter(true, x);
                if(primary instanceof And){
                    ArrayList<String> list = MainLoop.splitProcess(primary, true);
                    for (int y = 0; y < list.size(); y++) {
                        try (PrintWriter out = new PrintWriter("output/arbiter/past/" + x +"/past" + x + "_" + y + ".hoa")) {
                            out.println(list.get(y));
                        }
                    }
                }else {
                    String result = MainLoop.Process(generateArbiter(true, x),"", true);

                    try (PrintWriter out = new PrintWriter("output/arbiter/past/past" + x + ".hoa")) {
                        out.println(result);
                    }
                }
                System.out.println("done");

                /*System.out.println("arbiterfuture" + x);
                String result = MainLoop.Process(generateArbiter(false, x), true);

                try(PrintWriter out = new PrintWriter("output/arbiter/future/future" + x + ".hoa")) {
                    out.println(result);
                }
                System.out.println("done");*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void RandomTesting(int varmin, int varmax, int altmin, int altmax){
        for (int i = varmin; i <= varmax; i++) {
            for (int j = altmin; j <= altmax; j++) {
                try{
                    System.out.println("Props: " + i + ", Alternations: " + j);
                    DepthGen gen = new DepthGen();
                    String orig = gen.Generate(j, i, 11, 50);
                    InputStream input  = new ByteArrayInputStream(orig.getBytes(StandardCharsets.UTF_8));
                    BufferedInputStream in = new BufferedInputStream(input);
                    Parser.pltlGrammarLexer lexer = new pltlGrammarLexer(CharStreams.fromStream(in));
                    TokenStream tokenStream = new CommonTokenStream(lexer);
                    Parser.pltlGrammarParser parser = new pltlGrammarParser(tokenStream);

                    pltlGrammarParser.FormulaContext formulaContext = parser.formula();
                    PLTLExp exp = ContextConverter.Conversion(formulaContext);

                    String result = MainLoop.Process(exp, orig,  true);
                    try (PrintWriter out = new PrintWriter("output/random/rand_" + i + "_" + j + ".hoa")) {
                        out.println(result);
                    }
                    System.out.println("done");
                }catch (IOException e) {
                    e.printStackTrace();
                }
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
