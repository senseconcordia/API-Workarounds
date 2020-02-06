package com.Apiper;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FieldApiOverview extends ApiOverview {
    HashMap<String, List<FieldDeclaration>> fieldMap = new HashMap<>();
    HashSet<ResolvedFieldDeclaration> fieldSet = new HashSet<>();
    HashSet<String> accessors = new HashSet<>();

    public FieldApiOverview(Path apiPath, Path jarFilesPath){
        super(apiPath, jarFilesPath);
        obtainFields();
        gatherAccessorMethods();
    }

    private void obtainFields(){
        for (ClassOrInterfaceDeclaration item : classes.keySet()){
            fieldMap.put(item.resolve().getQualifiedName(), null);
            //generateFieldSet(item);
        }
    }

    private void generateFieldSet(ClassOrInterfaceDeclaration declaration){
        for (FieldDeclaration fieldDeclaration: declaration.getFields()){
            if (fieldDeclaration.isPublic()) {
                try {
                    fieldSet.add(fieldDeclaration.resolve().asField());
                } catch (Exception e) {
                    System.out.println(e.getMessage()); //TODO logging
                }
            }
        }
    }



    // TODO could improve by looking to see if return value of get methods is a field in the class, but this will make method less efficient
    private void gatherAccessorMethods(){

        classes.values().stream().flatMap(List::stream).forEach(
                methodDeclaration -> {
                    if (methodDeclaration.isPublic() &&
                            methodDeclaration.getName().asString().contains("get")) {
                            saveMethodSignature(methodDeclaration);
                    }
                });
    }

    private void saveMethodSignature(MethodDeclaration methodDeclaration){
        ResolvedMethodDeclaration resolvedMethodDeclaration = null;
        try{
            resolvedMethodDeclaration = methodDeclaration.resolve();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        if (resolvedMethodDeclaration != null) {
            try {
                accessors.add(resolvedMethodDeclaration.getQualifiedSignature());
            } catch (UnsolvedSymbolException e){
                //System.out.println("Could not get method signature due to UnsolvedSymbol: " +e.getMessage());
            } catch (StackOverflowError e){
                System.out.println("Could not get method signature due to Stack overflow: " +e.getMessage());
            }
        }
    }

    public HashSet<ResolvedFieldDeclaration> getFieldSet() {
        return fieldSet;
    }

    public HashSet<String> getAccessors() { return accessors; }

    public HashMap<String, List<FieldDeclaration>> getFieldMap() {
        return fieldMap;
    }
}
