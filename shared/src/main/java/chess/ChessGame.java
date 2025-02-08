package chess;

import java.util.Collection;
import java.util.ArrayList;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;
    private ChessBoard board;

    //Extra credit
    private boolean whiteKingMoved, blackKingMoved;
    private boolean blackRook1Moved, blackRook2Moved;
    private boolean whiteRook1Moved, whiteRook2Moved;
    private boolean pawnMoved2Spots;
    private ChessPosition pawnMovement;


    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTeam = WHITE;

        this.whiteKingMoved = false;
        this.blackKingMoved = false;
        this.blackRook1Moved = false;
        this.blackRook2Moved = false;
        this.whiteRook1Moved = false;
        this.whiteRook2Moved = false;
        this.pawnMoved2Spots = false;
        pawnMovement = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if(piece == null){
            return null;
        }

        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> filteredMoves = new ArrayList<>();

        for(ChessMove move : possibleMoves){

            ChessBoard testBoard = duplicateBoard();

            ChessPosition start = move.getStartPosition();
            ChessPosition end   = move.getEndPosition();

            //Theoretical movement as if movements allows my king to be in check then that's bad
            testBoard.addPiece(start, null);
            testBoard.addPiece(end, piece);
            if(!isInCheck(color, testBoard)){
                filteredMoves.add(move);
            }
        }

        if(piece.getPieceType() == PAWN && pawnMoved2Spots){
            if(startPosition.getRow() == pawnMovement.getRow() && (abs(startPosition.getColumn() - pawnMovement.getColumn()) == 1)) {
                int rowOffset = color == WHITE ? startPosition.getRow() + 1 : startPosition.getRow() - 1;
                ChessPosition end = new ChessPosition(rowOffset, pawnMovement.getColumn());
                ChessMove enPassAunt = new ChessMove(startPosition, end, null);
                filteredMoves.add(enPassAunt);
            }
        }

        return filteredMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try{
            ChessPiece piece = board.getPiece(move.getStartPosition());
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            TeamColor pieceColor = piece.getTeamColor();

            if(pieceColor != getTeamTurn()){
                throw new InvalidMoveException();
            }

            if(validMoves.isEmpty()){
                throw new InvalidMoveException();
            } else if(!validMoves.contains(move)){
                throw new InvalidMoveException();
            }

            //remove piece
            board.addPiece(move.getStartPosition(), null);
            //add new piece
            if(move.getPromotionPiece() == null){
                board.addPiece(move.getEndPosition(), piece);

                //if it was an enpassant move remove other pawn
                if(piece.getPieceType() == PAWN && move.getStartPosition().getColumn() != move.getEndPosition().getColumn()){
                    ChessPosition clearPawn = new ChessPosition(move.getEndPosition().getRow()+1, move.getEndPosition().getColumn());
                    board.addPiece(clearPawn, null);
                    clearPawn = new ChessPosition(move.getEndPosition().getRow()-1, move.getEndPosition().getColumn());
                    board.addPiece(clearPawn, null);
                }

                //determine pawn has moved two spots
                extraCreditMovementBoolean(move, piece, pieceColor);

            } else {
                board.addPiece(move.getEndPosition(), new ChessPiece(pieceColor, move.getPromotionPiece()));
            }

            //switch turns
            setTeamTurn(pieceColor == WHITE ? BLACK: WHITE);

        } catch(Exception NullPointerException){
            throw new InvalidMoveException();
        }
    }

    /**
     * Sets which king and rook and pawn pieces have moved for the extra credit en pass aunt and castling
     *
     * @param move The move that just took place
     * @param piece The piece that moved there
     * @param pieceColor The color of that piece
     */
    private void extraCreditMovementBoolean(ChessMove move, ChessPiece piece, TeamColor pieceColor) {

        if(piece.getPieceType() == PAWN && (abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) == 2)){
            pawnMoved2Spots = true;
            pawnMovement = move.getEndPosition();
        } else if (piece.getPieceType() == PAWN) {
            pawnMoved2Spots = false;
            pawnMovement = null;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    /**
     * Determines if the given team is in check with potential move on a new board
     *
     * @param color which team to check for check
     * @param testBoard deep copy of currentGame
     * @return True if the specified team is in check
     */
    private boolean isInCheck(TeamColor color, ChessBoard testBoard) {
        ChessPosition myKingLocation = findKing(color, testBoard);
        for (int i = 1; i <=8 ; i++) {
            for (int j = 1; j <=8 ; j++) {

                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = testBoard.getPiece(pos);
                if(piece !=null && piece.getTeamColor() != color){
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(testBoard, pos);
                    if (kingInDanger(possibleMoves, myKingLocation)) {
                        return true;
                    }
                }

            }
        }
        return false; //My king was not in danger
    }

    /**
     * Determines if a king is in danger from the other teams possible moves
     *
     * @param possibleMoves moves possible by a piece on the other team
     * @param myKingLocation location of my King to check if is in danger
     * @return boolean  King is in danger or no
     */
    private boolean kingInDanger(Collection<ChessMove> possibleMoves, ChessPosition myKingLocation){
        for (ChessMove move: possibleMoves){
            if(myKingLocation.equals(move.getEndPosition())){
                return true;
            }
        }
        return false; //King is safe
    }

    /**
     * Finds a teams king
     *
     * @param color the team to find the king of
     * @param board the board to find the king in
     * @return ChessPosition of the king or null
     */
    private ChessPosition findKing(TeamColor color, ChessBoard board){
        ChessPiece thisKing = new ChessPiece(color, KING);

        for (int i = 1; i <=8 ; i++) {
            for (int j = 1; j <=8 ; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.equals(thisKing)){
                    return pos;
                }
            }
        }
        return null; //If no king is found then you messed up
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> moveToFreeKing = validMoves(pos);
                    if(!moveToFreeKing.isEmpty()){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean check = isInCheck(teamColor, board);
        boolean checkMate = isInCheckmate(teamColor);

        if(checkMate || check){
            return false;
        }

        for (int i = 1; i < 9 ; i++) {
            for (int j = 1; j < 9 ; j++) {
                ChessPosition pos = newPosition(i,j);
                ChessPiece piece = board.getPiece(pos);
                if(piece !=null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> possibleMoves = validMoves(pos);
                    if (!possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true; //if all piece possible moves comes back empty then it's Stalemate
    }

    /**
     * Helper function to get rid of duplicate code
     *
     * @param row row for new position
     * @param col column for new position
     * @return the new ChessPosition object
     */
    private ChessPosition newPosition(int row,int col){
        return new ChessPosition(row, col);
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
        return board;
    }

    /**
     * duplicates the chessboard for testing
     *
     * @return a duplicated chessboard
     */
    private ChessBoard duplicateBoard(){
        ChessBoard newBoard = new ChessBoard();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8 ; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null){
                    newBoard.addPiece(position, piece);
                }
            }
        }
        return newBoard;
    }
}