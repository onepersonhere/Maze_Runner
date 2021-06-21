package maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    private static int mazeRow_Size;
    private static int mazeColumn_Size;
    private static int[][] maze;

    public static void main(String[] args) {
        //2d arr of ints, 1 = wall, 0 = passage
        //10 x 10
        //wall surrounds, except door and exit
        //path from entrance to exit
        //no blocks in maze
        while (true) {
            menu();
        }
    }
    static void menu(){

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Menu ===\n" +
                "1. Generate a new maze\n" +
                "2. Load a maze\n" +
                "0. Exit");
        int key = scanner.nextInt();
        if(key == 1){
            generateNew();
        }
        else if(key == 2){
            loadMaze();
            mazeMenu();
        }
        else if(key == 0){
            System.exit(0);
        }
        else{
            System.out.println("Incorrect option. Please try again");
            menu();
        }
    }
    static void loadMaze(){
        Scanner scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);

            List<List<Integer>> list = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                //reads line by line -> convert to 1s and 0s and store in maze
                List<Integer> line = new ArrayList<>();
                for(int i = 0; i < data.length(); i+=2){
                    if(data.charAt(i) == '\u2588'){
                        line.add(1);
                    }else if(data.charAt(i) == ' '){
                        line.add(0);
                    }else{
                        System.out.println("Cannot load the maze. It has an invalid format.");
                        break;
                    }
                }
                //if invalid format -> Cannot load the maze. It has an invalid format.
                list.add(line);
            }
            mazeRow_Size = list.size();
            mazeColumn_Size = list.get(0).size();
            maze = new int[mazeRow_Size][mazeColumn_Size];

            for(int i = 0; i < mazeRow_Size; i++){
                for(int j = 0; j < mazeColumn_Size; j++){
                    maze[i][j] = list.get(i).get(j);
                }
            }
            myReader.close();
            //System.out.println("Task completed Successfully");
        } catch (FileNotFoundException e) {
            System.out.println("The file "+filename+" does not exist");
            e.printStackTrace();
        }
    }
    static void generateNew(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the size of a new maze");
        mazeColumn_Size = mazeRow_Size = scanner.nextInt();
        maze = new int[mazeRow_Size][mazeColumn_Size];
        generateMaze();
        arrToMaze(false);
        mazeMenu();
    }
    static void writeMaze(FileWriter myWriter) throws IOException {
        for(int i = 0; i < mazeRow_Size; i++){
            for (int j = 0; j < mazeColumn_Size; j++){
                if(maze[i][j] == 1){
                    myWriter.write("\u2588\u2588");
                }
                else {
                    myWriter.write("  ");
                }
            }
            myWriter.write("\n");
        }
    }
    static void saveMaze(){
        Scanner scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        try {
            FileWriter myWriter = new FileWriter(filename);
            writeMaze(myWriter);
            myWriter.close();
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    static int[] findOpening(){
        int[] opening = new int[2];
        for(int i = 0; i < mazeRow_Size; i++){
            if(maze[i][0] == 0){
                opening = new int[]{i, 0};
                break;
            }
            if(maze[i][mazeColumn_Size-1] == 0){
                opening = new int[]{i, mazeColumn_Size-1};
                break;
            }
        }
        return opening;
    }
    static int[] findExit(){
        int[] exit = new int[2];

        for(int i = 0; i < mazeColumn_Size; i++){
            if(maze[0][i] == 0){
                exit = new int[]{0,i};
                break;
            }
            if(maze[mazeRow_Size-1][i] == 0){
                exit = new int[]{mazeRow_Size-1,i};
                break;
            }
        }
        return exit;
    }
    static void mark(int[] coord){
        maze[coord[0]][coord[1]] = 3;
    }
    static void unMark(int[] coord){
        maze[coord[0]][coord[1]] = 4;
    }
    private static LinkedList<int[]> Passage = new LinkedList<>();
    private static List<int[]> Visited = new ArrayList<>();
    //all visited coord
    //if visited, mark with 4
    static int search(int[] coord){
        //adds avaliable coord to Passage
        Passage = new LinkedList<>();
        int passageways = 0;
        //right
        if(coord[1] + 1 < mazeColumn_Size && maze[coord[0]][coord[1] + 1] == 0){
            Passage.add(new int[]{coord[0], coord[1] + 1});
            passageways++;
        }
        //left
        if(coord[1] - 1 > 0 && maze[coord[0]][coord[1] - 1] == 0){
            Passage.add(new int[]{coord[0], coord[1] - 1});
            passageways++;
        }
        //up
        if(coord[0] + 1 < mazeRow_Size && maze[coord[0] + 1][coord[1]] == 0){
            Passage.add(new int[]{coord[0] + 1, coord[1]});
            passageways++;
        }
        //down
        if(coord[0] - 1 > 0 && maze[coord[0] - 1][coord[1]] == 0){
            Passage.add(new int[]{coord[0] - 1, coord[1]});
            passageways++;
        }


        System.out.println("Passage: " + Arrays.deepToString(Passage.toArray()));
        return passageways;
    }
    static boolean searchAlgo(int[] coord, int[] exit, int passageways, Deque<int[]> stack){

        while(coord != exit) {
            //if only 1 passage, go to that passage.
            System.out.println("Stack: " + Arrays.deepToString(stack.toArray()));
            arrToMaze(true);
            if (passageways == 1) {
                coord = Passage.getLast();//err
                System.out.println("Chosen Coord: " + Arrays.toString(coord));

                mark(coord);
                Visited.add(coord);
                passageways = search(coord);

                if(searchAlgo(coord,exit,passageways,stack)){
                    int i = Visited.indexOf(stack.getLast());
                    for(int n = Visited.size() - 1; n >= i; n--){
                        int[] arr = Visited.get(n);
                        unMark(arr);
                    }
                    System.out.println("Coord removed from stack: " + stack.getLast());
                    stack.remove();
                    //coord = stack.getLast();
                    //searchAlgo(coord,exit,passageways,stack);
                }
            }
            //if 2 or more passage, choose random. store that coord in the stack
            if (passageways >= 2) { //split!!!
                while(Passage.size() != 0){//doesn't work, was keep adding the last
                    stack.add(Passage.getLast());
                    Passage.removeLast();
                }
                coord = stack.getLast();
                System.out.println("Chosen Coord: " + Arrays.toString(coord));

                mark(coord);
                Visited.add(coord);
                passageways = search(coord);
                //if ever reach dead end, break out of recursion and choose the other one
                if(searchAlgo(coord,exit,passageways,stack)){
                    int i = Visited.indexOf(stack.getLast());
                    for(int n = Visited.size() - 1; n >= i; n--){
                        int[] arr = Visited.get(n);
                        unMark(arr);
                    }
                    System.out.println("Coord removed from stack: " + Arrays.toString(stack.getLast()));
                    stack.remove();
                    //coord = stack.getLast();
                    //searchAlgo(coord,exit,passageways,stack);
                }

            }
            //if no passage, return back to last coord, choose another (implement bool)
            if (passageways == 0) {
                return true;
            }
        }
        return false;
    }
    static void autoSolver(){
        //find openings at perimeter
        int[] opening = findOpening();
        mark(opening);
        int[] exit = findExit();
        int[] coord = new int[2];
        //use a Deque
        Deque<int[]> stack = new ArrayDeque<>();
        //search up, down, left right
        int passageways = search(opening);

        searchAlgo(coord, exit, passageways, stack);
        //mark the coords of the lines between the coords with 3;
        arrToMaze(true);
    }

    static void mazeMenu(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Menu ===\n" +
                "1. Generate a new maze\n" +
                "2. Load a maze\n" +
                "3. Save the maze\n" +
                "4. Display the maze\n" +
                "5. Find the escape\n" +
                "0. Exit");
        int key = scanner.nextInt();
        if(key == 1){
            generateNew();
        }
        else if(key == 2){
            loadMaze();
        }
        else if(key == 3){
            saveMaze();
        }
        else if(key == 4){
            arrToMaze(false);
        }
        else if(key == 5){
            autoSolver();
        }
        else if(key == 0){
            System.exit(0);
        }
        else{
            System.out.println("Incorrect option. Please try again");
            mazeMenu();
        }
        mazeMenu();
    }
    static void displayPass(){
        System.out.print("  ");
    }
    static void displayWall(){
        System.out.print("\u2588\u2588");
    }
    static void generateMaze(){
        //insert 1 and 0s
        //make everything wall first
        for(int[] i : maze){
            Arrays.fill(i, 1);
        }
        //System.out.println(Arrays.deepToString(maze));
        //create 2 openings -> 0
        generateOpenings();

        //0 iff theres a 0 up, down, left or right
        //otherwise RNG
        //if 0 iff theres a 0 up, down, left or right, RNG is returned
        while (checkBlocks()) {
            RNG();
        }

    }
    static boolean checkBlocks(){
        //walls on the inside cannot have 3x3 block
        for(int i = 1; i < mazeRow_Size - 1; i++){
            for(int j = 1; j < mazeColumn_Size - 1; j++){
                if(maze[i][j] == 1){
                    if(maze[i][j+1] == 1 && maze[i][j-1] == 1
                            && maze[i+1][j] == 1 && maze[i-1][j] == 1
                            && maze[i+1][j+1] == 1 && maze[i+1][j-1] == 1
                            && maze[i-1][j+1] == 1 && maze[i-1][j-1] == 1){
                        return true;
                    }
                }
            }
        }
        //opening must reach exit -> for every 3 column, 0s must connect

        return false;
    }
    static int RNG(){
        int i = (int)(Math.random() * (mazeRow_Size - 2) + 1); //1-8 random
        int j = (int)(Math.random() * (mazeColumn_Size - 2) + 1); //1-8

        //location in consideration: maze[i][j];
        //check at location at maze[i+1][j] [i-1][j] [i][j+1] [i][j-1].
        //at least 2 walls
        int Count = 0;
        if(maze[i+1][j] == 0 || maze[i-1][j] == 0 || maze[i][j+1] == 0 || maze[i][j-1] == 0){
            if(maze[i][j+1] == 0){
                Count++;
            }
            if(maze[i][j-1] == 0){
                Count++;
            }
            if(maze[i+1][j] == 0){
                Count++;
            }
            if(maze[i-1][j] == 0){
                Count++;
            }
            if(Count < 3) {
                maze[i][j] = 0;
                return 0;
            }
        }
        //else return RNG;
        return RNG();
    }
    static void arrToMaze(boolean bool){
        //to ensure there is a wall
        boolean displayEscape = bool;
        for(int i = 0; i < mazeColumn_Size; i++){
            if(maze[0][i] == 0){
                maze[0][i] = 1;
            }
            if(maze[mazeRow_Size - 1][i] == 0){
                maze[mazeRow_Size - 1][i] = 1;
            }
        }
        //actual code
        for(int i = 0; i < mazeRow_Size; i++){
            for (int j = 0; j < mazeColumn_Size; j++){
                if(maze[i][j] == 1){
                    displayWall();
                }
                else if(displayEscape && maze[i][j] == 3){
                    System.out.print("//");
                }else{
                    displayPass();
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    static void generateOpenings(){
        int opening = (int)(Math.random() * (mazeRow_Size - 2) + 1);
        maze[opening][0] = 0;
        int exit = (int)(Math.random() * (mazeRow_Size - 2) + 1);
        maze[exit][mazeColumn_Size - 1] = 0;
    }
}
