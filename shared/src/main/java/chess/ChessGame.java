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
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece != null) {
            Collection<ChessMove> pieceMoves = piece.pieceMoves(this.board, startPosition);
            for (ChessMove move: pieceMoves) {
                ChessBoard currentBoard = new ChessBoard(board);
                this.board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                this.board.addPiece(move.getStartPosition(), null);
                if (!isInCheck(piece.getTeamColor())) {
                    validMoves.add(move);
                }
                this.board = currentBoard;
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (this.validMoves(move.getStartPosition()).contains(move) && teamTurn == piece.getTeamColor()) {
            this.board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            this.board.addPiece(move.getStartPosition(), null);
            if (move.getPromotionPiece() != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
            }
            this.teamTurn = this.teamTurn == TeamColor.WHITE ? TeamColor.BLACK: TeamColor.WHITE;
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
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = this.getBoard().getPiece(position);
                Collection<ChessMove> moves;
                if (piece == null) {
                    continue;
                }
                moves = piece.pieceMoves(board, position);
                Collection<ChessPosition> endPositions = moves.stream().map(ChessMove::getEndPosition).toList();
                for (ChessPosition endPosition: endPositions) {
                    if (Objects.equals(endPosition, kingPiecePosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Collection<ChessMove> getAllTeamMoves(TeamColor teamColor) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    allMoves.addAll(this.validMoves(position));
                }
            }
        }
        return allMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> allMoves = getAllTeamMoves(teamColor);

        ChessBoard currentBoard = new ChessBoard(board);
        for (ChessMove move: allMoves) {
            try {
                this.makeMove(move);
            } catch (InvalidMoveException e) {
                board = new ChessBoard(currentBoard);
                continue;
            }
            if (!this.isInCheck(teamColor)) {
                board = new ChessBoard(currentBoard);
                return false;
            }
        }
        board = new ChessBoard(board);
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> allMoves = getAllTeamMoves(teamColor);
        return allMoves.isEmpty();
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
