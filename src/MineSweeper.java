import java.util.Scanner;

public class MineSweeper {

    /* Change the size of the board here */
    static int boardSize = 10;

    static int rowLimit = boardSize;
    static int colLimit = boardSize;

    /* Change the amount of mines on the board */
    static int numberOfMines = 15;

    static char coveredCell = '-';
    static char uncoveredCell = '.';
    static char flagged = 'F';
    static char mines = 'm';

    static char[][] initializeBoard = new char[rowLimit][colLimit];
    static char[][] completeBoard = new char[rowLimit][colLimit];

    enum validSyntax {
        VALID, NOT_VALID
    }

    enum Command {
        FLAG, SWEEP, QUIT, UNKNOWN, CHECK_MINES

    }

    public static void createBoard() {
        int i, j;

        System.out.print("    ");

        for (i = 0; i < boardSize; i++) {
            char c = 65;
            System.out.printf("%c  ", c + i);
        }
        String str = "-";
        System.out.println();
        System.out.println("  +" + str.repeat(29));

        for (i = 0; i < boardSize; i++) {
            System.out.printf("%d | ", i);
            for (j = 0; j < boardSize; j++) {
                System.out.printf("%c  ", initializeBoard[i][j]);
            }
            System.out.println();
            System.out.println();
        }

    }

