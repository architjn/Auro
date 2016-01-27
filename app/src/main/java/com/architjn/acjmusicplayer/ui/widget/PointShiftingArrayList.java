package com.architjn.acjmusicplayer.ui.widget;

import java.util.ArrayList;

public class PointShiftingArrayList<T> extends ArrayList<T> {

    private int pointOnShifted = 0;

    @Override
    public void add(int index, T object) {
        super.add(getNewShiftedPoint(index), object);
    }

    public int getNewShiftedPoint(int index) {
        if (size() == 0)
            return 0;
        int newIndex = index + pointOnShifted;
        if (newIndex != size() - 1) {
            if (newIndex >= size() - 1)
                newIndex = size() - newIndex;
            if (newIndex < 0)
                newIndex = -newIndex;
        }
        return newIndex;
    }

    public int getNormalIndex(int index) {
        int newIndex = index + pointOnShifted;
        if (newIndex >= size())
            newIndex -= size();
        return newIndex;
    }

    public T getNormal(int index) {
        return super.get(index);
    }

    public void setPointOnShifted(int pointOnShifted) {
        this.pointOnShifted = pointOnShifted;
    }

    public void copy(ArrayList<T> newList) {
        super.clear();
        super.addAll(newList);
    }

    @Override
    public T get(int index) {
        return super.get(getNewShiftedPoint(index));
    }
}