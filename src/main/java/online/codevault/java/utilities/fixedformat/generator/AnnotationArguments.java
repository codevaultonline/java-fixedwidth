package online.codevault.java.utilities.fixedformat.generator;

import java.util.ArrayList;
import java.util.List;

public class AnnotationArguments {

    private List<AnnotationArgument> arguments = new ArrayList<>();

    public void addArgument(AnnotationArgument argument) {
        arguments.add(argument);
    }

    public List<AnnotationArgument> getArguments() {
        return arguments;
    }

}
