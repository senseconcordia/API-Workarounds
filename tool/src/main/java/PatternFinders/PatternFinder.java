package PatternFinders;

import Data.SaveFile;
import Patterns.PatternInstance;
import Extractor.APIFinderImpl;
import com.Apiper.ApiOverview;
import com.Apiper.ApiperPattern;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public abstract class PatternFinder {
    LinkedList<PatternInstance> patternInstances = new LinkedList<>();
    private ApiOverview apiSource = null;
    protected Path targetFolder = null;
    protected APIFinderImpl apiFinder;
    protected int maxJarsSupported = 0;

    public void findPatternInstances(Path targetFolder, ApiOverview apiSource) {
        this.apiSource = apiSource;
        this.targetFolder = targetFolder;
        this.apiFinder = new APIFinderImpl(targetFolder.toString(), ApiperPattern.properties.getJarFiles().toString());
        findPatternInstances(targetFolder);
    }

    public void findPatternInstances(Path targetFolder){
        try {
            exploreCompilationUnits(targetFolder);
            outputPatternInstances();
        } catch (RuntimeException e){
            System.out.println("Could not find instances in: " + targetFolder.toString());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public abstract void findPatternInstances(CompilationUnit cu);

    private void exploreCompilationUnits(Path targetFolder) throws RuntimeException{


        for (File file : getFilesFromFolder(targetFolder)){
            try {
                //javaparser does not support module files, which ahppen to be named module-info.java by design, so we exclude them here.
                if (!file.getName().contains("module-info.java")) {
                    findPatternInstances(StaticJavaParser.parse(file));
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


    public static HashSet<File> getFilesFromFolder(Path folder){
        HashSet<File> projectFiles = new HashSet<>();

        File dir = folder.toFile();
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null){
            for(File child: directoryListing){
                if(child.isDirectory()){
                    projectFiles.addAll(getFilesFromFolder(child.toPath()));
                }
                else if(child.getAbsolutePath().endsWith(".java")){
                    projectFiles.add(child);
                }
            }
        }

        return projectFiles;
    }

    // parses files in a one-at-a-time fashion to ensure that all files are processed if a project structure is absent.
    private void handleSingletonFiles(Path targetFolder){

        for (File file : targetFolder.toFile().listFiles()){
            if (file.getAbsolutePath().endsWith(".java")){
                try {
                    findPatternInstances(StaticJavaParser.parse(file));
                }catch (IOException e){
                    new RuntimeException(e);
                } catch (StackOverflowError e){
                    System.out.println("Stack overflowed on singleton parsing");
                }
            }
        }
    }

    private void outputPatternInstances() {
        SaveFile saveFile = SaveFile.getInstance();
        for (PatternInstance instance : patternInstances){
            saveFile.writeToSaveFile(instance.toString());
        }
    }

    public void outputPatternInstance(PatternInstance instance){
        SaveFile saveFile = SaveFile.getInstance();
        saveFile.writeToSaveFile(instance.toString());
    }

    public ApiOverview getApiSource(){
        return apiSource;
    }

    public int getMaxJarsSupported(){return maxJarsSupported;}

    public void setMaxJarsSupported(int apisUsed){
        this.maxJarsSupported = apisUsed;
    }

    protected List<String> transformImportsToStrings(CompilationUnit cu){
        List<String> stringifiedImports = new ArrayList<>();
        for (ImportDeclaration importDeclaration : cu.getImports()){
            String importName = importDeclaration.getName().asString();
            stringifiedImports.add(importName);
        }
        return stringifiedImports;
    }

    protected abstract void addPatternInstance(CompilationUnit cu, int lineNumber);
}
