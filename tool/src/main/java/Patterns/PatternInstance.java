package Patterns;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.nio.file.Path;

public abstract class PatternInstance {

    private Path folder;
    private File file;
    private int startLineNumber;
    private String type;

    PatternInstance(CompilationUnit cu, String pattern){
        this.type = pattern;
        this.setFolder(cu.getStorage().get().getPath());
        this.setFile(cu.getStorage().get().getPath().toFile());
    }

    public void setFolder(Path folder) {
        this.folder = folder;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setStartLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    final public String getType() {
        return type;
    }

    final public Path getFolder() {
        return folder;
    }

    final public File getFile() {
        return file;
    }

    final public int getStartLineNumber() {
        return startLineNumber;
    }

    public abstract String toString();
}
