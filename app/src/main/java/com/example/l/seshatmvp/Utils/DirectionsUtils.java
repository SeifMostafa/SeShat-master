package com.example.l.seshatmvp.Utils;

import android.util.Log;

import com.example.l.seshatmvp.model.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class DirectionsUtils {

    public static Direction[] getDirections(String filepath, int version) {
        Stack<Direction> directions = new Stack<>();
        Log.i("path","Path form DirectionsUtils: " + filepath);
        File file = new File(filepath);
        Log.i("My File", "Can Read: "+ file.canRead());
        Log.i("My File", "Can Write: "+ file.canWrite());
        Log.i("My File", "Path from file object:" + file.getAbsolutePath());
        try {
            int foundedversions = 0;
            Scanner scan = new Scanner(file);
            String myLetter = "";
            while (scan.hasNextLine()) {
                String line = null;
                line = scan.nextLine();
                myLetter += line;
//                Log.i("letter","My Letter: " + line);
                switch (line.charAt(0)) {
                    case 'I':
                        foundedversions++;
                        break;
                    case 'E':
                        if (foundedversions > version) {
                            Direction[] result = new Direction[directions.size()];
                            return directions.toArray(result);
                        } else {
                            directions.clear();
                        }
                        break;
                    case 'L':
                        directions.push(Direction.LEFT);
                        break;
                    case 'R':
                        directions.push(Direction.RIGHT);
                        break;
                    case 'U':
                        directions.push(Direction.UP);
                        break;
                    case 'D':
                        directions.push(Direction.DOWN);
                        break;
                    case 'S':
                        directions.push(Direction.SAME);
                        break;
                    case 'N':
                        directions.push(Direction.NOMATTER);
                        break;
                }
            }
            Log.i("Letter","My Letters: " + myLetter);
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        Direction[] result = new Direction[directions.size()];
        return directions.toArray(result);
    }
}
