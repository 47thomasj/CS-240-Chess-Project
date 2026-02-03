package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor color;
    private PieceType type;
    private PieceMoveCalculator moveCalculator;
    private boolean hasMoved;
    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type, boolean hasMoved) {
        this.color = color;
        this.type = type;
        moveCalculator = new PieceMoveCalculator(type, color);
        this.hasMoved = hasMoved;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN,
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * @return whether the given piece has ever been moved.
     */
    public boolean getHasMoved() { return hasMoved; }

    /**
     * Sets hasMoved to true.
     */
    public void setHasMoved() { this.hasMoved = true; }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return moveCalculator.calculateMoves(board, myPosition);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != ChessPiece.class) {
            return false;
        }
        return ((ChessPiece) obj).getPieceType() == this.getPieceType() && (((ChessPiece) obj).getTeamColor() == this.getTeamColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return this.getPieceType().toString().substring(0,2).toUpperCase();
        }
        return this.getPieceType().toString().substring(0,2).toLowerCase();
    }
}
