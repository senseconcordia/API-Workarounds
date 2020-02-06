package PatternFinders;

import Patterns.DeepCopyPattern;
import Extractor.APIFinderImpl;
import com.Apiper.ApiperPattern;
import com.Apiper.FieldApiOverview;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import java.nio.file.Path;
import java.util.List;

// Counts field usage for given objects
// Could also count method usage for given objects and how often results are used rather than copied for sake of copying
// how often things are used together should also be explored
public class DeepCopyPatternFinder extends PatternFinder {
    private FieldApiOverview apiOverview = null;

    public void findPatternInstances(Path targetFolder, FieldApiOverview apiOverview){
        this.apiOverview = apiOverview;
        this.apiFinder = new APIFinderImpl(targetFolder.toString(), ApiperPattern.properties.getJarFiles().toString());
        super.findPatternInstances(targetFolder);
    }

    @Override
    public void findPatternInstances(CompilationUnit cu) {

        fieldAccessed(cu);
        accessorMethodUsed(cu);
    }

    private void fieldAccessed(CompilationUnit compilationUnit){
        List<FieldAccessExpr> expressions = compilationUnit.findAll(FieldAccessExpr.class);
        for (FieldAccessExpr expression: expressions) {
            String expressionName = expression.getName().asString();
            if (apiFinder.findAllFields(transformImportsToStrings(compilationUnit), expressionName).size() > 0){
                DeepCopyPattern newPattern = new DeepCopyPattern(compilationUnit);
                newPattern.setFieldInvoked(expression.toString());
                newPattern.setClassInvolved(compilationUnit.getStorage().get().getFileName());
                patternInstances.add(newPattern);
            }
            else if (!expression.getParentNode().get().toString().contains(expression + ".new")) {
                try{
                    determineFieldAccessed(expression, compilationUnit);
                } catch (StackOverflowError e){
                    //TODO get some logging
                    System.out.println("Stack Overflowed on: "+ compilationUnit.getStorage().get().getPath().toString());
                }
            }
        }
    }

    private void determineFieldAccessed(FieldAccessExpr expression, CompilationUnit compilationUnit){
        try {
                    /*
                    TODO add logging and include those two statements as Debug
                    String whereIs = compilationUnit.getStorage().get().getPath().toString();
                    System.out.println(expression + ": " + whereIs + " " + expression.getBegin().get().line);
                    */
            String resolvedClass = expression.calculateResolvedType().asReferenceType().getQualifiedName();

            if (apiOverview.getFieldMap().keySet().contains(resolvedClass)) {
                DeepCopyPattern newPattern = new DeepCopyPattern(compilationUnit);
                newPattern.setFieldInvoked(expression.toString());
                newPattern.setClassInvolved(resolvedClass);
                patternInstances.add(newPattern);
            }
        } catch (Exception e) {
            // TODO get some logging
            //System.out.println("Check exception: "+ e.getMessage());
        } catch (StackOverflowError e){
            // TODO get some logging
            System.out.println("Stack Overflowed on: " + compilationUnit.getStorage().get().getPath().toString());
        }
    }

    private void accessorMethodUsed(CompilationUnit compilationUnit){
        List<MethodCallExpr> expressions = compilationUnit.findAll(MethodCallExpr.class);
        for (MethodCallExpr expression: expressions){
            try{
                ResolvedMethodDeclaration method = JavaParserFacade.get(apiOverview.getApiTypeSolver()).solve(expression).getCorrespondingDeclaration();
                if (apiOverview.getAccessors().contains(method.getQualifiedSignature())){

                        DeepCopyPattern newPattern = new DeepCopyPattern(compilationUnit);
                        newPattern.setAccessorMethod(method.getQualifiedSignature());
                        patternInstances.add(newPattern);

                }
            } catch (Exception e){
                // TODO get some logging
            } catch (StackOverflowError e){
                //TODO get some logging
                System.out.println("Stack Overflowed on: "+ compilationUnit.getStorage().get().getPath().toString());
            }
        }
    }

    @Override
    protected void addPatternInstance(CompilationUnit cu, int lineNumber) {
        DeepCopyPattern deepCopyPattern = new DeepCopyPattern(cu);
        deepCopyPattern.setStartLineNumber(lineNumber);
        outputPatternInstance(deepCopyPattern);
    }
}
