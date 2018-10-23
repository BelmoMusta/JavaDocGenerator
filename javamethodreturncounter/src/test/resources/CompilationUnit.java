package musta.belmo.utils.fx.controller;

import java.util.Comparator;

public class CompilationUnit {


    public void zeroReturnMethod() {
        System.out.println("There is no return instruction");
    }


    public int oneReturnMethod() {
        System.out.println("There is no return instruction");
        return 1;
    }

    public int twoReturnMethod(int type) {
        if (type == 0) {
            return 1;
        }
        return 2;
    }

    public int threeReturnMethod(int cs) {
        switch (cs) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
            default:
                return 5;
        }
    }

    public int twoReturnMethodWithinForLoop() {
        for (int i = 0; i < 10; i++) {
            if (i == 5) return 3;

        }
        return 1;
    }

    public int methodWithVoid() {
        return;
    }

    public int methodWithVoidWithSpace() {
        return;
    }

    public int methodeWithOverridenAnonymousClass() {
        return new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        }.compare(0, 1);
    }

}