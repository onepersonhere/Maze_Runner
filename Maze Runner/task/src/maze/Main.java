package maze;

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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the size of a maze");
        mazeRow_Size = scanner.nextInt();
        mazeColumn_Size = scanner.nextInt();
        maze = new int[mazeRow_Size][mazeColumn_Size];

        generateMaze();
        arrToMaze();

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
        while(checkBlocks()){
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
        //picked a random cell
        int i = (int)(Math.random() * (mazeRow_Size - 2) + 1);
        int j = (int)(Math.random() * (mazeColumn_Size - 2) + 1);
        //location in consideration: maze[i][j];
        //using prim's algo
        if(maze[i][j] == 1){
            maze[i][j] = 0;
        }else{
            return 0;
        }
         // set cell to path
        System.out.println("Original: "+ i + " " + j);

        //compute its frontier cell in a 4x4 radius
        LinkedList<int[]> FrontierCells = frontierCells(i,j);

        while(!FrontierCells.isEmpty()){
            System.out.println("Frontier: " + Arrays.deepToString(FrontierCells.toArray()));

            int rand = (int)(Math.random() * FrontierCells.size() + 1);
            int[] pickedCell = FrontierCells.get(rand - 1);
            System.out.println("Chosen Frontier: " + Arrays.toString(pickedCell));
            maze[pickedCell[0]][pickedCell[1]] = 0;
            //Let neighbors(frontierCell) = All cells in distance 2 in state Passage
            LinkedList<int[]> neighbourCells = neighbourCells(pickedCell[0], pickedCell[1]);
            int[] pickedNeighbourCell = new int[2];

            System.out.println("Neighbours: " + Arrays.deepToString(neighbourCells.toArray()));
            if(neighbourCells.size() > 0) {
                rand = (int) (Math.random() * neighbourCells.size() + 1);
                pickedNeighbourCell = neighbourCells.get(rand - 1);

                //cell in btw:
                int x = (pickedCell[0] - pickedNeighbourCell[0]);
                int y = (pickedCell[1] - pickedNeighbourCell[1]);
                if (x == -2) {
                    x = pickedCell[0] + 1;
                }
                if (x == 2) {
                    x = pickedCell[0] - 1;
                }
                if (y == -2) {
                    y = pickedCell[1] + 1;
                }
                if (y == 2) {
                    y = pickedCell[1] - 1;
                }
                if (x == 0) {
                    x = pickedCell[0];
                }
                if (y == 0) {
                    y = pickedCell[1];
                }
                //change the cell in btw to passage
                maze[x][y] = 0;
                System.out.println(x + " " + y);
                arrToMaze();
                //Compute the frontier cells of the chosen frontier cell and add them to the frontier list.
                i = pickedCell[0];
                j = pickedCell[1];
                if (i - 2 > 1 && maze[i - 2][j] == 1)
                    FrontierCells.add(new int[]{i - 2, j});
                if (i + 2 < mazeRow_Size - 1 && maze[i + 2][j] == 1)
                    FrontierCells.add(new int[]{i + 2, j});
                if (j - 2 > 1 && maze[i][j - 2] == 1)
                    FrontierCells.add(new int[]{i, j - 2});
                if (j + 2 < mazeColumn_Size - 1 && maze[i][j + 2] == 1)
                    FrontierCells.add(new int[]{i, j + 2});
            }
            //Remove the chosen frontier cell from the list of frontier cells.
            FrontierCells.remove(pickedCell);
        }

        //else return RNG;

        return 0;
    }
    static LinkedList<int[]> neighbourCells(int i, int j){
        LinkedList<int[]> List = new LinkedList<int[]>();
        if(i-2 > 1 && maze[i-2][j] == 0)
            List.add(new int[]{i-2,j});
        if(i+2 < mazeRow_Size - 1 && maze[i+2][j] == 0)
            List.add(new int[]{i+2,j});
        if(j-2 > 1 && maze[i][j-2] == 0)
            List.add(new int[]{i,j-2});
        if(j+2 < mazeColumn_Size - 1 && maze[i][j+2] == 0)
            List.add(new int[]{i,j+2});

        return List;
    }

    static LinkedList<int[]> frontierCells(int i, int j){
        LinkedList<int[]> List = new LinkedList<int[]>();
        if(i-2 > 1 && maze[i-2][j] == 1)
            List.add(new int[]{i-2,j});
        if(i+2 < mazeRow_Size - 1 && maze[i+2][j] == 1)
            List.add(new int[]{i+2,j});
        if(j-2 > 1 && maze[i][j-2] == 1)
            List.add(new int[]{i,j-2});
        if(j+2 < mazeColumn_Size - 1 && maze[i][j+2] == 1)
            List.add(new int[]{i,j+2});
        return List;
    }
    static void arrToMaze(){
        for(int i = 0; i < mazeRow_Size; i++){
            for (int j = 0; j < mazeColumn_Size; j++){
                if(maze[i][j] == 1){
                    displayWall();
                }
                else {
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
