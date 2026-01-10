package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int rowLocation;
    private int colLocation;
    public ChessPosition(int row, int col) {
        rowLocation = row;
        colLocation = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return rowLocation;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return colLocation;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) object;
        return rowLocation == that.rowLocation && colLocation == that.colLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowLocation, colLocation);
    }
}
