package chess;

import java.util.Collection;
import java.util.ArrayList;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;
    private ChessBoard board;
    boolean gameOver;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTeam = WHITE;
        this.gameOver = false;
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
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> filteredMoves = new ArrayList<>();

        for(ChessMove move : possibleMoves){
            ChessBoard testBoard = duplicateBoard();

            ChessPosition start = move.getStartPosition();
            ChessPosition end   = move.getEndPosition();

            testBoard.addPiece(start, null);
            testBoard.addPiece(end, piece); //do I need to worry about promotions here?
            if(!isInCheck(color, testBoard)){
                filteredMoves.add(move);
            }
        }


        //need to filter this for valid moves that prevent check/checkmate
        //also add moves for en passaunt and castling

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
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @param testBoard deep copy of currentGame
     * @return True if the specified team is in check
     */
    private boolean isInCheck(TeamColor color, ChessBoard testBoard) {
        throw new RuntimeException("Not Implemented");
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
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
        return board;
    }

    /**
     * duplicates the chessboard for testing
     *
     * @return a duplicated chessboard
     */
    private ChessBoard duplicateBoard(){
        ChessBoard newBoard = new ChessBoard();
        for (int i = 0; i <= 8; i++){
            for (int j = 0; j <= 8 ; j++) {
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

//
//public boolean isInCheck(TeamColor teamColor) {
//
//    ChessPosition kingPosition = findKing(teamColor, board);
//    // iterate through the whole board
//    for(int row =  1; row <= 8; row++){
//        for(int col =  1; col <= 8; col++){
//            ChessPosition position = new ChessPosition(row, col); // current square
//            ChessPiece piece = board.getPiece(position); // what piece is at the square
//            if(piece != null && piece.getTeamColor() != teamColor) { // enemy piece
//                if (canCaptureKing(piece.pieceMoves(board, position), kingPosition)) {
//                    return true;
//                }
//            }
//
//        }
//    }
//    return false; // no pieces can capture the king in their current location
//}
//
//private boolean theoreticalIsInCheck(TeamColor teamColor, ChessBoard localBoard) {
//    ChessPosition kingPosition = findKing(teamColor, localBoard);
//    // iterate through the whole board
//    for(int row =  1; row <= 8; row++){
//        for(int col =  1; col <= 8; col++){
//            ChessPosition position = new ChessPosition(row, col); // current square
//            ChessPiece piece = localBoard.getPiece(position); // what piece is at the square
//            if(piece != null && piece.getTeamColor() != teamColor) { // enemy piece
//                Collection<ChessMove> allMoves = piece.pieceMoves(localBoard, position);
//                if (canCaptureKing(allMoves, kingPosition)) {
//                    return true;
//                }
//            }
//
//        }
//    }
//    return false; // no pieces can capture the king
//}