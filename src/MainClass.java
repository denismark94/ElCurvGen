import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

/**
 * Created by Denis on 02.11.2015.
 */
public class MainClass {
    public static void main(String[] args) {
        int length = 8;
        Random rnd = new Random(System.nanoTime());
        BigInteger p = new BigInteger(length,rnd);
        do {
            p = p.nextProbablePrime();
        } while (!p.mod(new BigInteger("4")).equals(BigInteger.ONE));
        System.out.println(p);
        Complex f = factor(p);
        if (f != null)
            f.print();
    }



    public static Complex factor(BigInteger prime) {
        if (prime.mod(new BigInteger("4")).compareTo(BigInteger.ONE) != 0) {
            System.err.println("Простое число не удовлетворяет условию p = 1 mod 4");
            return null;
        }
        Random rand = new Random(System.nanoTime());
        BigInteger r;
        do {
            r = new BigInteger(prime.bitLength(), rand).mod(prime.subtract(BigInteger.ONE));
        }
        while (!r.modPow(prime.subtract(BigInteger.ONE).divide(new BigInteger("2")), prime).equals(prime.subtract(BigInteger.ONE)) || r.compareTo(BigInteger.ONE) < 1);
        BigInteger z = r.modPow(prime.subtract(BigInteger.ONE).divide(new BigInteger("4")), prime);
        return gcd(new Complex(prime, BigInteger.ZERO), new Complex(z, BigInteger.ONE));
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
}
