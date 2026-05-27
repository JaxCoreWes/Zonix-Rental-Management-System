package com.westoncodeops.zonixrental.service.MaintenanceService;

import com.westoncodeops.zonixrental.DTOs.Requests.CreateTicketRequest;
import com.westoncodeops.zonixrental.DTOs.Responses.MaintenanceTicketResponse;

import java.util.List;
import java.util.UUID;


public interface IMaintenanceService {
    MaintenanceTicketResponse createTicket(CreateTicketRequest ticket);
    MaintenanceTicketResponse resolveTicket(UUID ticketId);
    List<MaintenanceTicketResponse> getTenantTickets(String phoneNumber);
    List<MaintenanceTicketResponse> getAllTickets();

}
