package edu.dosw.parcial.core.validators.chain;

public abstract class ReservaValidationHandler {

    protected ReservaValidationHandler next;

    public ReservaValidationHandler setNext(ReservaValidationHandler next) {
        this.next = next;
        return next;
    }

    public abstract void handle(ReservaValidationContext context);

    protected void next(ReservaValidationContext context) {
        if (next != null) {
            next.handle(context);
        }
    }
}
