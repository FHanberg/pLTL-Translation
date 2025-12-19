package PLTL;

import java.util.ArrayList;
import java.util.HashSet;

public class Or extends Binary{

    public Or(PLTLExp left, PLTLExp right){
        super(left,right);
    }

    @Override
    public String getOp() {
        return "|";
    }

    @Override
    public <R> R accept(Visitor<R> v) {
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
        if(!(obj instanceof Or)){
            return false;
        }
        HashSet<PLTLExp> first = getAllOrProps(this);
        HashSet<PLTLExp> second = getAllOrProps((Or) obj);
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

    HashSet<PLTLExp> getAllOrProps(Or exp){
        HashSet<PLTLExp> results = new HashSet<>();
        if(exp.getLeft() instanceof Or){
            results.addAll(getAllOrProps((Or) exp.getLeft()));
        }else{
            results.add(exp.getLeft());
        }
        if(exp.getRight() instanceof Or){
            results.addAll(getAllOrProps((Or) exp.getRight()));
        }else{
            results.add(exp.getRight());
        }
        return results;
    }

    @Override
    public boolean dirtyEquals(PLTLExp target) {
        if(target instanceof And){
            HashSet<PLTLExp> first = getAllOrProps(this);
            HashSet<PLTLExp> second = getAllOrProps((Or) target);
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
