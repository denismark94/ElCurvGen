import java.math.BigInteger;

/**
 * Created by Denis on 02.11.2015.
 */
public class Complex {

    BigInteger a;
    BigInteger b;
    public static Complex ZERO = new Complex(0,0);

    public Complex(String a, String b) {
        this.a = new BigInteger(a);
        this.b = new BigInteger(b);
    }

    public Complex(int a, int b) {
        this.a = new BigInteger(String.valueOf(a));
        this.b = new BigInteger(String.valueOf(b));
    }

    public Complex(BigInteger a, BigInteger b) {
        this.a = a;
        this.b = b;
    }

    public Complex div(Complex y) {
        //Домножение на сопряженное избавляет от i в знаменателе
        Complex z = this.mult(y.conjugate());
        //знаменатель = a^2-(bi)^2 = a^2+b^2;
        BigInteger denom = y.a.multiply(y.a).add(y.b.multiply(y.b));
        BigInteger a = null, b = null;
        int comparator = z.a.abs().mod(denom.abs()).compareTo(denom.divide(new BigInteger("2")));
        if (comparator > 0)
            if (z.a.compareTo(BigInteger.ZERO) * denom.compareTo(BigInteger.ZERO) == -1)
                a = z.a.divide(denom).subtract(BigInteger.ONE);
            else
                a = z.a.divide(denom).add(BigInteger.ONE);
        else
            a = z.a.divide(denom);
        comparator = z.b.abs().mod(denom.abs()).compareTo(denom.divide(new BigInteger("2")));
        if (comparator > 0)
            if (z.b.compareTo(BigInteger.ZERO) * denom.compareTo(BigInteger.ZERO) == -1)
                b = z.b.divide(denom).subtract(BigInteger.ONE);
            else
                b = z.b.divide(denom).add(BigInteger.ONE);
        else
            b = z.b.divide(denom);
        return new Complex(a, b);
    }

    public Complex mod(Complex y) {
        Complex d = this.div(y);
        Complex e = y.mult(d);
        return new Complex(this.a.subtract(e.a),this.b.subtract(e.b));
    }

    public boolean isZero() {
        return(this.a.equals(BigInteger.ZERO) & this.b.equals(BigInteger.ZERO));
    }

    public Complex mult(Complex y) {
        BigInteger za = this.a.multiply(y.a).subtract(this.b.multiply(y.b));
        BigInteger zb = this.b.multiply(y.a).add(this.a.multiply(y.b));
        return new Complex(za, zb);
    }

    public Complex conjugate() {
        return new Complex(this.a, this.b.negate());
    }

    public void print() {
        int azero = this.a.compareTo(BigInteger.ZERO);
        int bzero = this.b.compareTo(BigInteger.ZERO);
        int bone = this.b.abs().compareTo(BigInteger.ONE);
        if (azero != 0)
            System.out.print(this.a);
        switch (bzero) {
            case -1:
                if (bone == 0)
                    System.out.println("-i");
                else
                    System.out.println("-" + this.b.negate() + "i");
                break;
            case 1:
                if (bone == 0)
                    System.out.println("+i");
                else
                    System.out.println("+" + this.b + "i");
                break;
            case 0:
                if (this.a.equals(BigInteger.ZERO))
                    System.out.println(0);
                System.out.println();
        }
    }
}
