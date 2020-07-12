package com.glushkov.annotationsimplementation;

import java.util.ArrayList;

public class TestClass {

    private int count = 0;

    @Inject(int.class)
    private int cost = 10;


    @Inject(ArrayList.class)
    private ArrayList arrayList;

    @Run
    public void changeCountValue() {
        count = 100;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList getArrayList() {
        return arrayList;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setArrayList(ArrayList arrayList) {
        this.arrayList = arrayList;
    }
}
