package chess;

import java.util.ArrayList;

/**
 * A helper class meant to calculate the legal moves of any given piece
 */
public class PieceMoveCalculator {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor color;
    public PieceMoveCalculator(ChessPiece.PieceType pieceType, ChessGame.TeamColor color) {
        type = pieceType;
        this.color = color;
    }

    private boolean checkIfMoveLegal(ChessMove move, ChessBoard board) {
        if (move.getEndPosition().getRow() > 8 || move.getEndPosition().getRow() < 1
                || move.getEndPosition().getColumn() > 8 || move.getEndPosition().getColumn() < 1) {
            return false;
        }
        boolean hasPiece = board.getPiece(move.getEndPosition()) != null;
        return !hasPiece || board.getPiece(move.getEndPosition()).getTeamColor() != this.color;
    }

    private ArrayList<ChessMove> calculateCastle(ChessPosition position, ChessBoard board, ChessGame game) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (game == null) { return moves; }
        
        ChessGame.TeamColor kingColor = board.getPiece(position).getTeamColor();
        ChessPosition queensideRookPosition = kingColor == ChessGame.TeamColor.WHITE ? new ChessPosition(1, 1) : new ChessPosition(8, 1);
        ChessPosition kingsideRookPosition = kingColor == ChessGame.TeamColor.WHITE ? new ChessPosition(1, 8) : new ChessPosition(8, 8);
        ChessPiece queensideRook = board.getPiece(queensideRookPosition);
        ChessPiece kingsideRook = board.getPiece(kingsideRookPosition);

        if (queensideRook != null && !queensideRook.getHasMoved() && !board.getPiece(position).getHasMoved()) {
            boolean clearPathToCastleSquare = true;
            for (int col = position.getColumn() - 1; col > queensideRookPosition.getColumn(); col--) {
                if (board.getPiece(new ChessPosition(position.getRow(), col)) != null) {
                    clearPathToCastleSquare = false;
                    break;
                }
            }
            
            if (clearPathToCastleSquare) {
                ChessPosition intermediate = new ChessPosition(position.getRow(), position.getColumn() - 1);
                ChessPosition destination = new ChessPosition(position.getRow(), position.getColumn() - 2);
                
                ChessMove queensideCastle = new ChessMove(position, destination, null);
                if (checkIfMoveLegal(queensideCastle, board) &&
                    !game.isSquareUnderAttack(position, kingColor) &&
                    !game.isSquareUnderAttack(intermediate, kingColor) &&
                    !game.isSquareUnderAttack(destination, kingColor)) {
                    moves.add(queensideCastle);
                }
            }
        }

        if (kingsideRook != null && !kingsideRook.getHasMoved() && !board.getPiece(position).getHasMoved()) {
            boolean clearPathToCastleSquare = true;
            for (int col = position.getColumn() + 1; col < kingsideRookPosition.getColumn(); col++) {
                if (board.getPiece(new ChessPosition(position.getRow(), col)) != null) {
                    clearPathToCastleSquare = false;
                    break;
                }
            }
            
            if (clearPathToCastleSquare) {
                ChessPosition intermediate = new ChessPosition(position.getRow(), position.getColumn() + 1);
                ChessPosition destination = new ChessPosition(position.getRow(), position.getColumn() + 2);
                
                ChessMove kingsideCastle = new ChessMove(position, destination, null);
                if (checkIfMoveLegal(kingsideCastle, board) &&
                    !game.isSquareUnderAttack(position, kingColor) &&
                    !game.isSquareUnderAttack(intermediate, kingColor) &&
                    !game.isSquareUnderAttack(destination, kingColor)) {
                    moves.add(kingsideCastle);
                }
            }
        }

