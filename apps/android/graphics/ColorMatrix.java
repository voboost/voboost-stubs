package android.graphics;

/**
 * Mock ColorMatrix class for Frida agent testing
 */
public class ColorMatrix {
    public ColorMatrix() {
        System.out.println("[ColorMatrix] ColorMatrix created");
    }

    public ColorMatrix(float[] src) {
        System.out.println("[ColorMatrix] ColorMatrix created with array");
    }

    public ColorMatrix(ColorMatrix src) {
        System.out.println("[ColorMatrix] ColorMatrix created from another ColorMatrix");
    }

    public void set(float[] src) {
        System.out.println("[ColorMatrix] set called");
    }

    public void set(ColorMatrix src) {
        System.out.println("[ColorMatrix] set called with ColorMatrix");
    }

    public void reset() {
        System.out.println("[ColorMatrix] reset called");
    }

    public void setSaturation(float sat) {
        System.out.println("[ColorMatrix] setSaturation called with: " + sat);
    }

    public void setScale(float rScale, float gScale, float bScale, float aScale) {
        System.out.println("[ColorMatrix] setScale called");
    }

    public void getArray(float[] array) {
        System.out.println("[ColorMatrix] getArray called");
    }

    public float[] getArray() {
        System.out.println("[ColorMatrix] getArray called");
        return new float[20];
    }

    public void preConcat(ColorMatrix prematrix) {
        System.out.println("[ColorMatrix] preConcat called");
    }

    public void postConcat(ColorMatrix postmatrix) {
        System.out.println("[ColorMatrix] postConcat called");
    }
}
