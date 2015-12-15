import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class MainClass {
    public static BigInteger ZERO = BigInteger.ZERO;
    public static BigInteger ONE = BigInteger.ONE;
    public static BigInteger TWO = new BigInteger("2");
    public static BigInteger FOUR = new BigInteger("4");
    public static BigInteger order, m,a;

    public static void main(String[] args) throws IOException {

        int length = 10;
        //Prime generation with fixed length
        Random rnd = new Random(System.nanoTime());
        BigInteger p = new BigInteger(length, rnd);
        do {
            p = p.nextProbablePrime();
        } while (!p.mod(new BigInteger("4")).equals(ONE));

        System.out.println("P = " + p);
        //Decompositon into the sum of squares
        Complex f = factor(p);
        BigInteger d = f.b.abs(),
                e = f.a.abs();
        System.out.println(String.format("d = %d\ne = %d",d,e));

        //Checking consequences
        order = order(p, d, e);
        if (order == null) {
            System.err.println("Не выполняется условие для порядка");
            return;
        }
        System.out.println("#E(GF(p)) = " + order);
        System.out.println("m = " + m);
        if (!checkDivisibility(p, order, 1, 30)) {
            System.err.println("Не выполняется условие делимости");
            return;
        }
        Point P0 = getP0(p);
        System.out.println(String.format("P_0 = (%d;%d)", P0.x, P0.y));
        Point G = P0;
        BigInteger i = ONE;
        while (i.compareTo(order.divide(m)) < 0) {
            if (G == null)
                G = P0;
            else
                G = G.add(P0);
            i = i.add(ONE);
        }
        if (G == null)
            System.out.println("G = P_inf");
        else {
            System.out.println(String.format("G = (%d;%d)", G.x, G.y));
            System.out.println("a = " + a);
            genPoints(G, order);
        }
    }

    public static void genPoints(Point G, BigInteger amount) throws IOException {
        Point tmp = G;
        BigInteger i = ONE;
        FileWriter x = new FileWriter("x.txt");
        FileWriter y = new FileWriter("y.txt");
        while (i.compareTo(amount) < 0) {
            tmp = tmp.add(G);
            i = i.add(ONE);
            if (tmp == null)
                break;
            x.write(tmp.x+"\r");
            y.write(tmp.y+"\r");
        }

        x.flush();
        y.flush();
    }

    public static Complex factor(BigInteger prime) {
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

    public static BigInteger div(BigInteger a, BigInteger b){
        BigInteger c = a.divide(b);
        if (c.equals(ZERO)){
            if (a.compareTo(ZERO) < 0 || b.compareTo(ZERO) < 0)
                c = ONE.negate();
        }
        return c;
    }

    public static BigInteger order(BigInteger p, BigInteger d, BigInteger e) {
        BigInteger[] ord = new BigInteger[4];
        //#E(GF(p) = p + 1 + 2d
        ord[0] = p.add(ONE).add(d.multiply(TWO));
        //#E(GF(p) = p + 1 - 2d
        ord[1] = p.add(ONE).subtract(d.multiply(TWO));
        //#E(GF(p) = p + 1 + 2e
        ord[2] = p.add(ONE).add(e.multiply(TWO));
        //#E(GF(p) = p + 1 - 2e
        ord[3] = p.add(ONE).subtract(e.multiply(TWO));
        System.out.println(Arrays.toString(ord));
        for (int i = 0; i < 2; i++) {
            if (ord[i].mod(FOUR).equals(ZERO)) {
                m = ord[i].divide(FOUR);
                return(ord[i]);
            }
        }
        for (int i = 2; i < 4; i++) {
            if (ord[i].divide(TWO).isProbablePrime(100))
            {
                m = ord[i].divide(TWO);
                return ord[i];
            }
        }
        /*for (int i = 0; i < ord.length; i++) {
            if (ord[i].divide(FOUR).isProbablePrime(1000)) {
                m = ord[i].divide(FOUR);
                return ord[i];
            }
            if (ord[i].divide(TWO).isProbablePrime(100)) {
                m = ord[i].divide(TWO);
                return ord[i];
            }
        }*/
        return null;
    }


    public static boolean checkDivisibility(BigInteger p, BigInteger ord, int n, int k) {
        for (int j = 1; j <= k; j++)
            if (ord.mod(p.pow((int) Math.pow(n, j)).subtract(ONE)).equals(ZERO))
                return false;
        return true;
    }

    public static Point getP0(BigInteger p) {
        BigInteger x, y;
        boolean residue = order.divide(m).equals(FOUR);
        boolean check;
        Random rand = new Random(System.nanoTime());
        while (true) {
            do
                x = new BigInteger(p.bitLength(), rand).mod(p);
            while (x.equals(ZERO));
            do
                y = new BigInteger(p.bitLength(), rand).mod(p);
            while (y.equals(ZERO));
            BigInteger temp = gcdExtended(x, p)[1];
            a = y.pow(2).subtract(x.pow(3)).multiply(temp).mod(p);
            check = p.subtract(a).modPow(p.subtract(ONE).divide(TWO), p).equals(ONE);
            if (residue ^ check)
                continue;
            Point P0 = new Point(x, y, p);
            Point G = P0;
            BigInteger i = ONE;
            while (i.compareTo(order) < 0) {
                if (G == null)
                    return P0;//G = P0;
                else
                    G = G.add(P0);
                i = i.add(ONE);
            }
            if (G == null)
                return P0;
        }

    }

    public static BigInteger[] gcdExtended(BigInteger a, BigInteger b){
        if (b.equals(ZERO))
            return new BigInteger[]{a, ONE, ZERO};
        BigInteger x1 = ZERO, y2 = ZERO;
        BigInteger x2 = ONE, y1 = ONE;
        BigInteger x = ZERO, y = ZERO, d = ZERO;
        while (b.compareTo(ZERO) > 0){
            BigInteger q = div(a, b);
            BigInteger r = a.subtract(q.multiply(b));
            x = x2.subtract(q.multiply(x1));
            y = y2.subtract(q.multiply(y1));
            a = b;
            b = r;
            x2 = x1;
            x1 = x;
            y2 = y1;
            y1 = y;
            d = a;
            x = x2;
            y = y2;
        }
        return new BigInteger[]{d, x, y};
    }
}
