package com.Apiper;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.resolution.UnsolvedSymbolException;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

public class MethodApiOverview extends ApiOverview {
    public HashSet<String> classSignatures = new HashSet<>();
    public HashSet<String> methodSignatures = new HashSet<>();

    public MethodApiOverview(Path apiPath, Path jarFilesPath){
        super(apiPath, jarFilesPath);
        getClassSignatures(); //TODO save somewhere for quick reload
        //getMethodSignatures(); //TODO save somewhere for quick reload
    }

    private void getClassSignatures(){
        for (ClassOrInterfaceDeclaration declaration : classes.keySet()){
            try{
                // only look at public classes, abstract classes are meant to be overloaded anyways...
                if (declaration.isPublic()) {
                    classSignatures.add(declaration.resolve().getQualifiedName());
                }
            } catch (Exception e){
                //TODO logging
            } catch (StackOverflowError e){
                System.out.println("Could not get method signature due to Stack overflow: " +e.getMessage());
            }
        }
    }

    private void getMethodSignatures(){

        classes.values().stream().flatMap(List::stream).forEach(
                methodDeclaration -> {
                    try{
                        methodSignatures.add(methodDeclaration.resolve().getQualifiedSignature());
                    } catch (UnsolvedSymbolException e){
                        //System.out.println("Could not get method signature due to: " +e.getMessage());//TODO logging
                    } catch (StackOverflowError e){
                        System.out.println("Could not get method signature due to Stack overflow: " +e.getMessage());
                        //methodSignatures.add(methodDeclaration.getDeclarationAsString(false,false,false));
                    } catch (Exception e){
                        System.out.println("Something horrible happened: " +e.getMessage());
                    }
                });

    }
}
