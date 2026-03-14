package board;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessPosition;

public class ChessPrinter {
    
    public static void printBoard(ChessBoard board, TeamColor teamColor) {
    String letters = teamColor == TeamColor.WHITE ? "abcdefgh" : "hgfedcba";
    String numbers = teamColor == TeamColor.WHITE ? "12345678" : "87654321";
    String topLeftCornerColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
    String teamTextColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
    String oponentTextColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE;
    System.out.println(printRow(board, 8, topLeftCornerColor, teamTextColor, oponentTextColor));

    }

    private static String printRow(ChessBoard board, int row, String startingColor, String teamTextColor, String oponentTextColor) {
        String currentColor = startingColor;        
        String rowString = "";

        for (int i = 1; i <= 8; i++) {
            ChessPosition position = new ChessPosition(row, i);
            TeamColor colorAtPosition = board.getPiece(position).getTeamColor();
            String textColor = colorAtPosition == TeamColor.WHITE ? teamTextColor : oponentTextColor;
            rowString += currentColor + textColor + " " + board.getPiece(new ChessPosition(row, i)).toString() + " ";
            currentColor = switchColor(currentColor);
        }
        rowString += EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
        return rowString;
    }

    private static String switchColor(String color) {
        return color.equals(EscapeSequences.SET_BG_COLOR_WHITE) ? EscapeSequences.SET_BG_COLOR_BLACK : EscapeSequences.SET_BG_COLOR_WHITE;
    }
}
