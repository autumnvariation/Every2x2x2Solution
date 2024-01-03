import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SolutionsGenerator {
    public static HashSet<String> allMoves = new HashSet<>();
    public static HashSet<String> solvedCases = new HashSet<>();
    public static HashMap<String,BufferedImage> colorToImage = new HashMap<>();
    private static void generateSolutions() throws IOException {
        //permutation: order of all pieces
        //orientation: how each piece is rotated
        PriorityQueue<String> queue = new PriorityQueue<>();
        queue.add("00abcdefgh");
        FileWriter writer = new FileWriter("res/solutions/solutions.txt");
        int count = 0;
        while(!queue.isEmpty()){
            String current = queue.poll();
            String currentState = current.substring(2, 10);
            String currentAlg = current.substring(10);
            if (solvedCases.add(currentState) && !isRotationDuplicate(currentState)){
                count ++;
                System.out.println(count);
                writer.write(currentState + ":" + currentAlg + "\n");
                ArrayList<String> variations = getVariations(current);
                queue.addAll(variations);
            }
        }
        writer.close();
    }

    private static ArrayList<String> getVariations(String current) {
        String currentState = current.substring(2, 10);

        ArrayList<String> variations = new ArrayList<>();
        HashSet<String> moves = new HashSet<>(allMoves);

        String currentAlg = current.substring(10);
        String[] currentAlgMoves = currentAlg.split(" ");
        String lastMove = currentAlgMoves[currentAlgMoves.length - 1];
        if (!lastMove.isEmpty()){
            moves.remove(lastMove.substring(0,1));
            moves.remove(lastMove.charAt(0) + "'");
            moves.remove(lastMove.charAt(0) + "2");
        }

        for (String move : moves) {
            String nextState = executeMove(currentState, move);

            if (solvedCases.contains(nextState)){
                continue;
            }
            if (isRotationDuplicate(nextState)){
                continue;
            }

            variations.add((String.format("%02d", (Integer.parseInt(current.substring(0, 2)) + 1)) + nextState + currentAlg + " " + move).trim());
        }

        return variations;
    }

    private static boolean isRotationDuplicate(String nextState) {
        ArrayList<String> rotationVariations = new ArrayList<>();

        rotationVariations.add(executeAlg(nextState, "U D'"));
        rotationVariations.add(executeAlg(nextState, "U2 D2"));
        rotationVariations.add(executeAlg(nextState, "U' D"));
        rotationVariations.add(executeAlg(nextState, "F B'"));
        rotationVariations.add(executeAlg(nextState, "F B' U D'"));
        rotationVariations.add(executeAlg(nextState, "F B' U2 D2"));
        rotationVariations.add(executeAlg(nextState, "F B' U' D"));
        rotationVariations.add(executeAlg(nextState, "F2 B2"));
        rotationVariations.add(executeAlg(nextState, "F2 B2 U D'"));
        rotationVariations.add(executeAlg(nextState, "F2 B2 U2 D2"));
        rotationVariations.add(executeAlg(nextState, "F2 B2 U' D"));
        rotationVariations.add(executeAlg(nextState, "F' B"));
        rotationVariations.add(executeAlg(nextState, "F' B U D'"));
        rotationVariations.add(executeAlg(nextState, "F' B U2 D2"));
        rotationVariations.add(executeAlg(nextState, "F' B U' D"));
        rotationVariations.add(executeAlg(nextState, "R L'"));
        rotationVariations.add(executeAlg(nextState, "R L' U D'"));
        rotationVariations.add(executeAlg(nextState, "R L' U2 D2"));
        rotationVariations.add(executeAlg(nextState, "R L' U' D"));
        rotationVariations.add(executeAlg(nextState, "R' L"));
        rotationVariations.add(executeAlg(nextState, "R' L U D'"));
        rotationVariations.add(executeAlg(nextState, "R' L U2 D2"));
        rotationVariations.add(executeAlg(nextState, "R' L U' D"));
        for (String rotationVariation : rotationVariations) {
            if (solvedCases.contains(rotationVariation)){
                return true;
            }
        }
        return false;
    }

    static String executeAlg(String state, String alg) {
        if (alg == null || alg.isEmpty()) {
            return state;
        }

        String[] moves = alg.split(" ");
        for (String move : moves) {
            if (move.isEmpty()) {
                continue;
            }
            state = executeMove(state, move);
        }
        return state;
    }
    static String executeMove(String state, String move) {
        StringBuilder returnState = new StringBuilder();

        //UBL, UBR, UFR, UFL, DBL, DBR, DFL, DFR
        //00,  01,  02,  03,  04,  05,  06,  07,
        //WBO, WBR, WGR, WGO, YBO, YBR, YGO, YGR
        //  a,   b,   c,   d,   e,   f,   g,   h //oriented
        //  i,   j,   k,   l,   m,   n,   o,   p //clockwise
        //  q,   r,   s,   t,   u,   v,   w,   x //counter-clockwise
        switch (move) {
            case "U" -> {
                returnState.append(state.charAt(3));
                returnState.append(state.charAt(0));
                returnState.append(state.charAt(1));
                returnState.append(state.charAt(2));
                returnState.append(state, 4, 8);
            }
            case "U'" -> {
                return executeMove(executeMove(executeMove(state, "U"), "U"), "U");
            }
            case "U2" -> {
                return executeMove(executeMove(state, "U"), "U");
            }
            case "R" -> {
                returnState.append(state.charAt(0));
                returnState.append(rotateCorner(state.charAt(2), true));//
                returnState.append(rotateCorner(state.charAt(7), false));//
                returnState.append(state, 3, 5);
                returnState.append(rotateCorner(state.charAt(1), false));//
                returnState.append(state.charAt(6));
                returnState.append(rotateCorner(state.charAt(5), true));//
            }
            case "R'" -> {
                return executeMove(executeMove(executeMove(state, "R"), "R"), "R");
            }
            case "R2" -> {
                return executeMove(executeMove(state, "R"), "R");
            }
            case "F" -> {
                returnState.append(state, 0, 2);
                returnState.append(rotateCorner(state.charAt(3), true));
                returnState.append(rotateCorner(state.charAt(6), false));
                returnState.append(state, 4, 6);
                returnState.append(rotateCorner(state.charAt(7), true));
                returnState.append(rotateCorner(state.charAt(2), false));
            }
            case "F'" -> {
                return executeMove(executeMove(executeMove(state, "F"), "F"), "F");
            }
            case "F2" -> {
                return executeMove(executeMove(state, "F"), "F");
            }

            case "D" -> {
                returnState.append(state, 0, 4);
                returnState.append(state.charAt(5));
                returnState.append(state.charAt(7));
                returnState.append(state.charAt(4));
                returnState.append(state.charAt(6));
            }
            case "D'" -> {
                return executeMove(executeMove(executeMove(state, "D"), "D"), "D");
            }
            case "D2" -> {
                return executeMove(executeMove(state, "D"), "D");
            }

            case "L" -> {
                returnState.append(rotateCorner(state.charAt(4), false));
                returnState.append(state, 1, 3);
                returnState.append(rotateCorner(state.charAt(0), true));
                returnState.append(rotateCorner(state.charAt(6), true));
                returnState.append(state.charAt(5));
                returnState.append(rotateCorner(state.charAt(3), false));
                returnState.append(state.charAt(7));
            }

            case "L'" -> {
                return executeMove(executeMove(executeMove(state, "L"), "L"), "L");
            }
            case "L2" -> {
                return executeMove(executeMove(state, "L"), "L");
            }

            case "B" -> {
                returnState.append(rotateCorner(state.charAt(1), true));
                returnState.append(rotateCorner(state.charAt(5), false));
                returnState.append(state, 2, 4);
                returnState.append(rotateCorner(state.charAt(0), false));
                returnState.append(rotateCorner(state.charAt(4), true));
                returnState.append(state, 6, 8);
            }
            case "B'" -> {
                return executeMove(executeMove(executeMove(state, "B"), "B"), "B");
            }
            case "B2" -> {
                return executeMove(executeMove(state, "B"), "B");
            }
        }
        return returnState.toString();
    }
    public static char rotateCorner(char corner, boolean clockwise) {
        String abc = "abcdefghijklmnopqrstuvwx".repeat(2);
        if (clockwise) {
            return abc.charAt(abc.indexOf(corner) + 8);
        }
        return abc.charAt(abc.indexOf(corner) + 16);
    }

    private static String reverseAlg(String alg) {
        if (alg.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String[] moves = alg.split(" ");
        for (int i = moves.length - 1; i != -1; i--) {
            sb.append(reverseMove(moves[i])).append(" ");
        }
        return sb.toString();
    }
    private static String reverseMove(String move) {
        if (move.length() == 1) {
            return move + "'";
        }
        if (move.substring(1).equals("'")) {
            return move.substring(0, 1);
        }
        return move;
    }

    public static void main (String[] args) throws IOException {
        allMoves.add("D");
        allMoves.add("D2");
        allMoves.add("D'");
        allMoves.add("L");
        allMoves.add("L2");
        allMoves.add("L'");

        allMoves.add("B");
        allMoves.add("B2");
        allMoves.add("B'");

        generateSolutions();
        tempFormatSolutions();
        loadStringToImage();
        loadFont();
        drawSolutions();
    }

    private static void loadStringToImage() throws IOException {
        File folder = new File("res/colors");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                colorToImage.put(listOfFiles[i].getName(),ImageIO.read(listOfFiles[i]));
            }
        }
    }

    private static void drawSolutions() throws IOException {
        Scanner scanner = new Scanner(new File("res/solutions/solutions_formatted.txt"));
        ArrayList<String> algsAndStates = new ArrayList<>();
        int currentImage = 0;
        while (scanner.hasNextLine()){
            for (int i = 0; i < 96; i++) {
                try{
                    algsAndStates.add(scanner.nextLine());
                } catch (Exception ignored) {
                }
            }
            BufferedImage img = new BufferedImage(1920,1080,BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setFont(customFont);
            g2.setBackground(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            for (int i = 1; i < 9; i++) {
                g2.drawLine(0,135 * i - 1,1920,135 * i - 1);
            }
            for (int i = 1; i < 13; i++) {
                g2.drawLine(i * 160 - 1,0,i * 160 - 1,1080);
            }

            for (int y = 0; y < 8; y++) {
                for (int x = 1; x < 13; x++) {
                    g2.drawLine(160 * x - 81, y * 135 + 6, 160 * x - 81, y * 135 + 94);
                }
            }
            int index = 0;
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 12; x++) {
                    StringBuilder halfAlg1 = new StringBuilder();
                    StringBuilder halfAlg2 = new StringBuilder();
                    String alg = algsAndStates.get(index).substring(9);
                    String state = algsAndStates.get(index).substring(0, 8);
                    drawDefaultCPiece(x, y, g2);

                    drawColors(x, y, g2, state);

                    g2.setColor(Color.WHITE);

                    if (g2.getFontMetrics().stringWidth(alg) > 153){
                        String[] moves = alg.split(" ");
                        int moveIndex = 0;
                        for (int i = 0; i < moves.length/2 + 1; i++) {
                            halfAlg1.append(moves[moveIndex]).append(" ");
                            moveIndex++;
                        }
                        for (int i = moveIndex; i < moves.length; i++) {
                            halfAlg2.append(moves[moveIndex]).append(" ");
                            moveIndex++;
                        }
                        if (g2.getFontMetrics().stringWidth(halfAlg2.toString()) > 153 || g2.getFontMetrics().stringWidth(halfAlg1.toString()) > 153){
                            g2.setFont(customFont.deriveFont(16f));
                            g2.drawString(halfAlg1.toString(), x * 160 + 3, y * 135 + 109 + 3);
                            g2.drawString(halfAlg2.toString(), x * 160 + 3, y * 135 + 109 + 19 + 3);
                            g2.setFont(customFont);
                        }
                        else{
                            g2.drawString(halfAlg1.toString(), x * 160 + 3, y * 135 + 109 + 3);
                            g2.drawString(halfAlg2.toString(), x * 160 + 3, y * 135 + 109 + 19 + 3);
                        }
                    }
                    else {
                        g2.drawString(alg, x * 160 + 3, y * 135 + 112);
                    }
                    index++;
                }
            }
            File outputfile = new File("res/images/" + currentImage + ".png");
            ImageIO.write(img, "png", outputfile);
            algsAndStates.clear();
            currentImage++;
            System.out.println(38273 - currentImage + " images left");
        }
    }

    private static void drawColors(int x, int y, Graphics2D g2, String state) {
        ArrayList<String> colors = getColors(state);
        g2.drawImage(colorToImage.get("top1" + colors.get(0)), x * 160 + 24,y * 135 + 14, null);
        g2.drawImage(colorToImage.get("top2" + colors.get(1)), x * 160 + 6,y * 135 + 23, null);
        g2.drawImage(colorToImage.get("top3" + colors.get(2)), x * 160 + 41,y * 135 + 23, null);
        //left cube row 2
        g2.drawImage(colorToImage.get("left" + colors.get(3)), x * 160 + 6,y * 135 + 33, null);
        g2.drawImage(colorToImage.get("right" + colors.get(4)), x * 160 + 57,y * 135 + 33, null);
        //left cube row 3
        g2.drawImage(colorToImage.get("left" + colors.get(5)), x * 160 + 6,y * 135 + 52, null);
        g2.drawImage(colorToImage.get("left" + colors.get(6)), x * 160 + 23,y * 135 + 61, null);
        g2.drawImage(colorToImage.get("right" + colors.get(7)), x * 160 + 40,y * 135 + 61, null);
        g2.drawImage(colorToImage.get("right" + colors.get(8)), x * 160 + 57,y * 135 + 52, null);
        //right cube
        g2.drawImage(colorToImage.get("top1" + colors.get(9)), x * 160 + 104,y * 135 + 14, null);
        g2.drawImage(colorToImage.get("top2" + colors.get(10)), x * 160 + 86,y * 135 + 23, null);
        g2.drawImage(colorToImage.get("top3" + colors.get(11)), x * 160 + 121,y * 135 + 23, null);
        g2.drawImage(colorToImage.get("top4" + colors.get(12)), x * 160 + 103,y * 135 + 32, null);
        //right cube row 2
        g2.drawImage(colorToImage.get("left" + colors.get(13)), x * 160 + 86,y * 135 + 33, null);
        g2.drawImage(colorToImage.get("left" + colors.get(14)), x * 160 + 103,y * 135 + 42, null);
        g2.drawImage(colorToImage.get("right" + colors.get(15)), x * 160 + 120,y * 135 + 42, null);
        g2.drawImage(colorToImage.get("right" + colors.get(16)), x * 160 + 137,y * 135 + 33, null);
        //right cube row 3
        g2.drawImage(colorToImage.get("left" + colors.get(17)), x * 160 + 86,y * 135 + 52, null);
        g2.drawImage(colorToImage.get("left" + colors.get(18)), x * 160 + 103,y * 135 + 61, null);
        g2.drawImage(colorToImage.get("right" + colors.get(19)), x * 160 + 120,y * 135 + 61, null);
        g2.drawImage(colorToImage.get("right" + colors.get(20)), x * 160 + 137,y * 135 + 52, null);
    }

    private static ArrayList<String> getColors(String state) {
        ArrayList<String> colors = new ArrayList<>();
        //UBL, UBR, UFR, UFL, DBL, DBR, DFL, DFR
        //00,  01,  02,  03,  04,  05,  06,  07,
        //WBO, WBR, WGR, WGO, YBO, YBR, YGO, YGR
        // aW,  bW,  cW,  dW,  eY,  fY,  gY,  hY //oriented
        // iB,  jR,  kG,  lO,  mO,  nB,  oG,  pR //clockwise
        // qO,  rB,  sR,  tG,  uB,  vR,  wO,  xG //counter-clockwise
        int[] topOrder = {0,3,1};
        for (int i = 0; i < 3; i++) {

            char piece = state.charAt(topOrder[i]);
            if ("abcd".indexOf(piece) != -1){
                colors.add("white.png");
            }
            else if ("efgh".indexOf(piece) != -1){
                colors.add("yellow.png");
            }
            else if ("irun".indexOf(piece) != -1){
                colors.add("blue.png");
            }
            else if ("ktox".indexOf(piece) != -1){
                colors.add("green.png");
            }
            else if ("qlmw".indexOf(piece) != -1){
                colors.add("orange.png");
            }
            else if ("jsvp".indexOf(piece) != -1){
                colors.add("red.png");
            }
        }
        colors.add(getClockwiseColor(state.charAt(3)));
        colors.add(getCounterClockwiseColor(state.charAt(1)));
        colors.add(getCounterClockwiseColor(state.charAt(6)));
        colors.add(getClockwiseColor(state.charAt(7)));
        colors.add(getCounterClockwiseColor(state.charAt(7)));
        colors.add(getClockwiseColor(state.charAt(5)));
        //second cube
        int[] topOrder1 = {7,6,5,4};
        for (int i = 0; i < 4; i++) {

            char piece = state.charAt(topOrder1[i]);
            if ("abcd".indexOf(piece) != -1){
                colors.add("white.png");
            }
            else if ("efgh".indexOf(piece) != -1){
                colors.add("yellow.png");
            }
            else if ("irun".indexOf(piece) != -1){
                colors.add("blue.png");
            }
            else if ("ktox".indexOf(piece) != -1){
                colors.add("green.png");
            }
            else if ("qlmw".indexOf(piece) != -1){
                colors.add("orange.png");
            }
            else if ("jsvp".indexOf(piece) != -1){
                colors.add("red.png");
            }
        }
        //second cube row 2
        colors.add(getClockwiseColor(state.charAt(6)));
        colors.add(getCounterClockwiseColor(state.charAt(4)));
        colors.add(getClockwiseColor(state.charAt(4)));
        colors.add(getCounterClockwiseColor(state.charAt(5)));
        //second cube row 3
        colors.add(getCounterClockwiseColor(state.charAt(3)));
        colors.add(getClockwiseColor(state.charAt(0)));
        colors.add(getCounterClockwiseColor(state.charAt(0)));
        colors.add(getClockwiseColor(state.charAt(1)));
        return colors;
    }

    private static String getCounterClockwiseColor(char piece) {
        //UBL, UBR, UFR, UFL, DBL, DBR, DFL, DFR
        //00,  01,  02,  03,  04,  05,  06,  07,
        //WBO, WBR, WGR, WGO, YBO, YBR, YGO, YGR
        // aO,  bB,  cR,  dG,  eB,  fR,  gO,  hG //oriented
        // iW,  jW,  kW,  lW,  mY,  nY,  oY,  pY //clockwise
        // qB,  rR,  sG,  tO,  uO,  vB,  wG,  xR //counter-clockwise

        if ("qrst".indexOf(piece) != -1){
            return "white.png";
        }
        else if ("uvwx".indexOf(piece) != -1){
            return "yellow.png";
        }
        else if ("ajmf".indexOf(piece) != -1){
            return "blue.png";
        }
        else if ("clgp".indexOf(piece) != -1){
            return "green.png";
        }
        else if ("ideo".indexOf(piece) != -1){
            return "orange.png";
        }
        else if ("bknh".indexOf(piece) != -1){
            return "red.png";
        }

        return null;
    }

    private static String getClockwiseColor(char piece) {
        //UBL, UBR, UFR, UFL, DBL, DBR, DFL, DFR
        //00,  01,  02,  03,  04,  05,  06,  07,
        //WBO, WBR, WGR, WGO, YBO, YBR, YGO, YGR
        // aO,  bB,  cR,  dG,  eB,  fR,  gO,  hG //oriented
        // iW,  jW,  kW,  lW,  mY,  nY,  oY,  pY //clockwise
        // qB,  rR,  sG,  tO,  uO,  vB,  wG,  xR //counter-clockwise
        if ("ijkl".indexOf(piece) != -1){
            return "white.png";
        }
        else if ("mnop".indexOf(piece) != -1){
            return "yellow.png";
        }
        else if ("qbev".indexOf(piece) != -1){
            return "blue.png";
        }
        else if ("sdwh".indexOf(piece) != -1){
            return "green.png";
        }
        else if ("atug".indexOf(piece) != -1){
            return "orange.png";
        }
        else if ("rcfx".indexOf(piece) != -1){
            return "red.png";
        }
        return null;
    }

    private static void drawDefaultCPiece(int x, int y, Graphics2D g2) {
        g2.drawImage(colorToImage.get("cPiece.png"), x * 160 + 23, y * 135 + 32, null);
    }

    private static void tempFormatSolutions() throws IOException {
        Scanner scanner = new Scanner(new File("res/solutions/solutions.txt"));
        FileWriter formattedSolutions = new FileWriter("res/solutions/solutions_formatted.txt");
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String state = line.substring(0, 8);
            String alg = "";
            try{

                alg = line.substring(10);
            }catch (Exception ignored){}
            formattedSolutions.write(state + ":" + reverseAlg(alg.trim()).trim() + "\n");
        }
        formattedSolutions.close();
    }
    private static Font customFont;
    private static void loadFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "\\res\\font\\MinecraftRegular-Bmg3.ttf")).deriveFont((float) 20);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //register the font
        ge.registerFont(customFont);
    }
}
