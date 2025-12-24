package android.graphics;

/**
 * Stub implementation of Rect for testing
 */
public class Rect {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public Rect() {
        this.left = 0;
        this.top = 0;
        this.right = 0;
        this.bottom = 0;
    }

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Rect(Rect other) {
        this.left = other.left;
        this.top = other.top;
        this.right = other.right;
        this.bottom = other.bottom;
    }

    public boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }

    public int centerX() {
        return (left + right) >> 1;
    }

    public int centerY() {
        return (top + bottom) >> 1;
    }

    public void set(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void set(Rect other) {
        this.left = other.left;
        this.top = other.top;
        this.right = other.right;
        this.bottom = other.bottom;
    }

    public void offset(int dx, int dy) {
        left += dx;
        top += dy;
        right += dx;
        bottom += dy;
    }

    public void offsetTo(int newLeft, int newTop) {
        right += newLeft - left;
        bottom += newTop - top;
        left = newLeft;
        top = newTop;
    }

    public boolean contains(int x, int y) {
        return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom;
    }

    public boolean contains(int left, int top, int right, int bottom) {
        return this.left < this.right && this.top < this.bottom &&
                this.left <= left && this.top <= top &&
                this.right >= right && this.bottom >= bottom;
    }

    public boolean contains(Rect r) {
        return contains(r.left, r.top, r.right, r.bottom);
    }

    public boolean intersect(int left, int top, int right, int bottom) {
        if (this.left < right && left < this.right && this.top < bottom && top < this.bottom) {
            if (this.left < left) this.left = left;
            if (this.top < top) this.top = top;
            if (this.right > right) this.right = right;
            if (this.bottom > bottom) this.bottom = bottom;
            return true;
        }
        return false;
    }

    public boolean intersect(Rect other) {
        return intersect(other.left, other.top, other.right, other.bottom);
    }

    public boolean union(int left, int top, int right, int bottom) {
        if (left < right && top < bottom) {
            if (this.left < this.right && this.top < this.bottom) {
                if (this.left > left) this.left = left;
                if (this.top > top) this.top = top;
                if (this.right < right) this.right = right;
                if (this.bottom < bottom) this.bottom = bottom;
                return true;
            } else {
                this.left = left;
                this.top = top;
                this.right = right;
                this.bottom = bottom;
                return true;
            }
        }
        return false;
    }

    public boolean union(Rect other) {
        return union(other.left, other.top, other.right, other.bottom);
    }

    @Override
    public String toString() {
        return "Rect(" + left + ", " + top + " - " + right + ", " + bottom + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Rect rect = (Rect) obj;
        return left == rect.left && top == rect.top && right == rect.right && bottom == rect.bottom;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }
}
