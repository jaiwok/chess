package chess.calculatemoves;


import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

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

    public Collection<ChessMove> listMoves(){
//        System.out.println(board.toString());
        return switch ( myPiece.getPieceType() ) {
            case ChessPiece.PieceType.ROOK   -> rookMoves();   //Working!!
            case ChessPiece.PieceType.KNIGHT -> knightMoves(); //Working!!
            case ChessPiece.PieceType.BISHOP -> bishopMoves(); //Working!!
            case ChessPiece.PieceType.QUEEN  -> queenMoves();  //Working!!
            case ChessPiece.PieceType.KING   -> kingMoves();   //Working!!
            case ChessPiece.PieceType.PAWN   -> pawnMoves();   //Working!!
        };
    }

    private Collection<ChessMove> pawnMoves() {        Collection<ChessMove> arr = new ArrayList<>();
        int row = currentRow;
        int col = currentCol;
        ChessPosition newSpot;
        if(myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            row -= 1;
        } else if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            row += 1;
        }

        newSpot = new ChessPosition(row, col);
        checkPawnMove(row, col, newSpot, arr);

        return arr;
    }

    private void checkPawnMove(int row, int col, ChessPosition newSpot, Collection<ChessMove> arr) {

        if(board.getPiece(newSpot) == null){
            if(row == 1 || row == 8) {
                promotePawn(arr, newSpot);
            }
            else if(currentRow == 7 || currentRow == 2) {
                arr.add(new ChessMove(myPosition, newSpot, null));

                int i = (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
                int rowPlusOne = row + i;
                newSpot = new ChessPosition(rowPlusOne, col);
                if(board.getPiece(newSpot) == null){
                    arr.add(new ChessMove(myPosition, newSpot, null));
                }
            }
            else{
                arr.add(new ChessMove(myPosition, newSpot, null));
            }
        }
        col += 1;
        if(col <=8) {
            newSpot = new ChessPosition(row, col);
            if (board.getPiece(newSpot) != null && board.getPiece(newSpot).getTeamColor() != myPiece.getTeamColor()) {
                if(row == 1 || row == 8) {
                    promotePawn(arr, newSpot);
                }
                else{
                    arr.add(new ChessMove(myPosition, newSpot, null));
                }
            }
        }
        col -= 2;
        if (col >= 1) {
            newSpot = new ChessPosition(row, col);
            if (board.getPiece(newSpot) != null && board.getPiece(newSpot).getTeamColor() != myPiece.getTeamColor()) {
                if(row == 1 || row == 8) {
                    promotePawn(arr, newSpot);
                }
                else{
                    arr.add(new ChessMove(myPosition, newSpot, null));
                }
            }
        }
    }

    private void promotePawn(Collection<ChessMove> arr, ChessPosition newSpot) {
        arr.add( new ChessMove(myPosition, newSpot, ChessPiece.PieceType.ROOK));
        arr.add( new ChessMove(myPosition, newSpot, ChessPiece.PieceType.KNIGHT));
        arr.add( new ChessMove(myPosition, newSpot, ChessPiece.PieceType.BISHOP));
        arr.add( new ChessMove(myPosition, newSpot, ChessPiece.PieceType.QUEEN));
    }

    private Collection<ChessMove> knightMoves() {
        Collection<ChessMove> myList = new ArrayList<>();

        int[] row = {-2,-1,1,2};
        int[] col = {-2,-1,1,2};

        //Checks all spots up and right
        for(int i : row){
            for(int j : col) {
                if(abs(i) == abs(j)){
                    //skip iteration
                    continue;
                } else if (!checkSpot(currentRow + i, currentCol + j, myList)){
                    //keep checking on this iteration as each spot is not related to another
                    continue;
                }
            }
        }

        return  myList;
    }

    private Collection<ChessMove> kingMoves() {
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

    private Collection<ChessMove> queenMoves() {
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

    private Collection<ChessMove> bishopMoves() {
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
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow + i, currentCol - i, myList)){
                break;
            }
        }

        return myList;
    }

    private Collection<ChessMove> rookMoves(){
        Collection<ChessMove> myList = new ArrayList<>();

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

        //Checks all spots left
        for(int i = 1; i <= 8; i++){

            if (!checkSpot(currentRow, currentCol - i, myList)){
                break;
            }
        }

        //Checks all spots right
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
