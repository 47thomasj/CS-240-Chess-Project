package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * A helper class meant to calculate the legal moves of any given piece
 */
public class PieceMoveCalculator {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType promotionPiece; // May be null
    public PieceMoveCalculator(ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPiece.PieceType promotionPiece) {
        type = pieceType;
        this.color = color;
        this.promotionPiece = promotionPiece;
    }

    private boolean checkIfMoveLegal(ChessMove move, ChessBoard board) {
        if (move.getEndPosition().getRow() > 8 || move.getEndPosition().getRow() < 1
                || move.getEndPosition().getColumn() > 8 || move.getEndPosition().getColumn() < 1) {
            return false;
        }
        boolean hasPiece = board.getPiece(move.getEndPosition()) != null;
        return !hasPiece || board.getPiece(move.getEndPosition()).getTeamColor() != this.color;
    }

    private ArrayList<ChessMove> calculateKingMoves(ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for (int col = position.getColumn() - 1; col <= position.getColumn() + 1; col ++) {
            for (int row = position.getRow() - 1; row <= position.getRow() + 1; row ++) {
                ChessMove move = new ChessMove(position, new ChessPosition(row, col), null);
                if (checkIfMoveLegal(move, board) && move.getEndPosition() != position) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    private ArrayList<ChessMove> calcualteKightMoves(ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        possiblePositions.add(new ChessPosition(position.getRow() + 2, position.getColumn() - 1));
        possiblePositions.add(new ChessPosition(position.getRow() + 2, position.getColumn() + 1));
        possiblePositions.add(new ChessPosition(position.getRow() - 2, position.getColumn() - 1));
        possiblePositions.add(new ChessPosition(position.getRow() - 2, position.getColumn() + 1));
        possiblePositions.add(new ChessPosition(position.getRow() - 1, position.getColumn() - 2));
        possiblePositions.add(new ChessPosition(position.getRow() + 1, position.getColumn() - 2));
        possiblePositions.add(new ChessPosition(position.getRow() - 1, position.getColumn() + 2));
        possiblePositions.add(new ChessPosition(position.getRow() + 1, position.getColumn() + 2));

        for (ChessPosition possiblePosition : possiblePositions) {
            ChessMove possibleMove = new ChessMove(position, possiblePosition, null);
            if (checkIfMoveLegal(possibleMove, board)) {
                moves.add(possibleMove);
            }
        }
        return moves;
    }

    private ArrayList<ChessMove> calculatePawnMoves(ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();


        ChessMove frontMove = new ChessMove(position, new ChessPosition(
                color == ChessGame.TeamColor.WHITE ? position.getRow() + 1 : position.getRow() - 1, position.getColumn()
        ), promotionPiece);

        ChessPosition leftPosition = new ChessPosition(
                color == ChessGame.TeamColor.WHITE ? position.getRow() + 1 : position.getRow() - 1,
                color == ChessGame.TeamColor.WHITE ? position.getColumn() - 1 : position.getColumn() + 1
        );
        ChessMove leftCapture = new ChessMove(position, leftPosition, promotionPiece);

        ChessPosition rightPosition = new ChessPosition(
                color == ChessGame.TeamColor.WHITE ? position.getRow() + 1 : position.getRow() - 1,
                color == ChessGame.TeamColor.WHITE ? position.getColumn() + 1 : position.getColumn() - 1
        );
        ChessMove rightCapture = new ChessMove(position, rightPosition, promotionPiece);

        if (checkIfMoveLegal(frontMove, board)) {
            moves.add(frontMove);
        }

        ChessPiece leftPiece = board.getPiece(leftPosition);
        ChessPiece rightPiece = board.getPiece(rightPosition);
        if (checkIfMoveLegal(leftCapture, board) && leftPiece != null && leftPiece.getTeamColor() != color) {
            moves.add(leftCapture);
        }
        if (checkIfMoveLegal(rightCapture, board) && rightPiece != null && rightPiece.getTeamColor() != color) {
            moves.add(rightCapture);
        }
        return moves;
    }

    public ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        return switch (type) {
            case KING -> calculateKingMoves(position, board);
            case KNIGHT -> calcualteKightMoves(position, board);
            case PAWN -> calculatePawnMoves(position, board);
            default -> new ArrayList<>();
        };

    }
}