package musta.belmo.javacodegenerator.exception;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;

public class CompilationException extends Exception {

    private final ParseProblemException problemException;

    public CompilationException(String message, ParseProblemException cause) {
        super(message, cause);
        this.problemException = cause;
    }

    public CompilationException(ParseProblemException cause) {
        super(cause);
        this.problemException = cause;

    }

    public ParseProblemException getProblemException() {
        return problemException;
    }

    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Problem problem : problemException.getProblems()) {
            stringBuilder.append(problem.getMessage());
        }
        return stringBuilder.toString();
    }
}
