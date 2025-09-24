package edu.ccrm.domain;

public enum Grade {
    S(10.0, "S - Outstanding"),
    A(9.0, "A - Excellent"),
    B(8.0, "B - Very Good"),
    C(7.0, "C - Good"),
    D(6.0, "D - Satisfactory"),
    E(5.0, "E - Pass"),
    F(0.0, "F - Fail");

    private final double gradePoints;
    private final String description;

    Grade(double gradePoints, String description) {
        this.gradePoints = gradePoints;
        this.description = description;
    }

    public double getGradePoints() {
        return gradePoints;
    }

    public String getDescription() {
        return description;
    }

    public static Grade fromScore(double score) {
        if (score >= 90) return S;
        if (score >= 80) return A;
        if (score >= 70) return B;
        if (score >= 60) return C;
        if (score >= 50) return D;
        if (score >= 40) return E;
        return F;
    }
}
