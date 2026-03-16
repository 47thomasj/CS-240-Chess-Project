package board;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessPosition;
import chess.ChessPiece;

public class ChessPrinter {
    
    public static void printBoard(ChessBoard board, TeamColor teamColor) {
    String letters = teamColor == TeamColor.WHITE ? " a  b  c  d  e  f  g  h " : " h  g  f  e  d  c  b  a ";

    String[] numbers = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    String topLeftCornerColor = EscapeSequences.SET_BG_COLOR_WHITE;
    String teamTextColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_RED;
    String oponentTextColor = teamColor == TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE;

    String currentColor = topLeftCornerColor;
    System.out.println("\n");
    System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   " + letters + "   " + EscapeSequences.RESET_BG_COLOR);
    if (teamColor == TeamColor.WHITE) {
        for (int row = 8; row >= 1; row--) {
            printWholeRow(board, numbers, row, currentColor, teamTextColor, oponentTextColor);
            currentColor = switchColor(currentColor);
        }
    } else {
        for (int row = 1; row <= 8; row++) {
            printWholeRow(board, numbers, row, currentColor, teamTextColor, oponentTextColor);
            currentColor = switchColor(currentColor);
        }
    }
    System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   " + letters + "   " + EscapeSequences.RESET_BG_COLOR);
    }

    private static void printWholeRow(
        ChessBoard board, 
        String[] numbers, 
        int row, 
        String currentColor, 
        String teamTextColor, 
        String oponentTextColor
    ) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + numbers[row - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        System.out.print(printRow(board, row, currentColor, teamTextColor, oponentTextColor));
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + numbers[row - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        System.out.println();
    }

    private static String printRow(ChessBoard board, int row, String startingColor, String teamTextColor, String oponentTextColor) {
        String currentColor = startingColor;        
        String rowString = "";

        if (teamTextColor.equals(EscapeSequences.SET_TEXT_COLOR_BLUE)) {
            for (int i = 1; i <= 8; i++) {
                rowString += printPiece(row, i, board, teamTextColor, oponentTextColor, currentColor);
                currentColor = switchColor(currentColor);
            }
        } else {
            for (int i = 8; i >= 1; i--) {
                rowString += printPiece(row, i, board, teamTextColor, oponentTextColor, currentColor);
                currentColor = switchColor(currentColor);
            }
        }
        rowString += EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
        return rowString;
    }

    private static String printPiece(int row, int col, ChessBoard board, String teamTextColor, String oponentTextColor, String currentColor) {
        String rowString = "";
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece pieceAtPosition = board.getPiece(position);

        TeamColor colorAtPosition = pieceAtPosition != null ? pieceAtPosition.getTeamColor() : null;
        String textColor = colorAtPosition == TeamColor.WHITE ? teamTextColor : oponentTextColor;
        rowString += currentColor + textColor + " ";
        rowString += pieceAtPosition != null ? pieceAtPosition.toString() : " "; 
        rowString += " ";
        return rowString;
    }

    private static String switchColor(String color) {
        return color.equals(EscapeSequences.SET_BG_COLOR_WHITE) ? EscapeSequences.SET_BG_COLOR_BLACK : EscapeSequences.SET_BG_COLOR_WHITE;
    }
}
