package CryptoBot;

public final class Preconditions {

    private Preconditions(){}

    public static void verify(boolean shouldBeTrue){
        if (! shouldBeTrue)
            throw new IllegalArgumentException();
    }
}
