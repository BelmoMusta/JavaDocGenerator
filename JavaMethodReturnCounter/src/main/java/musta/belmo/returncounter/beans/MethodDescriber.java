package musta.belmo.returncounter.beans;

public class MethodDescriber {
    private String emplacement;
    private int ligne;
    private String name;
    private int nbReturns;

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbReturns() {
        return nbReturns;
    }

    public void setNbReturns(int nbReturns) {
        this.nbReturns = nbReturns;
    }
}