    public static void fillBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                initializeBoard[i][j] = completeBoard[i][j] = coveredCell;
            }
        }
    }

    public static void minePlacer(int numberOfMines) {
        int createdMines;

        for (createdMines = 0; createdMines < numberOfMines; ) {
            for (int i = 0; i < rowLimit; i++) {

                for (int j = 0; j < colLimit; j++) {

                    if (Math.random() < 0.01) {
                        if (completeBoard[i][j] == mines) {
                            break;
                        }
                        completeBoard[i][j] = mines;
                        createdMines++;
                        if (createdMines == numberOfMines) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void mineBoard() {

        int i, j;

        System.out.print("    ");

        for (i = 0; i < boardSize; i++) {
            char c = 65;
            System.out.printf("%c  ", c + i);
        }
        String str = "-";
        System.out.println();
        System.out.println("  +" + str.repeat(29));

        for (i = 0; i < boardSize; i++) {
            System.out.printf("%d | ", i);
            for (j = 0; j < boardSize; j++) {
                System.out.printf("%c  ", completeBoard[i][j]);
            }
            System.out.println();
            System.out.println();
        }
    }

    public static Command parseCommand(String userInput) {

        String commandString = userInput.split(" ")[0];

        String[] commandStringSplit = commandString.split("");

        switch (commandString) {
            case "flag":
                return Command.FLAG;
            case "clear":
                return Command.SWEEP;
            case "quit":
                return Command.QUIT;
            case "printmines":
                return Command.CHECK_MINES;
            default:
                if (!commandStringSplit[0].matches("^[frq]") || (commandStringSplit.length <= 1)) {
                    return Command.UNKNOWN;
                }
                break;
        }
        return null;
    }

    public static String[] parseArguments(String userInput) {

        String[] commandAndArguments = userInput.split(" ");

        String[] arguments = new String[commandAndArguments.length - 1];
        if (commandAndArguments.length - 1 >= 0)
            System.arraycopy(commandAndArguments, 1, arguments, 0, commandAndArguments.length - 1);

        return arguments[0].split("");
    }

    public static int col(String[] splittedArg) {

        String splittedColumn = splittedArg[0];
        int colCoordinate = splittedColumn.charAt(0);
        colCoordinate = colCoordinate - 97;
        return colCoordinate;
    }

    public static int row(String[] splittedArg) {

        String splittedRow = splittedArg[1];
        int rowCoordinate = splittedRow.charAt(0);
        rowCoordinate = rowCoordinate - 48;
        return rowCoordinate;
    }

    public static void printSyntaxError() {

        System.out.println("Syntax error! Try again.");
    }

    public static void handleCommandFlag(String[] columnsAndRows) {

        if (initializeBoard[row(columnsAndRows)][col(columnsAndRows)] == coveredCell) {
            initializeBoard[row(columnsAndRows)][col(columnsAndRows)] = flagged;
        } else if (initializeBoard[row(columnsAndRows)][col(columnsAndRows)] == flagged) {
            initializeBoard[row(columnsAndRows)][col(columnsAndRows)] = coveredCell;
        }
    }

    public static void handleCommandSweep(String[] columnsAndRows) {

        if (ifMineInCell(row(columnsAndRows), col(columnsAndRows))) {
            sweepAll();
        }
        checkNearbyCells(row(columnsAndRows), col(columnsAndRows));
    }

    public static void handleCommandQuit() {

        System.out.println("Exiting...");
        System.exit(0);

    }

    public static boolean checkIfWon() {
        int count = 0;
        for (int row = 0; row < boardSize; row++)
            for (int column = 0; column < boardSize; column++)
                if (completeBoard[row][column] == coveredCell)
                    count++;
        return count == 0;
    }

    public static void sweepAll() {

        for (int row = 0; row < boardSize; row++)
            for (int column = 0; column < boardSize; column++)
                if (completeBoard[row][column] != mines)
                    checkNearbyCells(row, column);
        youLost();
    }

    public static boolean ifValidCell(int checkRow, int checkCol) {
        return (checkRow >= 0) && (checkRow < rowLimit) && (checkCol >= 0) && (checkCol < colLimit);

    }

    public static boolean ifMineInCell(int row, int col) {
        return completeBoard[row][col] == mines;
    }

    public static char changeCellState(int rowCell, int colCell, int cellState) {

        char newCellState = completeBoard[rowCell][colCell];
        if (ifValidCell(rowCell, colCell)) {
            if (cellState == 0) {
                initializeBoard[rowCell][colCell] = uncoveredCell;
                completeBoard[rowCell][colCell] = uncoveredCell;
            } else {
                newCellState = (char) (cellState + 48);
                initializeBoard[rowCell][colCell] = newCellState;
                completeBoard[rowCell][colCell] = newCellState;
            }
        }
        return newCellState;
    }

    public static void checkNearbyCells(int row, int col) {

        int count = 0;

        if (ifValidCell(row, col))
            if (completeBoard[row][col] == mines) {
                return;
            }
        if ((ifValidCell(row - 1, col))) {
            if (completeBoard[row - 1][col] == mines) {
                count++;
            }
        }
        if ((ifValidCell(row - 1, col + 1)))
            if (completeBoard[row - 1][col + 1] == mines) {
                count++;
            }
        if ((ifValidCell(row, col + 1)))
            if (completeBoard[row][col + 1] == mines) {
                count++;
            }
        if ((ifValidCell(row + 1, col + 1)))
            if (completeBoard[row + 1][col + 1] == mines) {
                count++;
            }
        if ((ifValidCell(row + 1, col)))
            if (completeBoard[row + 1][col] == mines) {
                count++;
            }
        if ((ifValidCell(row + 1, col - 1)))
            if (completeBoard[row + 1][col - 1] == mines) {
                count++;
            }
        if ((ifValidCell(row, col - 1)))
            if (completeBoard[row][col - 1] == mines) {
                count++;
            }
        if ((ifValidCell(row - 1, col - 1)))
            if (completeBoard[row - 1][col - 1] == mines) {
                count++;
            }
        char cellState = changeCellState(row, col, count);

        if ((ifValidCell(row - 1, col)))
            if (cellState == coveredCell) {
                checkNearbyCells(row - 1, col);
            }
        if ((ifValidCell(row - 1, col + 1)))
            if (cellState == coveredCell) {
                checkNearbyCells(row - 1, col + 1);
            }
        if ((ifValidCell(row, col + 1)))
            if (cellState == coveredCell) {
                checkNearbyCells(row, col + 1);
            }
        if ((ifValidCell(row + 1, col + 1)))
            if (cellState == coveredCell) {
                checkNearbyCells(row + 1, col + 1);
            }
        if ((ifValidCell(row + 1, col)))
            if (cellState == coveredCell) {
                checkNearbyCells(row + 1, col);
            }
        if ((ifValidCell(row + 1, col - 1)))
            if (cellState == coveredCell) {
                checkNearbyCells(row + 1, col - 1);
            }
        if ((ifValidCell(row, col - 1)))
            if (cellState == coveredCell) {
                checkNearbyCells(row, col - 1);
            }
        if ((ifValidCell(row - 1, col - 1)))
            if (cellState == coveredCell) {
                checkNearbyCells(row - 1, col - 1);
            }
    }

    public static void youWin() {

        System.out.println("WELL DONE !");
        System.exit(0);
    }

    public static void youLost() {

        mineBoard();
        System.out.println("GAME OVER");

        System.exit(0);

    }

    public static validSyntax checkValidSyntax(String userInput) {

        String[] commandAndArguments = userInput.split(" ");
        if (commandAndArguments.length == 2) {

            String[] arguments = new String[commandAndArguments.length - 1];

            System.arraycopy(commandAndArguments, 1, arguments, 0, commandAndArguments.length - 1);
            String[] argumentsSplit = arguments[0].split("");

            if (argumentsSplit.length == 2) {

                if ((argumentsSplit[0].matches("[a-j]")) && (argumentsSplit[1].matches("[0-9]"))) {

                    return validSyntax.VALID;
                }
            }
        }
        return validSyntax.NOT_VALID;
    }

    public static void main(String[] args){

        fillBoard();
        minePlacer(numberOfMines);
        createBoard();

        Scanner scanner = new Scanner(System.in);

        //noinspection InfiniteLoopStatement
        while (true) {

            System.out.print("> ");
            String userInput = scanner.nextLine();


            Command command = parseCommand(userInput);
            validSyntax syntax = checkValidSyntax(userInput);

            if (command == Command.UNKNOWN) {
                System.out.println("Unknown command. Try again.");
                continue;
            } else if (command == Command.CHECK_MINES) {
                mineBoard();
                continue;
            } else if (command == Command.QUIT) {
                handleCommandQuit();
            } else if (syntax == validSyntax.NOT_VALID) {
                printSyntaxError();
                continue;
            }

            String[] splittedArg = parseArguments(userInput);

            if (command == Command.FLAG) {
                handleCommandFlag(splittedArg);
            } else if (initializeBoard[row(splittedArg)][col(splittedArg)] != coveredCell) {
                System.out.println("Not allowed, try different move.");
                continue;
            } else if (command == Command.SWEEP) {
                handleCommandSweep(splittedArg);

            }
            createBoard();

            if (checkIfWon()) {
                youWin();
            }
        }
    }
}