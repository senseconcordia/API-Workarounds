package Extractor.classLoaderFinder;

import PatternFinders.PatternFinder;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

// Contains code borrowed and inspired by Nikolaos Tsantalis, with permission
public class ClassLoaderFinder {
    CompilationUnit compilationUnit;
    boolean extendsClassLoader = false;
    HashSet<String> classNames = new HashSet<>();
    HashSet<File> filesToBrowse = new HashSet<>();


    public ClassLoaderFinder(CompilationUnit compilationUnit){
        this.compilationUnit = compilationUnit;
        extendsClassLoader();
    }

    public ClassLoaderFinder(Path folder){
        filesToBrowse = PatternFinder.getFilesFromFolder(folder);
        setUpSymbolSolver();
        exploreCompilationUnits();
    }

    private void setUpSymbolSolver(){
        TypeSolver apiTypeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver()
        );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(apiTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
    }

    private void exploreCompilationUnits() throws RuntimeException{

        for (File file : filesToBrowse){
            try {
                //javaparser does not support module files, which ahppen to be named module-info.java by design, so we exclude them here.
                if (!file.getName().contains("module-info.java")) {
                    this.compilationUnit = StaticJavaParser.parse(file);
                    extendsClassLoader();
                }}
            catch (StackOverflowError e) {
                System.out.println("Stack Overflowed on parsing compilation units");
            } catch (IOException e){
                System.out.println(e.getMessage());
            } catch (OutOfMemoryError e){
                System.out.println("Too large, ran out of memory " + e.getMessage());
            } catch (ParseProblemException e){
                System.out.println("Problem parsing file: " + file.getAbsolutePath());
            }
        }
    }

    private void extendsClassLoader(){

        List<ClassOrInterfaceDeclaration> declarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration declaration : declarations){
            getMatchingExtendedTypes(declaration);
        }
    }

    private void getMatchingExtendedTypes(ClassOrInterfaceDeclaration declaration){

        for (ClassOrInterfaceType extendedType: declaration.getExtendedTypes()) {
            try{
                ResolvedReferenceType resolvedType = extendedType.resolve();
                checkIfExtensionOfClassLoader(resolvedType, declaration);

                for (ResolvedReferenceType ancestor : resolvedType.getAllClassesAncestors()) {
                    checkIfExtensionOfClassLoader(ancestor, declaration);
                }
            } catch (Exception e){
                // Leave this empty, we don't care if we skip a few
            }
        }
    }

    private void checkIfExtensionOfClassLoader(ResolvedReferenceType potentialExtension, ClassOrInterfaceDeclaration declaration){
        if (potentialExtension.getQualifiedName() == "java.lang.ClassLoader") {
            extendsClassLoader = true;
            classNames.add(declaration.getNameAsString());
        }
    }

    public boolean classCallsAClassLoader(CompilationUnit compilationUnit){
        List<ObjectCreationExpr> expressions = compilationUnit.findAll(ObjectCreationExpr.class);
        for (ObjectCreationExpr expression: expressions) {
            if (classNames.contains(expression.getType().getName().asString())){
                return true;
            }
        }
        return false;
    }

    public boolean foundClassLoaderExtension(){
        return extendsClassLoader;
    }

    public HashSet<String> getExtendingClassNames(){
        return classNames;
    }
}
