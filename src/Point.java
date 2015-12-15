import sun.applet.Main;

import java.math.BigInteger;

/**
 * Created by Denis on 23.11.2015.
 */
public class Point {
    public BigInteger x;
    public BigInteger y;
    public BigInteger p;
    public BigInteger a;
    private BigInteger ZERO = BigInteger.ZERO;
    private BigInteger TWO = new BigInteger("2");
    private BigInteger THREE = new BigInteger("3");

    public Point(BigInteger x, BigInteger y, BigInteger p) {
        this.x = x;
        this.y = y;
        this.p = p;
        if (!x.equals(ZERO))
        this.a = (((y.pow(2)).subtract(x.pow(3))).multiply(MainClass.gcdExtended(x,p)[1]).mod(p));
    }

    public Point add(Point P2) {
        BigInteger lambda,x3,y3;
        if (this.equals(P2)) {
            if (y.equals(ZERO))
                return null;
            lambda = (THREE.multiply(x.pow(2)).add(a)).multiply(MainClass.gcdExtended(TWO.multiply(y), p)[1]);
            x3 = (lambda.pow(2).subtract(TWO.multiply(x))).mod(p);
            y3 = (lambda.multiply(x.subtract(x3)).subtract(y)).mod(p);
        } else {
            if (x.equals(P2.x))
                return null;
            lambda = ((P2.y.subtract(y)).
                    multiply(minv(P2.x.subtract(x),p))).mod(p);
            x3 = (lambda.pow(2).subtract(P2.x).subtract(x)).mod(p);
            y3 = (lambda.multiply(x.subtract(x3)).subtract(y)).mod(p);
        }
        return new Point(x3,y3,p);
    }

    public boolean equals(Point a) {
        return (this.x.equals(a.x) && this.y.equals(a.y));
    }

    public BigInteger minv(BigInteger x, BigInteger p) {
        return MainClass.gcdExtended(x,p)[1];
    }
}
