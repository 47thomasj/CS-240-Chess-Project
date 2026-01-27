package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
        NONE
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece != null) {
            return piece.pieceMoves(this.board, startPosition);
        }
        return new ArrayList<>();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (this.validMoves(move.getStartPosition()).contains(move)) {
            this.board.addPiece(move.getStartPosition(), null);
            this.board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        } else {
            throw new InvalidMoveException();
        }

    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        ChessPosition kingPiecePosition = null;
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPiecePosition = position;
                    break;
                }
            }
            if (kingPiecePosition != null) {
                break;
            }
        }
        return kingPiecePosition;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPiecePosition = getKingPosition(teamColor);

        for (int row=1; row<=8; row++) {
            for (int col = 1; col <= 8; col++) {
                Collection<ChessMove> moves = this.validMoves(new ChessPosition(row, col));
                Collection<ChessPosition> endPositions = moves.stream().map(ChessMove::getEndPosition).toList();
                for (ChessPosition position: endPositions) {
                    if (Objects.equals(position, kingPiecePosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPiecePosition = getKingPosition(teamColor);
        Collection<ChessMove> kingMoves = this.validMoves(kingPiecePosition);
        boolean isInCheck = this.isInCheck(teamColor);
        return isInCheck && kingMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        ChessGame that = (ChessGame) obj;
        return Objects.equals(this.board, that.board) && Objects.equals(this.teamTurn, that.teamTurn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.board, this.teamTurn);
    }
}
