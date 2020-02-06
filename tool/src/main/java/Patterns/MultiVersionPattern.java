package Patterns;

import com.github.javaparser.ast.CompilationUnit;

public class MultiVersionPattern extends PatternInstance {

    public MultiVersionPattern(CompilationUnit cu){
        super(cu, "MultiVersionPattern");
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(this.getType());
        sb.append(": ");
        sb.append(this.getFile());
        sb.append(", in line: ");
        sb.append(this.getStartLineNumber());

        return sb.toString();
    }
}
