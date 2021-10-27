package com.benh3n.structs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Mesh {
    private ArrayList<Triangle> tris;

    public Mesh() {

    }
    public Mesh(ArrayList<Triangle> tris) {
        this.tris = tris;
    }

//    public static Mesh loadObjectFromFile(String sFilename){
//        Mesh tempMesh = new Mesh();
//        try {
//            File file = new File(sFilename);
//            Scanner in = new Scanner(file);
//            ArrayList<Vec3D> vecList = new ArrayList<>();
//            ArrayList<Triangle> triList = new ArrayList<>();
//            while (in.hasNextLine()) {
//                String[] line = in.nextLine().split(" ");
//                switch (line[0]) {
//                    case "v":
//                        float x = Float.parseFloat(line[1]);
//                        float y = Float.parseFloat(line[2]);
//                        float z = Float.parseFloat(line[3]);
//                        vecList.add(new Vec3D(x, y, z));
//                        break;
//
//                    case "f":
//                        int p0 = Integer.parseInt(line[1]) - 1;
//                        int p1 = Integer.parseInt(line[2]) - 1;
//                        int p2 = Integer.parseInt(line[3]) - 1;
//                        Triangle tri = new Triangle(vecList.get(p0), vecList.get(p1), vecList.get(p2));
//                        triList.add(tri);
//                        break;
//                }
//            }
//            in.close();
//            tempMesh.tris = triList;
//        } catch (FileNotFoundException e) {
//            System.out.println("File not Found!");
//            e.printStackTrace();
//        }
//        return tempMesh;
//    }

    public Mesh(String path) {
        try {
            Scanner in = new Scanner(new File(path));
            ArrayList<Vec3D> vecList = new ArrayList<>();
            ArrayList<Triangle> triList = new ArrayList<>();
            while (in.hasNextLine()) {
                String[] line = in.nextLine().split(" ");
                switch (line[0]) {
                    case "v":
                        float x = Float.parseFloat(line[1]);
                        float y = Float.parseFloat(line[2]);
                        float z = Float.parseFloat(line[3]);
                        vecList.add(new Vec3D(x, y, z));
                        break;

                    case "f":
                        int p0 = Integer.parseInt(line[1]) - 1;
                        int p1 = Integer.parseInt(line[2]) - 1;
                        int p2 = Integer.parseInt(line[3]) - 1;
                        Triangle tempTri = new Triangle(vecList.get(p0), vecList.get(p1), vecList.get(p2));
                        triList.add(tempTri);
                        break;
                }
            }
            in.close();
            this.tris = triList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Triangle> getTris() {
        return tris;
    }

    public void setTris(ArrayList<Triangle> tris) {
        this.tris = tris;
    }
}
