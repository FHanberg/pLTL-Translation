package PLTL;

import java.util.ArrayList;
import java.util.HashSet;

public class And extends Binary{

    public And(PLTLExp left, PLTLExp right){
        super(left, right);
    }

    @Override
    public String getOp() {
        return "&";
    }

    @Override
    public <R> R accept(PLTLExp.Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public <R, A> R accept(AltVisitor<R, A> v, A args) {
        return v.visit(this, args);
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof And)){
            return false;
        }
        ArrayList<PLTLExp> first = getAllAndProps(this);
        ArrayList<PLTLExp> second = getAllAndProps((And) obj);
        if(first.size() != second.size()){
            return false;
        }
        for (PLTLExp a: first) {
            boolean deeper = hasAndOr(a);
            String s = a.getString();
            boolean match = false;
            for (PLTLExp b: second) {
                boolean deeper2 = hasAndOr(b);
                if(deeper != deeper2)
                    return false;
                if (s.equals(b.getString())) {
                    second.remove(b);
                    match = true;
                    break;
                } else if (deeper) {
                    if (a.equals(b)) {
                        second.remove(b);
                        match = true;
                        break;
                    }
                }
            }
            if(!match){
                return false;
            }
        }
        return true;
    }

    public static ArrayList<PLTLExp> getAllAndProps(And exp){
        ArrayList<PLTLExp> results = new ArrayList<>();
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

    @Override
    public boolean dirtyEquals(PLTLExp target) {
        if(target instanceof And){
            ArrayList<PLTLExp> first = getAllAndProps(this);
            ArrayList<PLTLExp> second = getAllAndProps((And) target);
            if(first.size() != second.size()){
                return false;
            }
            for (PLTLExp a: first) {
                boolean deeper = hasAndOr(a);
                String s = a.getString();
                boolean match = false;
                for (PLTLExp b: second) {
                    boolean deeper2 = hasAndOr(b);
                    if(deeper != deeper2)
                        return false;
                    if (s.equals(b.getString())) {
                        second.remove(b);
                        match = true;
                        break;
                    } else if (deeper) {
                        if (a.dirtyEquals(b)) {
                            second.remove(b);
                            match = true;
                            break;
                        }
                    }
                }
                if(!match){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
