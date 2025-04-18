package PLTL;

import java.util.HashSet;

public class And extends Binary{

    public And(PLTLExp left, PLTLExp right){
        super(left, right);
    }

    @Override
    public <R> R accept(PLTLExp.Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof And)){
            return false;
        }
        HashSet<PLTLExp> first = getAllAndProps(this);
        HashSet<PLTLExp> second = getAllAndProps((And) obj);
        if(first.size() != second.size()){
            return false;
        }
        for (PLTLExp a: first) {
            boolean match = false;
            for (PLTLExp b: second) {
                if(a.equals(b)){
                    second.remove(b);
                    match = true;
                    break;
                }
            }
            if(!match){
                return false;
            }
        }
        return true;
    }

    HashSet<PLTLExp> getAllAndProps(And exp){
        HashSet<PLTLExp> results = new HashSet<>();
        if(exp.getLeft() instanceof And){
            results.addAll(getAllAndProps((And) exp.getLeft()));
        }else{
            results.add(exp.getLeft());
        }
        if(exp.getRight() instanceof And){
            results.addAll(getAllAndProps((And) exp.getRight()));
        }else{
            results.add(exp.getRight());
        }
        return results;
    }
}
