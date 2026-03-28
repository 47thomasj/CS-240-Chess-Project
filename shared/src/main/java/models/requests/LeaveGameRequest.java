package models.requests;

public record LeaveGameRequest(String authToken, Integer gameID) {}