/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idev.com.drawline.Model;

public class Latitude {
    private Double degree;
    private Double minute;
    private Double second;
    private String direction;

    public Latitude(Double degree, Double minute, Double second, String direction) {
        this.degree = degree;
        this.minute = minute;
        this.second = second;
        this.direction = direction;
    }

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }

    public Double getMinute() {
        return minute;
    }

    public void setMinute(Double minute) {
        this.minute = minute;
    }

    public Double getSecond() {
        return second;
    }

    public void setSecond(Double second) {
        this.second = second;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    

}    