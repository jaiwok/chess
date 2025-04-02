package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class PrintBoard {
    private static final String[] HEADER_FORWARD = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] HEADER_BACKWARD = {"h", "g", "f", "e", "d", "c", "b", "a"};

    public static void print(ChessGame game, ChessGame.TeamColor perspective) {
        StringBuilder sb = new StringBuilder();
        if (perspective == ChessGame.TeamColor.WHITE) {
            sb.append(makeLettering(HEADER_FORWARD));
            for (int row = 8; row >= 1; row--) {
                sb.append(" ").append(row).append(" ");
                for (int col = 1; col <= 8; col++) {
                    colorSquares(game, row, col, sb);
                }
                sb.append(RESET_BG_COLOR).append(" ").append(row).append("\n");
            }
            sb.append(makeLettering(HEADER_FORWARD));
        } else {
            sb.append(makeLettering(HEADER_BACKWARD));
            for (int row = 1; row <= 8; row++) {
                sb.append(" ").append(row).append(" ");
                for (int col = 8; col >= 1; col--) {
                    colorSquares(game, row, col, sb);
                }
                sb.append(RESET_BG_COLOR).append(" ").append(row).append("\n");
            }
            sb.append(makeLettering(HEADER_BACKWARD));
        }
        sb.append("\n");
        System.out.print(sb);
    }

    private static void colorSquares(ChessGame game, int row, int col, StringBuilder sb) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = game.getBoard().getPiece(position);
        boolean isLightSquare = (row + col) % 2 == 1;
        sb.append(getColoredSquare(piece, isLightSquare));
    }

    private static String makeLettering(String[] letters) {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (String letter : letters) {
            sb.append(letter).append("   ");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String getPieceSymbol(ChessPiece piece) {
        String symbol = EMPTY;
        String color = RESET_TEXT_COLOR;
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                color = SET_TEXT_COLOR_WHITE;
                symbol = switch (piece.getPieceType()) {
                    case KING ->   BLACK_KING;
                    case QUEEN ->  BLACK_QUEEN;
                    case BISHOP -> BLACK_BISHOP;
                    case KNIGHT -> BLACK_KNIGHT;
                    case ROOK ->   BLACK_ROOK;
                    case PAWN ->   BLACK_PAWN;
                };
            } else {
                color = SET_TEXT_COLOR_BLACK;
//                color = SET_TEXT_COLOR_RED;
                symbol = switch (piece.getPieceType()) {
                    case KING -> BLACK_KING;
                    case QUEEN -> BLACK_QUEEN;
                    case BISHOP -> BLACK_BISHOP;
                    case KNIGHT -> BLACK_KNIGHT;
                    case ROOK -> BLACK_ROOK;
                    case PAWN -> BLACK_PAWN;
                };
            }
        }
        return color + symbol + RESET_TEXT_COLOR;
    }

    private static String getColoredSquare(ChessPiece piece, boolean isLightSquare) {
        String bgColor = isLightSquare ? SET_BG_COLOR_BEIGE : SET_BG_COLOR_BROWN;
        String symbol = getPieceSymbol(piece);
        return bgColor + symbol + RESET_BG_COLOR;
    }
}
