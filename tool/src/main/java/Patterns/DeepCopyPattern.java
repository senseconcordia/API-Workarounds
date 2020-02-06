package Patterns;

import com.github.javaparser.ast.CompilationUnit;

public class DeepCopyPattern extends PatternInstance {
    private String fieldInvoked = null;
    private String classInvolved = null;
    private String accessorMethod = null;

    public DeepCopyPattern(CompilationUnit compilationUnit){
        super(compilationUnit,"DeepCopyPattern");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getType());
        sb.append(String.format(", in file: %s", this.getFile()));

        if (fieldInvoked != null){
            sb.append(
                    String.format(" field: %s from class: %s was invoked",
                            this.getFieldInvoked(),
                            this.getClassInvolved())
            );
        } else if (accessorMethod != null){
            sb.append(
                    String.format("Accessor Method: %s was invoked",
                            this.getAccessorMethod())
            );
        }
        return sb.toString();
    }

    public String getFieldInvoked() {
        return fieldInvoked;
    }

    public void setFieldInvoked(String fieldInvoked) {
        this.fieldInvoked = fieldInvoked;
    }


    public String getClassInvolved() {
        return classInvolved;
    }

    public void setClassInvolved(String classInvolved) {
        this.classInvolved = classInvolved;
    }

    public String getAccessorMethod() {
        return accessorMethod;
    }

    public void setAccessorMethod(String accessorMethod) {
        this.accessorMethod = accessorMethod;
    }
}
