package dataaccess;

public class GameDAOTests {
    // Test that an empty table is created on init where no database exists before
    // Test that no table is created on init where a database does exist before
    // Test that a game can be created
    // Test that a game can be read (Create a chess game, then add it, then read it back, and assert that the resulting game obj is eq to the original one)
    // Test that requesting to read a game that isn't in the database throws "Error: bad request"
    // Test that a game can be updated (Create a chess game, then add it, then change it's state by making a move, then update it, then read it back, and assert that the resulting game obj is eq to the updated one)
    // Test that requesting to update a game that isn't in the database throws "Error: invalid game id"
    // Test that the database can be cleared
    // Test that a database with 2+ games in it can have those games listed
}
