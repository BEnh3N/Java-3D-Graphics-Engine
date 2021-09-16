package com.benh3n;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Meshes {
    public static class vec3D implements Cloneable {
        double x;
        double y;
        double z;
        public vec3D(){

        }
        public vec3D(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public vec3D clone() {
            try {
                vec3D clone = (vec3D) super.clone();
                clone.x = this.x;
                clone.y = this.y;
                clone.z = this.z;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class triangle implements Cloneable {
        vec3D p1;
        vec3D p2;
        vec3D p3;
        short col;
        public triangle(){
            this.p1 = new vec3D(0.0f, 0.0f, 0.0f);
            this.p2 = new vec3D(0.0f, 0.0f, 0.0f);
            this.p3 = new vec3D(0.0f, 0.0f, 0.0f);
            this.col = 0;
        }
        public triangle(vec3D p1, vec3D p2, vec3D p3){
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

        public triangle clone() {
            try {
                triangle clone = (triangle) super.clone();
                clone.p1 = this.p1.clone();
                clone.p2 = this.p2.clone();
                clone.p3 = this.p3.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class mesh {
        ArrayList<triangle> tris;
        public mesh(){
            this.tris = new ArrayList<>();
        }

        public static mesh loadObjectFromFile(String sFilename){
            mesh tempMesh = new mesh();
            try {
                File file = new File(sFilename);
                Scanner in = new Scanner(file);
                ArrayList<vec3D> vecList = new ArrayList<>();
                ArrayList<triangle> triList = new ArrayList<>();
                while (in.hasNextLine()) {
                    String[] line = in.nextLine().split(" ");
                    if (line[0].equals("v")){
                        float x = Float.parseFloat(line[1]);
                        float y = Float.parseFloat(line[2]);
                        float z = Float.parseFloat(line[3]);
                        vecList.add(new vec3D(x, y, z));
                    } else if (line[0].equals("f")){
                        int p1 = Integer.parseInt(line[1]) - 1;
                        int p2 = Integer.parseInt(line[2]) - 1;
                        int p3 = Integer.parseInt(line[3]) - 1;
                        triangle tri = new triangle(vecList.get(p1), vecList.get(p2), vecList.get(p3));
                        triList.add(tri);
                    }
                }
                in.close();
                tempMesh.tris = triList;
            } catch (FileNotFoundException e) {
                System.out.println("File not Found!");
                e.printStackTrace();
            }
            return tempMesh;
        }
    }
}
