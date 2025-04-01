package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public class TicketTypeRequest {
    private final int noOfTickets;
    private final Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT(25), CHILD(15), INFANT(0);

        private final int ticketPrice;

        Type(int ticketPrice) {
            this.ticketPrice = ticketPrice;
        }

        public int getTicketPrice() {
            return ticketPrice;
        }
    }

}
