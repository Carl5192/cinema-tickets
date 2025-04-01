package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImplTest {
    private TicketPaymentService paymentService;
    private SeatReservationService seatService;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentService.class);
        seatService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, seatService);
    }

    @Test
    void validPurchase() {
        ticketService.purchaseTickets(1L,
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        verify(paymentService).makePayment(1L, 65); 
        verify(seatService).reserveSeat(1L, 3); 
    }

    @Test
    void noAdultTickets() {
        assertThrows(InvalidPurchaseException.class, () ->
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)));
        verifyNoInteractions(paymentService, seatService);
    }

    @Test
    void tooManyTickets() {
        assertThrows(InvalidPurchaseException.class, () ->
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26)));
        verifyNoInteractions(paymentService, seatService);
    }

    @Test
    void tooManyInfants() {
        assertThrows(InvalidPurchaseException.class, () ->
            ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)));
        verifyNoInteractions(paymentService, seatService);
    }
    
    @Test
    void noAccountID() {
        assertThrows(InvalidPurchaseException.class, () ->
            ticketService.purchaseTickets(null,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)));
        verifyNoInteractions(paymentService, seatService);
    }
    
}