package board;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessPosition;
import chess.ChessPiece;

public class ChessPrinter {
    
    public static void printBoard(ChessBoard board, TeamColor teamColor) {
    String letters = teamColor == TeamColor.WHITE ? " a  b  c  d  e  f  g  h " : " h  g  f  e  d  c  b  a ";
    String[] numbers = teamColor == TeamColor.WHITE ? new String[] {"1", "2", "3", "4", "5", "6", "7", "8"} : new String[] {"8", "7", "6", "5", "4", "3", "2", "1"};
    String topLeftCornerColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
    String teamTextColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
    String oponentTextColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE;

    String currentColor = topLeftCornerColor;
    System.out.println("\n");
    System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   " + letters + "   " + EscapeSequences.RESET_BG_COLOR);
    for (int row = 8; row >= 1; row--) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + numbers[row - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        System.out.print(printRow(board, row, currentColor, teamTextColor, oponentTextColor));
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + numbers[row - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        System.out.println();
        currentColor = switchColor(currentColor);
    }
    System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   " + letters + "   " + EscapeSequences.RESET_BG_COLOR);
    }

    private static String printRow(ChessBoard board, int row, String startingColor, String teamTextColor, String oponentTextColor) {
        String currentColor = startingColor;        
        String rowString = "";

        for (int i = 1; i <= 8; i++) {
            ChessPosition position = new ChessPosition(row, i);
            ChessPiece pieceAtPosition = board.getPiece(position);

            TeamColor colorAtPosition = pieceAtPosition != null ? pieceAtPosition.getTeamColor() : null;
            String textColor = colorAtPosition == TeamColor.WHITE ? teamTextColor : oponentTextColor;
            rowString += currentColor + textColor + " ";
            rowString += pieceAtPosition != null ? pieceAtPosition.toString() : " "; 
            rowString += " ";
            currentColor = switchColor(currentColor);
        }
        rowString += EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
        return rowString;
    }

    private static String switchColor(String color) {
        return color.equals(EscapeSequences.SET_BG_COLOR_WHITE) ? EscapeSequences.SET_BG_COLOR_BLACK : EscapeSequences.SET_BG_COLOR_WHITE;
    }
}
