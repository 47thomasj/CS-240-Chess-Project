package models.requests;

public record JoinGameRequest(String authToken, String playerColor, Integer gameID) {}