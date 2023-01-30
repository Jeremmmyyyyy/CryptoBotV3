package CryptoBot.TradingBot.Bots;

import CryptoBot.TradingBot.TradingBotBasic;

public class TradingBot1 extends TradingBotBasic {

    public TradingBot1(String filename){
        super(filename);
    }

    @Override
    protected void computeNextAction() {

        if (counter % 4 == 0){
            buy(50);
        }else if (counter % 4 == 3){
            sell(100);
        }else {
            doNothing();
        }
    }
}
