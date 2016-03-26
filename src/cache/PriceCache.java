package cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import data.Price;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import static java.util.Comparator.*;

@ToString
public class PriceCache {
	private PriceCache() {
	}
	private static PriceCache me = new PriceCache();
	public static PriceCache getInstance() {
		return me;
	}

	@Data
	@AllArgsConstructor
	private class SymbolAndBroaker {
		private String symbol;
		private String broaker;
	}

	private Map<SymbolAndBroaker, Price> prices = new HashMap<>();

	/**
	 * symbol で検索する
	 * @param symbol
	 * @return
	 */
	public List<Price> findBySymbol(String symbol) {
		List<Price> result = new ArrayList<>();
		prices.entrySet().forEach(entry -> {
			if (entry.getKey().getSymbol().equals(symbol)) {
				result.add(entry.getValue());
			}
		});
		result.stream().sorted(comparing(Price::getSpread).reversed());
		return result;
	}

	/**
	 * broaker で検索する
	 * @param broaker
	 * @return
	 */
	public List<Price> findByBroaker(String broaker) {
		List<Price> result = new ArrayList<>();
		prices.entrySet().forEach(entry -> {
			if (entry.getKey().getBroaker().equals(broaker)) {
				result.add(entry.getValue());
			}
		});
		return result;
	}

	/**
	 * symbol と broaker が一致するエンティティを返す
	 * @param symbol
	 * @param broaker
	 * @return
	 */
	public Price get(String symbol, String broaker) {
		Price result = null;
		for (Entry<SymbolAndBroaker, Price> entry: prices.entrySet()) {
			if (entry.getKey().equals(new SymbolAndBroaker(symbol, broaker))) {
				result = entry.getValue();
				break;
			}
		}
		if (result == null) {
			result = new Price(broaker, "0.0", "0.0", String.format("%.1f", 0.0), new Date(0L));
		}
		return result;
	}

	/**
	 * キャッシュに保存する
	 * @param broaker
	 * @param symbol
	 * @param bid
	 * @param ask
	 * @param digits
	 * @param acceptTime
	 * @return
	 */
	public Price save(String broaker, String symbol, double bid, double ask, int digits, Date acceptTime) {
		double spreadPips = calcSpreadPips(bid, ask, digits);
		spreadPips = Double.parseDouble(String.format("%.1f", spreadPips));
		return prices.put(new SymbolAndBroaker(symbol, broaker), new Price(broaker, String.format("%." + Integer.toString(digits) + "f", bid), String.format("%." + Integer.toString(digits) + "f", ask), String.format("%.1f", spreadPips), acceptTime));
	}

	public double calcSpreadPips(double bid, double ask, double digits) {
	    double spreadPips = (ask - bid) * Math.pow(10.0, digits);
	    if (digits == 3 || digits == 5){
	    	spreadPips = spreadPips/ 10.0;
	    }
		return spreadPips;
	}
}
