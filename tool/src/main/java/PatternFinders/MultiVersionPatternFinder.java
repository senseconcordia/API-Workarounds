package PatternFinders;
import Patterns.MultiVersionPattern;
import Extractor.classLoaderFinder.ClassLoaderFinder;
import com.Apiper.ApiOverview;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MultiVersionPatternFinder extends PatternFinder {
    ClassLoaderFinder classLoaderFinder;

    public void findPatternInstances(Path targetFolder, ApiOverview apiSource, ClassLoaderFinder classLoaderFinder) {
        this.classLoaderFinder = classLoaderFinder;
        super.findPatternInstances(targetFolder, apiSource);
    }

    // detect class loader instances after if conditions
    // taken from : https://dzone.com/articles/java-classloader-handling
    // gotten from : https://stackoverflow.com/questions/31884133/how-can-i-use-two-versions-of-the-same-library-in-the-same-project-in-java
    // also detect weaker version from Google Android (by config)
    public void findPatternInstances(CompilationUnit cu){

        // java.net.URLClassLoader
        try {
            /*
            if (importsContainDesiredKind(cu, "java.net.URLClassLoader")) {
                // Look for urls with .jar, check if that are multiple?
                // other way: Check to see if after if statement you have a call to classloader, or class that imports classloader?
                if (classLoaderInCondition(cu)){
                    addPatternInstance(cu, 305);
                }
                addPatternInstance(cu, 203);
            }
            /*
            if (containsDesiredIfStatement(cu, "Build.VERSION.")) {
                addPatternInstance(cu, 206);
            }
            if (usesMultipleJars(cu)){
                addPatternInstance(cu, 429);
            }
*/          if (classLoaderFinder.classCallsAClassLoader(cu)) {
                searchWithJarAnalyzer(cu);
            }

        } catch (StackOverflowError e){
            System.out.println("Stack Overflowed in: " + cu.getStorage().get().getPath().toString() + " line46 of MVPF");
        }

    }

    private void searchWithJarAnalyzer(CompilationUnit cu){
        HashSet<Integer> problemLinesFound = new HashSet<>();

        for (MethodCallExpr expression: cu.findAll(MethodCallExpr.class)) {
            Set<String> potentialJarsUsed = apiFinder.findMostRecentJarThatContainsMethod(transformImportsToStrings(cu), expression.getName().asString(), expression.getArguments().size());
            if (potentialJarsUsed.size() > 0){
                if (potentialJarsUsed.size() != this.getMaxJarsSupported() && this.getMaxJarsSupported() > 0){
                    problemLinesFound.add(expression.getBegin().get().line);
                }
                if (potentialJarsUsed.size() > this.getMaxJarsSupported()){
                    this.setMaxJarsSupported(potentialJarsUsed.size());
                }
            }
        }
        addPatternsFound(problemLinesFound, cu);
    }

    private void addPatternsFound(HashSet<Integer> lines, CompilationUnit cu ){
        for (int i : lines){
            addPatternInstance(cu, i);
        }
    }

    private boolean objectExtensionOfClassLoader(CompilationUnit cu){
        List<ObjectCreationExpr> expressions = cu.findAll(ObjectCreationExpr.class);
        for (ObjectCreationExpr expression: expressions) {
            expression.getType().resolve().getAllClassesAncestors();
        }
        return false;
    }

    private boolean importsContainDesiredKind(CompilationUnit cu, String desiredImport){

        if (cu.getImports().stream().anyMatch(importDeclaration ->
            importDeclaration.getName().toString().equalsIgnoreCase(desiredImport))){
            return true;
        }
        return false;
    }

    private boolean classLoaderInCondition(CompilationUnit cu){

        for (IfStmt ifStmt : cu.findAll(IfStmt.class)) {
            if (ifStmt.hasThenBlock()) {
                if (ifStmt.getThenStmt().toString().toLowerCase().contains("classloader")) {
                    return true;
                }
            } else if (ifStmt.hasElseBlock()) {
                if (ifStmt.getElseStmt().toString().toLowerCase().contains("classloader")) {
                    return true;
                }
            }
        }
        return false;
    }
    /*
    private boolean usesMultipleJars(CompilationUnit cu){
        HashSet<String> jarsUsed = new HashSet<>();

        List<ObjectCreationExpr> expressions = cu.findAll(ObjectCreationExpr.class);
        List<MethodCallExpr> methodExpression = cu.findAll(MethodCallExpr.class);
        for (ObjectCreationExpr expression: expressions) {
            jarsUsed.addAll(getJarUsed(resolveType(expression)));
        }

        for (MethodCallExpr methodCallExpr: methodExpression){
            jarsUsed.addAll(getJarUsed(resolveType(methodCallExpr)));
        }

        boolean multipleJarsUsed = jarsUsed.size() > 1;

        return multipleJarsUsed;
    }*/

    private ResolvedTypeDeclaration resolveType(ObjectCreationExpr typeDeclaration){
        ResolvedTypeDeclaration resolvedType = null;
        try{
            resolvedType = typeDeclaration.resolve().declaringType();
        } catch (UnsolvedSymbolException e){
            //System.out.println(e.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return resolvedType;
    }

    private ResolvedTypeDeclaration resolveType(MethodCallExpr methodCallExpr){
        ResolvedTypeDeclaration resolvedType = null;
        try{
            resolvedType = methodCallExpr.resolve().declaringType();
        } catch (UnsolvedSymbolException e){
            //System.out.println(e.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return resolvedType;
    }

    private void importCheck(CompilationUnit cu){
        for (ImportDeclaration importDeclaration: cu.getImports()){
            importDeclaration.findRootNode();
        }
    }
    /*
    private HashSet<String> getJarUsed(ResolvedTypeDeclaration resolvedTypeDeclaration){
        HashSet<String> foundJars = new HashSet<>();

        if (resolvedTypeDeclaration != null) {
            if(resolvedTypeDeclaration instanceof JavassistInterfaceDeclaration){
                CtClass test = ((JavassistInterfaceDeclaration) resolvedTypeDeclaration).getCtClass();
                ClassPool poolTest = test.getClassPool();
                foundJars.addAll(getInstancesOfSubstring(poolTest.toString()));
            }
            else if (resolvedTypeDeclaration instanceof JavassistClassDeclaration){
                CtClass test = ((JavassistClassDeclaration) resolvedTypeDeclaration).getCtClass();
                ClassPool poolTest = test.getClassPool();
                foundJars.addAll(getInstancesOfSubstring(poolTest.toString()));
            }
        }
        return foundJars;
    }
    */


    private HashSet<String> getInstancesOfSubstring(String string){
        HashSet<String> foundSubstrings = new HashSet<>();

        Pattern pattern = Pattern.compile("file.*\\.jar");
        Matcher m = pattern.matcher(string);
        while (m.find()){
            foundSubstrings.add(m.group());
        }
        return foundSubstrings;
    }

    private boolean containsDesiredIfStatement(CompilationUnit cu, String desiredIf){

        if (cu.findAll(IfStmt.class).stream().anyMatch(node ->
            node.getCondition().toString().contains(desiredIf))){
            return true;
        }
        return false;
    }

    @Override
    protected void addPatternInstance(CompilationUnit cu, int lineNumber){
        MultiVersionPattern multiVersionPattern = new MultiVersionPattern(cu);
        multiVersionPattern.setStartLineNumber(lineNumber);
        outputPatternInstance(multiVersionPattern);
    }

}
