package com.example.register_tcc;

public class IaInputData {
    private int gravidez;
    private double glicose;
    private double imc;
    private int idade;

    public IaInputData(int gravidez, double glicose, double imc, int idade) {
        this.gravidez = gravidez;
        this.glicose = glicose;
        this.imc = imc;
    }

    public int getGravidez() { return gravidez; }
    public void setGravidez(int gravidez) { this.gravidez = gravidez; }

    public double getGlicose() { return glicose; }
    public void setGlicose(double glicose) { this.glicose = glicose; }
    
    public double getImc() { return imc; }
    public void setImc(double imc) { this.imc = imc; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }
}