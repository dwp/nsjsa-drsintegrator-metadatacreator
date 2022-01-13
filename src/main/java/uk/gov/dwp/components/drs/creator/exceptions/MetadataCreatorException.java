package uk.gov.dwp.components.drs.creator.exceptions;

import uk.gov.dwp.components.drs.creator.definitions.Constants;
import uk.gov.dwp.components.drs.creator.domain.ErrorInfo;

/**
 * Base exception for logical processing errors within the DRS Request Creator
 */
public class MetadataCreatorException extends Exception {

    /**
     * For serialisation
     */
    private static final long serialVersionUID = 1L;

    /**
     * Indicates if this is a potentially retriable failure or a permanent one
     */
    private final boolean isTransient;

    /**
     * Message only constructor
     *
     * @param isTransient whether this is a permanent or retriable failure
     * @param message     associated error message
     */
    public MetadataCreatorException(boolean isTransient, String message) {
        super(message);
        this.isTransient = isTransient;
    }

    /**
     * Message and underlying exception constructor
     *
     * @param isTransient whether this is a permanent or retriable failure
     * @param message     expected error message
     * @param cause       underlying throwable
     */
    public MetadataCreatorException(boolean isTransient, String message, Throwable cause) {
        super(message, cause);
        this.isTransient = isTransient;
    }

    /**
     * Indicates if this is permanent or temporary
     *
     * @return transient error indicator
     */
    public boolean isTransient() {
        return isTransient;
    }

    /**
     * Marshal to an <code>ErrorInfo</code> object for responses to invoking services
     *
     * @return model object containing the details of this exception
     */
    public ErrorInfo toErrorInfo() {
        return new ErrorInfo(Constants.COMPONENT_NAME, this.getMessage(), this.isTransient);
    }
}
