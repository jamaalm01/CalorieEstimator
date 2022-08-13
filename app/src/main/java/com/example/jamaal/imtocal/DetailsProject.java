package com.example.jamaal.imtocal;

/**
 * Created by Chaitanya on 8/10/2016.
 */
public class DetailsProject {
    String projectName;
    String imageName;
    int platesNo;
    String plateSize;
    String dispName="";

    public String getImageName() {
        return imageName;
    }

    public String getDispName() {
        return dispName;
    }

    public void setDispName(String dispName) {
        this.dispName = dispName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getPlatesNo() {
        return platesNo;
    }

    public void setPlatesNo(int platesNo) {
        this.platesNo = platesNo;
    }

    public String getPlateSize() {
        return plateSize;
    }

    public void setPlateSize(String plateSize) {
        this.plateSize = plateSize;
    }
}
