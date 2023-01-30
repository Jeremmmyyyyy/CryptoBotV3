# CryptoBotV3 :moneybag:

This was an attempt to create a real time Bitcoin (BTC) traiding bot. The data was downloaded every minute from [AlphaVantage](https://www.alphavantage.co/) and was then used to analyse the market in order to create virtual buy and sell actions
The GUI is done with JavaFX.

If you want to use the project you need 2 API keys that need to be inserted in this [file](CryptoBotV3/src/CryptoBot/WebService/RequestHandler.java) at line 36-41
```
public enum ApiKey{
    API_KEY_1 ("HERE"),
    API_KEY_2 ("HERE");
    String key;
    ApiKey(String key){this.key = key;}
}
```
This part of the program manages to use the key at a maximum of 500 times a day for each key.

## Some pictures 

### Daily BTC course monitor
![image](https://user-images.githubusercontent.com/79469048/215513513-27889b9e-5883-4ffb-9506-f3945a301a64.png)

### Real time BTC exchange rate monitor
![image](https://user-images.githubusercontent.com/79469048/215513650-558552d8-23e7-462f-abc4-b35dd61419cd.png)

### Trading bot monitor
![image](https://user-images.githubusercontent.com/79469048/215513756-1a374cf8-b0f0-4b8e-8b9d-014e30cf222a.png)

### API request handler monitor
![image](https://user-images.githubusercontent.com/79469048/215513871-a126b6de-f491-453b-ae4e-7215c8db4497.png)


> This project was coded in Java 11 
