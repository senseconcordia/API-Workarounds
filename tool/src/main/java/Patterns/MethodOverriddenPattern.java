package Patterns;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodOverriddenPattern extends ChangingFunctionalityPattern{

    public String methodOverridden;

    public MethodOverriddenPattern(CompilationUnit cu){
        super(cu);
    }

    public MethodOverriddenPattern(CompilationUnit cu, MethodDeclaration methodDeclaration){
        super(cu);

        setMethodOverridden(generateCallName(methodDeclaration));
    }

    public String getMethodOverridden() {
        return methodOverridden;
    }

    public void setMethodOverridden(String methodOverridden) {
        this.methodOverridden = methodOverridden;
    }

    private String generateCallName(MethodDeclaration methodDeclaration){

        return methodDeclaration.resolve().getQualifiedSignature();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(this.getType());
        sb.append(": ");
        sb.append(this.getFile());
        sb.append(", overridden: ");
        sb.append(this.methodOverridden);

        return sb.toString();
    }
}
