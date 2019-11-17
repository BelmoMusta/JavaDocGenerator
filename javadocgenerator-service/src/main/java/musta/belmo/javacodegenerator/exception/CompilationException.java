package musta.belmo.javacodegenerator.exception;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class CompilationException extends Exception {

    /**
     * The {@link #problemException} attribute.
     */
    private final ParseProblemException problemException;

    /**
     * The CompilationException class constructor.
     *
     * @param message{@link String}
     * @param cause{@link ParseProblemException}
     */
    public CompilationException(String message, ParseProblemException cause) {
        super(message, cause);
        this.problemException = cause;
    }

    /**
     * The CompilationException class constructor.
     *
     * @param cause{@link ParseProblemException}
     */
    public CompilationException(ParseProblemException cause) {
        super(cause);
        this.problemException = cause;
    }

    /**
     * @return Attribut {@link #problemException}
     */
    public ParseProblemException getProblemException() {
        return problemException;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Problem problem : problemException.getProblems()) {
            stringBuilder.append(problem.getMessage());
        }
        return stringBuilder.toString();
    }
}
