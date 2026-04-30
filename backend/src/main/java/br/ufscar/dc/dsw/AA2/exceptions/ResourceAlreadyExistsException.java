package br.ufscar.dc.dsw.AA2.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String entity, String key, String value) {
        super(entity + " já existe com " + key + " igual a " + value + ".");
    }
}
