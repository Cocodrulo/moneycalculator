package software.ulpgc.moneycalculator.application.queen;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WebService {
    private static final String ApiKey = "aeb1cd5ef6081142040d717f";
    private static final String ApiUrl = "https://v6.exchangerate-api.com/v6/API-KEY/".replace("API-KEY", ApiKey);
    private static final String FlagsUrl = "https://flagcdn.com/w40/";

    public static class CurrencyLoader implements software.ulpgc.moneycalculator.architecture.io.CurrencyLoader {

        @Override
        public List<Currency> loadAll() {
            try {
                return readCurrencies();
            } catch (IOException e) {
                return List.of();
            }
        }

        private List<Currency> readCurrencies() throws IOException {
            try (InputStream is = openInputStream(createConnection())) {
                return readCurrenciesWith(jsonIn(is));
            }
        }

        private List<Currency> readCurrenciesWith(String json) throws IOException {
            return readCurrenciesWith(jsonObjectIn(json));
        }

        private List<Currency> readCurrenciesWith(JsonObject jsonObject) throws IOException {
            return readCurrenciesWith(jsonObject.get("supported_codes").getAsJsonArray());
        }

        private List<Currency> readCurrenciesWith(JsonArray jsonArray) throws IOException {
            List<Currency> list = new ArrayList<>();
            for (JsonElement item : jsonArray)
                list.add(readCurrencyWith(item.getAsJsonArray()));
            return list;
        }

        private Currency readCurrencyWith(JsonArray tuple) throws IOException {
            return new Currency(
                    tuple.get(0).getAsString(),
                    tuple.get(1).getAsString(),
                    getCurrencyImage(tuple.get(0).getAsString())
            );
        }

        private String getCurrencyImage(String code) throws IOException {
            if (code == null || code.length() < 2) return "";
            return FlagsUrl + code.substring(0, 2).toLowerCase() + ".png";
        }

        private static String jsonIn(InputStream is) throws IOException {
            return new String(is.readAllBytes());
        }

        private static JsonObject jsonObjectIn(String json) {
            return new Gson().fromJson(json, JsonObject.class);
        }

        private InputStream openInputStream(URLConnection connection) throws IOException {
            return connection.getInputStream();
        }

        private static URLConnection createConnection() throws IOException {
            URL url = URI.create(ApiUrl + "codes").toURL();
            return url.openConnection();
        }
    }

    public static class ExchangeRateLoader implements software.ulpgc.moneycalculator.architecture.io.ExchangeRateLoader {
        @Override
        public ExchangeRate load(Currency from, Currency to) {
            try {
                return new ExchangeRate(
                    LocalDate.now(),
                    from,
                    to,
                    readConversionRate(URI.create(ApiUrl + "pair/" + from.code() + "/" + to.code()).toURL())
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private double readConversionRate(URL url) throws IOException {
            return readConversionRate(url.openConnection());
        }

        private double readConversionRate(URLConnection connection) throws IOException {
            try (InputStream inputStream = connection.getInputStream()) {
                return readConversionRate(new String(new BufferedInputStream(inputStream).readAllBytes()));
            }
        }

        private double readConversionRate(String json) {
            return readConversionRate(new Gson().fromJson(json, JsonObject.class));
        }

        private double readConversionRate(JsonObject object) {
            return object.get("conversion_rate").getAsDouble();
        }

    }
}
