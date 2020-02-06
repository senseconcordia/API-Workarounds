package visitors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ImportVisitor extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(ImportDeclaration importDeclaration, Void arg){
        super.visit(importDeclaration, arg);
        System.out.println(importDeclaration.getName());
    }
}
