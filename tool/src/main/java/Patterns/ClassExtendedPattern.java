package Patterns;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;


public class ClassExtendedPattern extends ChangingFunctionalityPattern {
    public String classExtended;

    public ClassExtendedPattern(CompilationUnit cu){
        super(cu);
    }

    public ClassExtendedPattern(CompilationUnit cu, ClassOrInterfaceDeclaration extendedType){
        super(cu);
        setClassExtended(extendedType);
    }

    public void setClassExtended(ClassOrInterfaceDeclaration classExtended) {
        try {
            this.classExtended = classExtended.resolve().getQualifiedName();
        } catch(Exception e){
            System.out.println("Problem with setting an extended class: " + e.getMessage());
            this.classExtended = classExtended.getParentNode().get().findCompilationUnit().get().getPackageDeclaration().get().getName().asString();
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(this.getType());
        sb.append(": ");
        sb.append(this.getFile());
        sb.append(", extended: ");
        sb.append(this.classExtended);

        return sb.toString();
    }
}
