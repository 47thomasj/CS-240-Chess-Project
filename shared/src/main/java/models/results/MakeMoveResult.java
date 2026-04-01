package models.results;

import models.GameData;

public record MakeMoveResult(boolean success, String message, GameData game) {}    
