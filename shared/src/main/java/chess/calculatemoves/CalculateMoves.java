package chess.calculatemoves;


import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.abs;

public class CalculateMoves {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final int currentRow;
    private final int currentCol;
    private final ChessPiece myPiece;

    public CalculateMoves(ChessBoard board, ChessPosition myPosition, ChessPiece myPiece) {
        this.board = board;
        this.myPosition = myPosition;
        this.currentRow = myPosition.getRow();
        this.currentCol = myPosition.getColumn();
        this.myPiece = myPiece;
    }

    public Collection<ChessMove> ListMoves(){

        return switch ( myPiece.getPieceType() ) {
            case ChessPiece.PieceType.ROOK   -> RookMoves();   //Working!!
            case ChessPiece.PieceType.KNIGHT -> KnightMoves();
            case ChessPiece.PieceType.BISHOP -> BishopMoves(); //Working!!
            case ChessPiece.PieceType.QUEEN  -> QueenMoves();  //Working!!
            case ChessPiece.PieceType.KING   -> KingMoves();   //Working!!
            case ChessPiece.PieceType.PAWN   -> PawnMoves();
        };
    }

    private Collection<ChessMove> PawnMoves() {
        Collection<ChessMove> myList = new ArrayList<>();
        return myList;
    }

    private Collection<ChessMove> KnightMoves() {
        Collection<ChessMove> myList = new ArrayList<>();
        return  myList;
    }

    private Collection<ChessMove> KingMoves() {
        Collection<ChessMove> myList = new ArrayList<>();

        int row;
        int col;

        //Checks all spots around the piece
        for(int i = -1; i <= 1; i++){
            row = currentRow + i;
            for(int j = -1; j <= 1; j++) {
                col = currentCol + j;
                if(row == currentRow && col == currentCol){
                    continue;
                } else if (!checkSpot(row, col, myList)){
                    break;
                }
            }
        }
        return myList;
    }

    private Collection<ChessMove> QueenMoves() {
        Collection<ChessMove> myList = new ArrayList<>();

        //Checks all spots up and right
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow + i, currentCol + i, myList)){
                break;
            }
        }

        //Checks all spots down and right
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow - i, currentCol + i, myList)){
                break;
            }
        }


        //Checks all spots down and left
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow - i, currentCol - i, myList)){
                break;
            }
        }

        //Checks all spots up and left
        for(int i = 1; i <= 8; i++) {

            if (!checkSpot(currentRow + i, currentCol - i, myList)) {
                break;
            }
        }

        //Checks all spots up
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow + i, currentCol, myList)){
                break;
            }
        }

        //Checks all spots down
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow - i, currentCol, myList)){
                break;
            }
        }

        //Checks all spots on the left
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow, currentCol - i, myList)){
                break;
            }
        }

        //Checks all spots on the right
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow, currentCol + i, myList)){
                break;
            }
        }

        return myList;
    }

    private Collection<ChessMove> BishopMoves() {
        Collection<ChessMove> myList = new ArrayList<>();

        //Checks all of the spots up and right
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow + i, currentCol + i, myList)){
                break;
            }
        }

        //Checks all of the spots down and right
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow - i, currentCol + i, myList)){
                break;
            }
        }

        //Checks all of the spots down and left
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow - i, currentCol - i, myList)){
                break;
            }
        }

        //Checks all of the spots up and left
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow + i, currentCol - i, myList)){
                break;
            }
        }

        return myList;
    }

    private Collection<ChessMove> RookMoves(){
        Collection<ChessMove> myList = new ArrayList<>();

        //Checks all of the spots up
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow + i, currentCol, myList)){
                break;
            }
        }

        //Checks all of the spots down
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow - i, currentCol, myList)){
                break;
            }
        }

        //Checks all of the spots left
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow, currentCol - i, myList)){
                break;
            }
        }

        //Checks all of the spots right
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow, currentCol + i, myList)){
                break;
            }
        }

        return myList;
    }

    /**
     * General function that checks a spot as a valid move based on passed in parameters
     * Will add new moves to the passed in array
     *
     * @param row New row #
     * @param col New col #
     * @param arr movement array
     * @return Boolean to determine if piece's for loop should continue or break
     */
    private Boolean checkSpot(int row, int col, Collection<ChessMove> arr ){

        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }

        ChessPosition newSpot = new ChessPosition(row, col);

        if (board.getPiece(newSpot) == null) {
            arr.add( new ChessMove(myPosition, newSpot, null));
            return true;
        }
        else if (board.getPiece(newSpot).getTeamColor() != myPiece.getTeamColor()) {
            arr.add( new ChessMove(myPosition, newSpot, null));
            return false;
        }
        else {
            return false;
        }
    }

}
