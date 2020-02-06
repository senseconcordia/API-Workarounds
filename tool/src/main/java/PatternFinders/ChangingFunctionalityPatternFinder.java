package PatternFinders;

import Patterns.ChangingFunctionalityPattern;
import Patterns.ClassExtendedPattern;
import Patterns.MethodOverriddenPattern;
import com.Apiper.MethodApiOverview;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ChangingFunctionalityPatternFinder extends PatternFinder {
    private MethodApiOverview apiSource;

    public void findPatternInstances(Path targetFolder, MethodApiOverview apiSource) {
        this.apiSource = apiSource;
        super.findPatternInstances(targetFolder);
    }

    @Override
    public void findPatternInstances(CompilationUnit cu) {
        classIsExtended(cu);
        methodIsOverRidden(cu);
        classIsUsed(cu);
        methodIsInvoked(cu);
    }

    private void classIsExtended(CompilationUnit compilationUnit){

        List<ClassOrInterfaceDeclaration> declarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration declaration : declarations){
            getMatchingExtendedTypes(declaration, compilationUnit);
        }
    }

    private void getMatchingExtendedTypes(ClassOrInterfaceDeclaration declaration, CompilationUnit cu){

        for (ClassOrInterfaceType extendedType: declaration.getExtendedTypes()) {
            if (importsContainsClass(extendedType.getNameAsString(), cu)) {
                try {
                    Optional<ClassOrInterfaceDeclaration> overrridenDeclaration = apiSource.classes.keySet()
                            .stream()
                            .filter(apiClass -> apiClass.getName().equals(extendedType.getName()))
                            .findFirst();

                    if (overrridenDeclaration.isPresent()) {
                        patternInstances.add(new ClassExtendedPattern(cu, overrridenDeclaration.get()));
                    }
                } catch (StackOverflowError e) {
                    // TODO get some logging
                    System.out.println("Stack Overflowed on: " + cu.getStorage().get().getPath().toString() + " line58 of CFPF");
                }
            }
        }
    }

    private boolean importsContainsClass(String className, CompilationUnit cu){

        for(String officialAPISignature: apiSource.classSignatures){
            if (officialAPISignature.contains(className)){
                for (ImportDeclaration importDeclaration: cu.getImports()){
                    if(importDeclaration.getNameAsString().contains(officialAPISignature)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean importsContainsMethod(MethodDeclaration methodDeclaration, CompilationUnit cu){

        for(ClassOrInterfaceDeclaration classDeclaraction: apiSource.classes.keySet()){
            if (apiSource.classes.get(classDeclaraction).contains(methodDeclaration)){
                if (importsContainsClass(classDeclaraction.getNameAsString(), cu)){
                    return true;
                }
            }
        }

        return false;
    }

    private void methodIsOverRidden(CompilationUnit compilationUnit){

        for (MethodDeclaration methodDeclaration : compilationUnit.findAll(MethodDeclaration.class)){
            if (methodDeclaration.getAnnotationByName("Override").isPresent()){
                try {
                    Optional<MethodDeclaration> overrridenDeclaration = apiSource.classes.values()
                            .stream()
                            .flatMap(List::stream)
                            .filter(declarationNode -> methodDeclaration.getSignature().equals(declarationNode.getSignature()))
                            .findAny();

                    if (overrridenDeclaration.isPresent()) {
                        if (importsContainsMethod(overrridenDeclaration.get(), compilationUnit)) {
                            patternInstances.add(new MethodOverriddenPattern(compilationUnit, overrridenDeclaration.get()));
                        }
                    }
                } catch (StackOverflowError e){
                    // TODO get some logging
                    System.out.println("Stack Overflowed on: " + compilationUnit.getStorage().get().getPath().toString()+ " line78 of CFPF");
                }
            }
        }
    }

    private void classIsUsed(CompilationUnit compilationUnit){
        List<ObjectCreationExpr> expressions = compilationUnit.findAll(ObjectCreationExpr.class);
        for (ObjectCreationExpr expression: expressions){
            try{
                String classSignature = expression.resolve().declaringType().getQualifiedName();
                if (apiSource.classSignatures.contains(classSignature)){
                    ChangingFunctionalityPattern newPattern = new ChangingFunctionalityPattern(compilationUnit);
                    newPattern.setClassInvoked(classSignature);
                    patternInstances.add(newPattern);
                }
            } catch (Exception e){
                // TODO get some logging
                //System.out.println("Check exception in classUsage: "+ e.getMessage());
                //simpletonClassUsage(expression, compilationUnit);
            } catch (StackOverflowError e){
                // TODO get some logging
                System.out.println("Stack Overflowed on: " + compilationUnit.getStorage().get().getPath().toString() + " line99 of CFPF");
                //simpletonClassUsage(expression, compilationUnit);
            }
        }
    }

    // This finds the resolved signature for a method expression, if it is in our list of API methods it claims it is used
    // It is also possible to simply find whether the class of a method is part of our API and then that's good enough as well...
    // This is much faster so we do it this way.
    private void methodIsInvoked(CompilationUnit compilationUnit){

        List<MethodCallExpr> expressions = compilationUnit.findAll(MethodCallExpr.class);
        for (MethodCallExpr expression: expressions){
            try {
                String classSignature = expression.resolve().declaringType().getQualifiedName();
                if (apiSource.classSignatures.contains(classSignature)){
                    ChangingFunctionalityPattern newPattern = new ChangingFunctionalityPattern(compilationUnit);
                    newPattern.setMethodInvoked(expression.resolve().getQualifiedSignature());
                    patternInstances.add(newPattern);
                }
            } catch (Exception e){
                // TODO get some logging
                System.out.println("Check exception in methodUsage: "+ e.getMessage());
            } catch (StackOverflowError e){
                // TODO get some logging
                System.out.println("Stack Overflowed on: " + compilationUnit.getStorage().get().getPath().toString() + " line124 of CFPF");
            }

        }
    }

    @Override
    protected void addPatternInstance(CompilationUnit cu, int lineNumber) {
        ChangingFunctionalityPattern changingFunctionalityPattern = new ChangingFunctionalityPattern(cu);
        changingFunctionalityPattern.setStartLineNumber(lineNumber);
        outputPatternInstance(changingFunctionalityPattern);
    }
}
