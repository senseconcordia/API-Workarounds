package com.Apiper;


import Data.ApiperPatternGetPropertiesValues;
import Data.SaveFile;
import PatternFinders.ChangingFunctionalityPatternFinder;
import PatternFinders.DeepCopyPatternFinder;
import PatternFinders.MultiVersionPatternFinder;
import Extractor.classLoaderFinder.ClassLoaderFinder;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class ApiperPattern {
    public static ApiperPatternGetPropertiesValues properties;


    public static void main(String[] args) throws IOException{

        properties = new ApiperPatternGetPropertiesValues();

        createSaveFile();

        // write your code here

        if (args.length < 2) {
            throw argumentException();
        }

        final String option = args[0];

        if (option.equalsIgnoreCase("-h")) {
            printHelp();
        }

        else if (option.equalsIgnoreCase("-a")) {
            Path path = Paths.get(args[1]);
            if (properties.getSaveFilePath().toFile().exists()) {
                checkMultiVersionPattern(properties.getSaveFilePath(),
                        new ApiOverview(properties.getApiSource(),
                                properties.getJarFiles()), new ClassLoaderFinder(path));
            }
            else{
                throw new FileNotFoundException(path.toFile().toString());
            }
        }

        else if (option.equalsIgnoreCase("-b")) {
            Path path = Paths.get(args[1]);
            if (properties.getSaveFilePath().toFile().exists()) {
                checkChangingFunctionalityPattern(properties.getSaveFilePath(),
                        new MethodApiOverview(properties.getApiSource(),
                                properties.getJarFiles()));
            }
            else{
                throw new FileNotFoundException(path.toFile().toString());
            }
        }
        else if (option.equalsIgnoreCase("-c")) {
            Path path = Paths.get(args[1]);
            if (properties.getSaveFilePath().toFile().exists()) {
                checkDeepCopyPattern(properties.getSaveFilePath(),
                        new FieldApiOverview(properties.getApiSource(),
                                properties.getJarFiles()));
            }
            else{
                throw new FileNotFoundException(path.toFile().toString());
            }
        }

        else if (option.equalsIgnoreCase("-all")){
            Path path = Paths.get(args[1]);

            gatherMultiVersionPatterns(path);
            gatherChangingFunctionalityPatterns(path);
            gatherDeepCopyPattern(path);
        }

        else if (option.equalsIgnoreCase("-multiversion")){
            Path path = Paths.get(args[1]);

            gatherMultiVersionPatterns(path);
        }

        else if (option.equalsIgnoreCase("-changingFunctionality")){
            Path path = Paths.get(args[1]);

            gatherChangingFunctionalityPatterns(path);
        }
    }

    private static void gatherMultiVersionPatterns(Path path) {
        ApiOverview apiOverview = new ApiOverview(properties.getApiSource(), properties.getJarFiles());

        if (path.toFile().isDirectory()) {
            for (Path folder : getFoldersInDir(path)) {
                System.out.println("Checking project: " + folder.toString());
                ClassLoaderFinder classLoaderFinder = new ClassLoaderFinder(folder);
                checkMultiVersionPattern(folder, apiOverview, classLoaderFinder);
            }
        }
        System.out.println("Done checking multiVersionPatterns");
    }

    private static void gatherChangingFunctionalityPatterns(Path path) {
        MethodApiOverview methodApiOverview = new MethodApiOverview(properties.getApiSource(), properties.getJarFiles());

        if (path.toFile().isDirectory()) {
            for (Path folder : getFoldersInDir(path)) {
                System.out.println("Checking project: " + folder.toString());
                checkChangingFunctionalityPattern(folder, methodApiOverview);
            }
        }
        System.out.println("Done checking ChangingFunctionalityPattern");
    }

    private static void gatherDeepCopyPattern(Path path){
        FieldApiOverview fieldApiOverview = new FieldApiOverview(properties.getApiSource(), properties.getJarFiles());

        if (path.toFile().isDirectory()) {
            for (Path folder : getFoldersInDir(path)) {
                System.out.println("Checking project: " + folder.toString());
                checkDeepCopyPattern(folder, fieldApiOverview);
            }
        }
        System.out.println("Done checking DeepCopyPattern");
    }

    private static void checkMultiVersionPattern(Path checkPath, ApiOverview apiOverview, ClassLoaderFinder classLoaderFinder){
        MultiVersionPatternFinder multiVersionPatternFinder = new MultiVersionPatternFinder();
        multiVersionPatternFinder.findPatternInstances(checkPath, apiOverview, classLoaderFinder);
    }

    private static void checkChangingFunctionalityPattern(Path checkPath, MethodApiOverview apiOverview){
        ChangingFunctionalityPatternFinder changingFunctionalityPatternFinder = new ChangingFunctionalityPatternFinder();
        changingFunctionalityPatternFinder.findPatternInstances(checkPath, apiOverview);
    }

    private static void checkDeepCopyPattern(Path checkPath, FieldApiOverview apiOverview){
        DeepCopyPatternFinder deepCopyPatternFinder = new DeepCopyPatternFinder();
        deepCopyPatternFinder.findPatternInstances(checkPath, apiOverview);
    }

    private static IllegalArgumentException argumentException() {
        return new IllegalArgumentException("Type 'Exparser -h' for help.");
    }

    private static void createSaveFile(){
        SaveFile saveFile = SaveFile.getInstance();
        saveFile.createNewSaveFile(properties.getSaveFilePath());
    }

    private static void printHelp(){
        System.out.println("-h\t\t\t\t\t\t\tShow tips");
        System.out.println("-a <folder>");

    }

    TypeSolver myTypeSolver = new CombinedTypeSolver(
            new ReflectionTypeSolver(),
            new JavaParserTypeSolver(new File(""))

            );

    private static HashSet<Path> getFoldersInDir(Path directory){
        HashSet<Path> folders = new HashSet<>();
        HashSet<String> projectsSearched = getProjectsSearched();

        for (File folder : directory.toFile().listFiles()){
            if (projectsSearched.contains(folder.getName())) {
                folders.add(folder.toPath());
            }
        }

        return folders;
    }

    private static HashSet<String> getProjectsSearched(){
        HashSet<String> projects = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(properties.getAllowedProjectsFile().toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String folderName = values[0].split("/")[0];
                projects.add(folderName);
            }
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }


        return projects;
    }
}
