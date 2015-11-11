import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

/**
 * Created by Denis on 02.11.2015.
 */
public class MainClass {
    public static BigInteger ZERO = BigInteger.ZERO;
    public static BigInteger ONE = BigInteger.ONE;
    public static BigInteger TWO = new BigInteger("2");
    public static BigInteger THREE = new BigInteger("3");
    public static BigInteger FOUR = new BigInteger("4");

    public static void main(String[] args) {
        int length = 8;
        Random rnd = new Random(System.nanoTime());
        //Генерация простого заданной длины
        BigInteger p = new BigInteger(length, rnd);
        do {
            p = p.nextProbablePrime();
        } while (!p.mod(new BigInteger("4")).equals(ONE));
        System.out.println(p);

        //Разложение на сумму квадратов
        Complex f = factor(p);
        if (f == null) {
            System.err.println("Простое число не удовлетворяет условию p = 1 mod 4");
            return;
        }
        f.print();
        //Проверка на выполнение следствий
        BigInteger[] ord = order(p,f.a,f.b);
        if (!checkOrder(ord)) {
            System.err.println("Не выполняется условие для порядка");
            return;
        }
        if (!checkDivisibility(p, ord, 1, 30)) {
            System.err.println("Не выполняется условие делимости");
            return;
        }
        BigInteger[] P_0 = genPoint(p, ord);
        System.out.println(String.format("P_0 = (%d;%d)", P_0[0], P_0[1]));
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

    public static BigInteger[] order(BigInteger p, BigInteger d, BigInteger e)   {
        BigInteger[] ord = new BigInteger[4];
        //#E(GF(p) = p + 1 + 2d
        ord[0] = p.add(ONE).add(d.multiply(TWO));
        //#E(GF(p) = p + 1 - 2d
        ord[1] = p.add(ONE).subtract(d.multiply(TWO));
        //#E(GF(p) = p + 1 + 2e
        ord[2] = p.add(ONE).add(e.multiply(TWO));
        //#E(GF(p) = p + 1 - 2e
        ord[3] = p.add(ONE).subtract(e.multiply(TWO));
        return ord;
    }

    public static boolean checkOrder(BigInteger[] ord) {
                //#E(GF(p)) = 2m или 4m
        for (int i = 0; i < 2; i++)
            if (ord[i].divide(FOUR).isProbablePrime(100))
                return true;
        for (int i = 2; i < 4; i++)
            if (ord[i].divide(TWO).isProbablePrime(100))
                return true;
        return false;
    }

    public static boolean checkDivisibility(BigInteger p, BigInteger[] ord, int n, int k) {
        int flag = 0;
        for (int i = 0; i < 4; i++)
                for (int j = 1; j <= k; j++)
                    if (ord[i].mod(p.pow((int) Math.pow(n, j)).subtract(ONE)).equals(ZERO)) {
                        flag++;
                        break;
                    }
        return (flag != 4);
    }

    public static BigInteger[] genPoint(BigInteger p, BigInteger[] ord) {
        BigInteger x, y, a, m, lambda;
        Random rand = new Random(System.nanoTime());
        while (true) {
            do
                x = new BigInteger(p.bitLength(), rand).mod(p);
            while (x.equals(ZERO));
            do
                y = new BigInteger(p.bitLength(), rand).mod(p);
            while (y.equals(ZERO));
            BigInteger[] result = {x, y};
            a = y.pow(2).subtract(x.pow(2)).divide(x).mod(p);
            if (p.subtract(a).modPow(p.subtract(ONE).divide(TWO), p).equals(ONE))
                if (!ord[0].equals(ZERO) && !ord[1].equals(ZERO)) {
                    System.out.println(ord[0].divide(FOUR));
                    System.out.println(ord[1].divide(FOUR));
                    m = ord[0].divide(FOUR);
                } else
                    continue;
            else if (!ord[2].equals(ZERO) && !ord[3].equals(ZERO)) {
                System.out.println(ord[2].divide(FOUR));
                System.out.println(ord[3].divide(FOUR));
                m = ord[3].divide(TWO);
            } else
                continue;
            BigInteger x1 = x, x3;
            BigInteger yt = y;
            for (BigInteger i = ONE; i.compareTo(ord[0]) < 1; i = i.add(ONE)) {
                lambda = x1.pow(2).multiply(THREE).add(a).divide(yt.multiply(TWO)).mod(p);
                x3 = lambda.pow(2).subtract(x1.multiply(TWO)).mod(p);
                yt = lambda.multiply(x1.subtract(x3)).subtract(yt).mod(p);
                if (yt.equals(ZERO) && i.compareTo(ord[0]) < 0)
                    return result;
                x1 = x3;
            }
        }
    }
}
