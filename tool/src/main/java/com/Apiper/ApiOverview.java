package com.Apiper;

import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class ApiOverview {
    private static JavaSymbolSolver symbolSolver;
    public static LinkedHashMap<ClassOrInterfaceDeclaration, List<MethodDeclaration>> classes = new LinkedHashMap<>();
    private static TypeSolver typeSolver;


    public ApiOverview(Path apiPath, Path jars){
        setUpSymbolSolver(apiPath, jars);
        exploreCompilationUnits(apiPath);
    }

    private void setUpSymbolSolver(Path apiPath, Path jarFiles){
        TypeSolver apiTypeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(apiPath.toFile())
        );
        if (jarFiles != null) {
            addJarsToTypeSolver(apiTypeSolver, jarFiles);
        }
        typeSolver = apiTypeSolver;
        symbolSolver = new JavaSymbolSolver(apiTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
    }

    private void addJarsToTypeSolver(TypeSolver apiTypeSolver, Path jarFilesPath){
        if(jarFilesPath.toFile().isDirectory()) {
            for (File file : jarFilesPath.toFile().listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    try {
                        ((CombinedTypeSolver) apiTypeSolver).add(new JarTypeSolver(file));
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    private void searchClasses(CompilationUnit cu){
        cu.accept(new ClassVisitor(), null);
    }

    private void exploreCompilationUnits(Path targetFolder){
        final ProjectRoot projectRoot =
                new SymbolSolverCollectionStrategy().collect(targetFolder);

        for (SourceRoot root: projectRoot.getSourceRoots()) {
            root.setParserConfiguration(StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver));
            for (ParseResult parseResult : root.tryToParseParallelized()){
                parseResult.ifSuccessful(cu ->
                        searchClasses((CompilationUnit)cu)
                );
            }
        }
    }

    private static class ClassVisitor extends VoidVisitorAdapter<Void>{

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg){
            classes.put(n, getPublicMethods(n));
            super.visit(n, arg);
        }
    }

    public static List<MethodDeclaration> getPublicMethods(ClassOrInterfaceDeclaration n){
        List<MethodDeclaration> methodDeclarationList = new ArrayList<>();
        for (MethodDeclaration methodDeclaration : n.getMethods()){
            if (methodDeclaration.isPublic()){
                methodDeclarationList.add(methodDeclaration);
            }
        }
        return methodDeclarationList;
    }

    public static JavaSymbolSolver getJavaSymbolSolver() {
        return symbolSolver;
    }

    public static TypeSolver getApiTypeSolver(){
        return typeSolver;
    }
}
