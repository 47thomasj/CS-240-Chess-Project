package board;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessPosition;
import chess.ChessPiece;
import chess.ChessMove;

import java.util.Collection;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Scanner;

import java.util.HashMap;


public class ChessPrinter {

    public static ChessPosition getPositionFromUser() {
        HashMap<String, Integer> letterToColNumber = new HashMap<>();
        letterToColNumber.put("a", 1);
        letterToColNumber.put("b", 2);
        letterToColNumber.put("c", 3);
        letterToColNumber.put("d", 4);
        letterToColNumber.put("e", 5);
        letterToColNumber.put("f", 6);
        letterToColNumber.put("g", 7);
        letterToColNumber.put("h", 8);

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        int row = -1;
        int col = -1;

        while (col == -1) {
            System.out.print("Enter the column of the piece to get legal moves for (a-h): ");
            String colLetter = scanner.next();
            colLetter = colLetter.toLowerCase();
            if (!letterToColNumber.containsKey(colLetter)) {
                System.out.println("Invalid column. Please enter a valid column (a-h).");
                continue;
            }
            col = letterToColNumber.get(colLetter);
        }

        while (row == -1) {
            System.out.print("Enter the row of the piece to get legal moves for (1-8): ");
            row = scanner.nextInt();
            if (row < 1 || row > 8) {
                System.out.println("Invalid row. Please enter a valid row (1-8).");
                row = -1;
                continue;
            }
        }

        return new ChessPosition(row, col);
    }
    
    public static void printBoard(ChessBoard board, TeamColor teamColor) {
        printBoardWithHighlights(board, teamColor, new ArrayList<>(), null);
    }

    public static void printLegalMoves(ChessBoard board, TeamColor teamColor) {
        if (teamColor == null) {
            teamColor = TeamColor.WHITE;
        }
        
        ChessPosition position = getPositionFromUser();
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            printBoard(board, teamColor);
            return;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        Collection<ChessPosition> positionsToHighlight = moves.stream()
            .map(ChessMove::getEndPosition)
            .collect(Collectors.toCollection(ArrayList::new));
        printBoardWithHighlights(board, teamColor, positionsToHighlight, position);
    }
    
    private static void printBoardWithHighlights(
        ChessBoard board, 
        TeamColor teamColor, 
        Collection<ChessPosition> positionsToHighlight, 
        ChessPosition rootPosition
    ) {
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
            printWholeRow(board, numbers, row, currentColor, teamTextColor, oponentTextColor, positionsToHighlight, rootPosition);
            currentColor = switchColor(currentColor);
        }
    } else {
        for (int row = 1; row <= 8; row++) {
            printWholeRow(board, numbers, row, currentColor, teamTextColor, oponentTextColor, positionsToHighlight, rootPosition);
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
        String oponentTextColor,
        Collection<ChessPosition> positionsToHighlight,
        ChessPosition rootPosition
    ) {
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + numbers[row - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        System.out.print(printRow(board, row, currentColor, teamTextColor, oponentTextColor, positionsToHighlight, rootPosition));
        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " " + numbers[row - 1] + " " + EscapeSequences.RESET_BG_COLOR);
        System.out.println();
    }

    private static String printRow(
        ChessBoard board, 
        int row, 
        String startingColor, 
        String teamTextColor, 
        String oponentTextColor,
        Collection<ChessPosition> positionsToHighlight,
        ChessPosition rootPosition
    ) {
        String currentColor = startingColor;        
        String rowString = "";

        if (teamTextColor.equals(EscapeSequences.SET_TEXT_COLOR_BLUE)) {
            for (int i = 1; i <= 8; i++) {
                rowString += printPiece(row, i, board, teamTextColor, oponentTextColor, currentColor, positionsToHighlight, rootPosition);
                currentColor = switchColor(currentColor);
            }
        } else {
            for (int i = 8; i >= 1; i--) {
                rowString += printPiece(row, i, board, teamTextColor, oponentTextColor, currentColor, positionsToHighlight, rootPosition);
                currentColor = switchColor(currentColor);
            }
        }
        rowString += EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
        return rowString;
    }

    private static String printPiece(
        int row, int col, 
        ChessBoard board, 
        String teamTextColor, 
        String oponentTextColor, 
        String currentColor,
        Collection<ChessPosition> positionsToHighlight,
        ChessPosition rootPosition
    ) {
        String rowString = "";
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece pieceAtPosition = board.getPiece(position);

        TeamColor colorAtPosition = pieceAtPosition != null ? pieceAtPosition.getTeamColor() : null;
        String textColor = colorAtPosition == TeamColor.WHITE ? teamTextColor : oponentTextColor;
        if (positionsToHighlight.contains(position)) {
            currentColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
            if (colorAtPosition == TeamColor.WHITE) {
                currentColor = EscapeSequences.SET_BG_COLOR_GREEN;
            }
        } else if (position.equals(rootPosition)) {
            currentColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        }

        rowString += currentColor + textColor + " ";
        rowString += pieceAtPosition != null ? pieceAtPosition.toString() : " "; 
        rowString += " ";
        return rowString;
    }

    private static String switchColor(String color) {
        return color.equals(EscapeSequences.SET_BG_COLOR_WHITE) ? EscapeSequences.SET_BG_COLOR_BLACK : EscapeSequences.SET_BG_COLOR_WHITE;
    }
}
