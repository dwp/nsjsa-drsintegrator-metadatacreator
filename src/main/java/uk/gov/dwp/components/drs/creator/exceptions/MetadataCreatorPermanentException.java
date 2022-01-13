package uk.gov.dwp.components.drs.creator.exceptions;

/**
 * Concrete exception class for permanent exceptions
 */
public class MetadataCreatorPermanentException extends MetadataCreatorException {

    /**
     * For serialisation
     */
    private static final long serialVersionUID = 1L;

    /**
     * Message only constructor
     *
     * @param message associated error message
     */
    public MetadataCreatorPermanentException(String message) {
        super(false, message);
    }

    /**
     * Message and underlying cause constructor
     *
     * @param message associated error message
     * @param cause   underlying throwable
     */
    public MetadataCreatorPermanentException(String message, Throwable cause) {
        super(false, message, cause);
    }
}