        return moves;
    }

    private ArrayList<ChessMove> calculateKingMoves(ChessPosition position, ChessBoard board, ChessGame game) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for (int col = position.getColumn() - 1; col <= position.getColumn() + 1; col ++) {
            for (int row = position.getRow() - 1; row <= position.getRow() + 1; row ++) {
                ChessMove move = new ChessMove(position, new ChessPosition(row, col), null);
                if (checkIfMoveLegal(move, board) && move.getEndPosition() != position) {
                    moves.add(move);
                }
            }
        }
        moves.addAll(calculateCastle(position, board, game));
        return moves;
    }

    private ArrayList<ChessMove> calculateQueenMoves(ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(calculateBishopMoves(position, board));
        moves.addAll(calculateRookMoves(position, board));
        return moves;
    }

    private ArrayList<ChessMove> calculateBishopMoves(ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        boolean nextMoveCaptures = false;

        ChessMove nextUpperRight = new ChessMove(
            position,
            new ChessPosition(position.getRow() + 1, position.getColumn() + 1),
            null
        );
        while (checkIfMoveLegal(nextUpperRight, board) && !nextMoveCaptures) {
            moves.add(nextUpperRight);
            nextMoveCaptures = board.getPiece(nextUpperRight.getEndPosition()) != null;
            nextUpperRight = new ChessMove(
                position,
                new ChessPosition(nextUpperRight.getEndPosition().getRow() + 1, nextUpperRight.getEndPosition().getColumn() + 1),
                null
            );
        }
        nextMoveCaptures = false;

        ChessMove nextUpperLeft = new ChessMove(
            position,
            new ChessPosition(position.getRow() + 1, position.getColumn() - 1),
            null
        );
        while (checkIfMoveLegal(nextUpperLeft, board) && !nextMoveCaptures) {
            moves.add(nextUpperLeft);
            nextMoveCaptures = board.getPiece(nextUpperLeft.getEndPosition()) != null;
            nextUpperLeft = new ChessMove(
                position,
                new ChessPosition(nextUpperLeft.getEndPosition().getRow() + 1, nextUpperLeft.getEndPosition().getColumn() - 1),
                null
            );
        }
        nextMoveCaptures = false;

        ChessMove nextLowerRight = new ChessMove(
            position,
            new ChessPosition(position.getRow() - 1, position.getColumn() + 1),
            null
        );
        while (checkIfMoveLegal(nextLowerRight, board) && !nextMoveCaptures) {
            moves.add(nextLowerRight);
            nextMoveCaptures = board.getPiece(nextLowerRight.getEndPosition()) != null;
            nextLowerRight = new ChessMove(
                position,
                new ChessPosition(nextLowerRight.getEndPosition().getRow() - 1, nextLowerRight.getEndPosition().getColumn() + 1),
                null
            );
        }
        nextMoveCaptures = false;

        ChessMove nextLowerLeft = new ChessMove(
            position,
            new ChessPosition(position.getRow() - 1, position.getColumn() - 1),
            null
        );
        while (checkIfMoveLegal(nextLowerLeft, board) && !nextMoveCaptures) {
            moves.add(nextLowerLeft);
            nextMoveCaptures = board.getPiece(nextLowerLeft.getEndPosition()) != null;
            nextLowerLeft = new ChessMove(
                position,
                new ChessPosition(nextLowerLeft.getEndPosition().getRow() - 1, nextLowerLeft.getEndPosition().getColumn() - 1),
                null
            );
        }

        return moves;
    }

    private ArrayList<ChessMove> calculateKnightMoves(ChessPosition position, ChessBoard board) {
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

    private ArrayList<ChessMove> calculateRookMoves(ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        boolean nextMoveCaptures = false;

        ChessMove nextFront = new ChessMove(
            position,
            new ChessPosition(position.getRow() + 1, position.getColumn()),
            null
        );
        while (checkIfMoveLegal(nextFront, board) && !nextMoveCaptures) {
            moves.add(nextFront);
            nextMoveCaptures = board.getPiece(nextFront.getEndPosition()) != null;
            nextFront = new ChessMove(
                position,
                new ChessPosition(nextFront.getEndPosition().getRow() + 1, nextFront.getEndPosition().getColumn()),
                null
            );
        }
        nextMoveCaptures = false;

        ChessMove nextBack = new ChessMove(
            position,
            new ChessPosition(position.getRow() - 1, position.getColumn()),
            null
        );
        while (checkIfMoveLegal(nextBack, board) && !nextMoveCaptures) {
            moves.add(nextBack);
            nextMoveCaptures = board.getPiece(nextBack.getEndPosition()) != null;
            nextBack = new ChessMove(
                position,
                new ChessPosition(nextBack.getEndPosition().getRow() - 1, nextBack.getEndPosition().getColumn()),
                null
            );
        }
        nextMoveCaptures = false;

        ChessMove nextRight = new ChessMove(
            position,
            new ChessPosition(position.getRow(), position.getColumn() + 1),
            null
        );
        while (checkIfMoveLegal(nextRight, board) && !nextMoveCaptures) {
            moves.add(nextRight);
            nextMoveCaptures = board.getPiece(nextRight.getEndPosition()) != null;
            nextRight = new ChessMove(
                position,
                new ChessPosition(nextRight.getEndPosition().getRow(), nextRight.getEndPosition().getColumn() + 1),
                null
            );
        }
        nextMoveCaptures = false;

        ChessMove nextLeft = new ChessMove(
            position,
            new ChessPosition(position.getRow(), position.getColumn() - 1),
            null
        );
        while (checkIfMoveLegal(nextLeft, board) && !nextMoveCaptures) {
            moves.add(nextLeft);
            nextMoveCaptures = board.getPiece(nextLeft.getEndPosition()) != null;
            nextLeft = new ChessMove(
                position,
                new ChessPosition(nextLeft.getEndPosition().getRow(), nextLeft.getEndPosition().getColumn() - 1),
                null
            );
        }

        return moves;
    }

    private int getPawnMoveRow(ChessPosition position) {
        if (color == ChessGame.TeamColor.WHITE) {
            return position.getRow() + 1;
        }
        return position.getRow() - 1;
    }

    private ArrayList<ChessMove> calculateEnPassant(ChessPosition position, ChessBoard board, ChessGame game) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        if (game != null && game.getLastMove() != null) {
            ChessMove lastMove = game.getLastMove();
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());

            boolean lastMoveTypeIsPawn = lastMovedPiece != null && lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN;
            boolean lastMoveTypeIsNotSameTeam = lastMovedPiece != null && lastMovedPiece.getTeamColor() != color;
            boolean lastMoveStartRowIsPawnRow = lastMove.getStartPosition().getRow() == (color == ChessGame.TeamColor.WHITE ? 7 : 2);
            boolean lastMoveEndRowIsEnPassantRow = lastMove.getEndPosition().getRow() == (color == ChessGame.TeamColor.WHITE ? 5 : 4);
            
            if (lastMoveTypeIsPawn && lastMoveTypeIsNotSameTeam && lastMoveStartRowIsPawnRow && lastMoveEndRowIsEnPassantRow) {
                int enPassantRow = lastMove.getEndPosition().getRow();
                if (position.getRow() == enPassantRow) {
                    int row = getPawnMoveRow(position);
                    int col = lastMove.getEndPosition().getColumn();
                    ChessMove enPassantMove = new ChessMove(position, new ChessPosition(row, col), null);
                    moves.add(enPassantMove);
                }
            }
        }
        return moves;
    }

    private boolean checkIfEndInPromotionRow(ChessPosition position) {
        return color == ChessGame.TeamColor.WHITE ? position.getRow() == 8 : position.getRow() == 1;
    }

    private ArrayList<ChessMove> calculatePawnMoves(ChessPosition position, ChessBoard board, ChessGame game) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece.PieceType[] promotionPieces = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KNIGHT
        };

        ChessMove oneFrontNoPromo = new ChessMove(
            position,
            new ChessPosition(getPawnMoveRow(position), position.getColumn()),
            null
        );
        if (checkIfMoveLegal(oneFrontNoPromo, board) && board.getPiece(oneFrontNoPromo.getEndPosition()) == null) {
            if (checkIfEndInPromotionRow(oneFrontNoPromo.getEndPosition())) {
                for (ChessPiece.PieceType promotionType: promotionPieces) {
                    ChessPosition endPosition = new ChessPosition(getPawnMoveRow(position), position.getColumn());
                    ChessMove oneFrontPromo = new ChessMove(position, endPosition, promotionType);
                    moves.add(oneFrontPromo);
                }
            } else {
                moves.add(oneFrontNoPromo);
            }
        }

        if (color == ChessGame.TeamColor.WHITE ? position.getRow() == 2: position.getRow() == 7) {
            int row = color == ChessGame.TeamColor.WHITE ? getPawnMoveRow(position) + 1 : getPawnMoveRow(position) - 1;
            ChessMove twoFrontNoPromo = new ChessMove(
                position,
                new ChessPosition(
                    row, position.getColumn()
                ),
                null
            );
            boolean moveIsLegal = checkIfMoveLegal(twoFrontNoPromo, board);
            boolean nullAtMoveEnd = board.getPiece(twoFrontNoPromo.getEndPosition()) == null;
            boolean halfMoveIsLegal = checkIfMoveLegal(oneFrontNoPromo, board);
            if (moveIsLegal && nullAtMoveEnd && halfMoveIsLegal && board.getPiece(oneFrontNoPromo.getEndPosition()) == null) {
                if (checkIfEndInPromotionRow(twoFrontNoPromo.getEndPosition())) {
                    for (ChessPiece.PieceType promotionType: promotionPieces) {
                        ChessPosition endPosition = new ChessPosition(row, position.getColumn());
                        ChessMove twoFrontPromo = new ChessMove(position, endPosition, promotionType);
                        moves.add(twoFrontPromo);
                    }
                } else {
                    moves.add(twoFrontNoPromo);
                }
            }
        }

        ChessPosition frontLeftNoPromoPosition = new ChessPosition(getPawnMoveRow(position), position.getColumn() - 1);
        ChessMove frontLeftNoPromo = new ChessMove(
            position,
            frontLeftNoPromoPosition,
            null
        );
        if (checkIfMoveLegal(frontLeftNoPromo, board) && board.getPiece(frontLeftNoPromo.getEndPosition()) != null) {
            if (checkIfEndInPromotionRow(frontLeftNoPromo.getEndPosition())) {
                for (ChessPiece.PieceType promotionType: promotionPieces) {
                    ChessPosition endPosition = new ChessPosition(getPawnMoveRow(position), position.getColumn() - 1);
                    ChessMove frontLeftPromo = new ChessMove(position, endPosition, promotionType);
                    moves.add(frontLeftPromo);
                }
            } else {
                moves.add(frontLeftNoPromo);
            }
        }

        ChessPosition frontRightNoPromoPosition = new ChessPosition(getPawnMoveRow(position), position.getColumn() + 1);
        ChessMove frontRightNoPromo = new ChessMove(
            position,
            frontRightNoPromoPosition,
            null
        );
        if (checkIfMoveLegal(frontRightNoPromo, board) && board.getPiece(frontRightNoPromo.getEndPosition()) != null) {
            if (checkIfEndInPromotionRow(frontRightNoPromo.getEndPosition())) {
                for (ChessPiece.PieceType promotionType: promotionPieces) {
                    ChessPosition endPosition = new ChessPosition(getPawnMoveRow(position), position.getColumn() + 1);
                    ChessMove frontRightPromo = new ChessMove(position, endPosition, promotionType);
                    moves.add(frontRightPromo);
                }
            } else {
                moves.add(frontRightNoPromo);
            }
        }

        moves.addAll(calculateEnPassant(position, board, game));
        return moves;
    }

    public ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessGame game) {
        return switch (type) {
            case KING -> calculateKingMoves(position, board, game);
            case QUEEN -> calculateQueenMoves(position, board);
            case BISHOP -> calculateBishopMoves(position, board);
            case KNIGHT -> calculateKnightMoves(position, board);
            case ROOK -> calculateRookMoves(position, board);
            case PAWN -> calculatePawnMoves(position, board, game);
            default -> new ArrayList<>();
        };

    }
}