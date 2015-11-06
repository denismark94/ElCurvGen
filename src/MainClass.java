import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

/**
 * Created by Denis on 02.11.2015.
 */
public class MainClass {
    public static BigInteger ONE = BigInteger.ONE;
    public static BigInteger ZERO = BigInteger.ZERO;
    public static BigInteger TWO = new BigInteger("2");
    public static BigInteger FOUR = new BigInteger("4");

    public static void main(String[] args) {
        int length = 8;
        Random rnd = new Random(System.nanoTime());
        BigInteger p = new BigInteger(length,rnd);
        do {
            p = p.nextProbablePrime();
        } while (!p.mod(new BigInteger("4")).equals(ONE));
        System.out.println(p);
        Complex f = factor(p);
        BigInteger ord = checkOrder(p,f.a,f.b);
        if (f != null & ord!= null) {
            f.print();
            System.out.println(ord);
            System.out.println(checkDivisibility(p,ord,1,30));
        }
    }



    public static Complex factor(BigInteger prime) {
        if (prime.mod(FOUR).compareTo(ONE) != 0) {
            System.err.println("Простое число не удовлетворяет условию p = 1 mod 4");
            return null;
        }
        Random rand = new Random(System.nanoTime());
        BigInteger r;
        do {
            r = new BigInteger(prime.bitLength(), rand).mod(prime.subtract(ONE));
        }
        while (!r.modPow(prime.subtract(ONE).divide(TWO), prime).equals(prime.subtract(ONE)) || r.compareTo(ONE) < 1);
        BigInteger z = r.modPow(prime.subtract(ONE).divide(FOUR), prime);
        return gcd(new Complex(prime, ZERO), new Complex(z, ONE));
    }

    public static Complex gcd(Complex a, Complex b) {
        Complex r = a.mod(b);
        Complex temp;
        while (!r.isZero()) {
            temp = b;
            b = r;
            a = temp;
            r = a.mod(b);
        }
        return b;
    }

    public static BigInteger checkOrder (BigInteger p, BigInteger d, BigInteger e) {
        BigInteger[] ord = new BigInteger[4];
        ord[0] = p.add(ONE).add(d.multiply(TWO));
        ord[1] = p.add(ONE).subtract(d.multiply(TWO));
        ord[2] = p.add(ONE).add(e.multiply(TWO));
        ord[3] = p.add(ONE).subtract(e.multiply(TWO));
        for (int i = 0; i < 4; i++)
            if (ord[i].divide(TWO).isProbablePrime(100) || ord[i].divide(FOUR).isProbablePrime(100))
                return ord[i];
        return null;
    }

    public static boolean checkDivisibility(BigInteger p,BigInteger ord,int n, int k) {
        for (int i = 1; i <= k; i++) {
            if(ord.mod(p.pow((int)Math.pow(n,i)).subtract(ONE)).equals(ZERO))
                return false;
        }
        return true;
    }
}
