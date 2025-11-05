package Generator;

import PLTL.PLTLExp;

public class ArbiterGen {

    public static char getChar(int i) {
        return i<0 || i>25 ? '?' : (char)('a' + i);
    }

    public static String FutureArbiter(int n){
        StringBuilder builder = new StringBuilder();

        builder.append("(G(");
        for (int i = 0; i < n-1; i++) {
            for (int j = i + 1; j < n; j++){
                if(i != 0 && j != 1)
                    builder.append("& ");
                builder.append("!( g");//"!( gi & gj ) "
                builder.append(getChar(i));
                builder.append(" & g");
                builder.append(getChar(j));
                builder.append(" ) ");
            }
        }
        builder.append("))");

        for(int i = 0 ; i < n ; i++){
            builder.append("& (G ( r");
            builder.append(getChar(i));
            builder.append(" -> F g");
            builder.append(getChar(i));
            builder.append(" )) & (! g");
            builder.append(getChar(i));
            builder.append(" W r");
            builder.append(getChar(i));
            builder.append(") & (G( g");
            builder.append(getChar(i));
            builder.append(" -> X(! g");
            builder.append(getChar(i));
            builder.append(" W r");
            builder.append(getChar(i));
            builder.append(" ))) ");
        }

        return builder.toString();
    }

    public static String PastArbiter(int n){
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        for (int i = 0; i < n-1; i++) {
            for (int j = i + 1; j < n; j++){
                if(i != 0 && j != 1)
                    builder.append("& ");
                builder.append("!( g");//"!( gi & gj ) "
                builder.append(getChar(i));
                builder.append(" & g");
                builder.append(getChar(j));
                builder.append(" ) ");
            }
        }
        builder.append(")");

        for(int i = 0 ; i < n ; i++){
            builder.append("& (GF ( !r"); // & (GF(!ra ~S ga)) & (G((ga -> ra) | (Y(ra B !ga)))
            builder.append(getChar(i));
            builder.append(" ~S g");
            builder.append(getChar(i));
            builder.append(" )) & (G ((g");
            builder.append(getChar(i));
            builder.append(" -> r");
            builder.append(getChar(i));
            builder.append(") | (Y( r");
            builder.append(getChar(i));
            builder.append(" B !g");
            builder.append(getChar(i));
            builder.append(" ))))");
        }

        return builder.toString();
    }
}
