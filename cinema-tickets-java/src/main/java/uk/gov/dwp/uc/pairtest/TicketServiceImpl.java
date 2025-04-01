package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
	
	private static final int MAX_TICKETS = 25;
    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }
    
    /**
     * Processes ticket purchase requests for a given account.
     * Validates account ID and ticket requests, calculates totals,
     * and triggers payment and seat reservation.
     *
     * @param accountId The account ID making the purchase (must be > 0)
     * @param ticketTypeRequests Varargs of ticket requests (Adult, Child, Infant)
     * @throws InvalidPurchaseException If validation fails (e.g., no Adult, >25 tickets)
     */
	@Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be a positive number");
        }
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("At least one ticket request is required");
        }

        int[] totals = calculateTotals(ticketTypeRequests);
        int totalAmount = totals[0];
        int seatsToReserve = totals[1];

        paymentService.makePayment(accountId, totalAmount);
        reservationService.reserveSeat(accountId, seatsToReserve);
        
    }

	/**
     * Calculates total cost and seats required for ticket requests.
     * Validates ticket quantities and types, throwing exceptions for invalid cases.
     *
     * @param ticketTypeRequests Array of ticket requests to process
     * @return int[] with [0] = totalAmount, [1] = seatsToReserve
     * @throws InvalidPurchaseException If requests are invalid (e.g., negative quantity of tickets)
     */
    private int[] calculateTotals(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        int totalTickets = 0;
        int adultTickets = 0;
        int infantTickets = 0;
        int totalAmount = 0;
        int seatsToReserve = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request == null) {
                throw new InvalidPurchaseException("Ticket request cannot be null");
            }
            if (request.getNoOfTickets() < 0) {
                throw new InvalidPurchaseException("Number of tickets cannot be negative");
            }

            TicketTypeRequest.Type type = request.getTicketType();
            int quantity = request.getNoOfTickets();
            totalTickets += quantity;

            switch (type) {
                case ADULT -> {
                    adultTickets += quantity;
                    totalAmount += quantity * type.getTicketPrice();
                    seatsToReserve += quantity;
                }
                case CHILD -> {
                    totalAmount += quantity * type.getTicketPrice();
                    seatsToReserve += quantity;
                }
                case INFANT -> infantTickets += quantity;
                default -> throw new InvalidPurchaseException("Unknown ticket type: " + type);
            }
        }

        validatePurchase(totalTickets, adultTickets, infantTickets);
        return new int[] { totalAmount, seatsToReserve };
    }
    
    /**
     * Validates ticket purchase against business rules.
     * Ensures max tickets, adult requirement, and infant limits are met.
     *
     * @param totalTickets Total number of tickets requested
     * @param adultTickets Number of adult tickets
     * @param infantTickets Number of infant tickets
     * @throws InvalidPurchaseException If rules are violated
     */
    private void validatePurchase(int totalTickets, int adultTickets, int infantTickets) {
        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than " + MAX_TICKETS + " tickets at a time");
        }
        if (adultTickets == 0 && totalTickets > 0) {
            throw new InvalidPurchaseException("Child or Infant tickets require at least one Adult ticket");
        }
        if (infantTickets > adultTickets) {
            throw new InvalidPurchaseException("Number of Infant tickets cannot exceed Adult tickets");
        }
    }
}

