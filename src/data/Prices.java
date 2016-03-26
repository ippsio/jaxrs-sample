package data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Prices {
	private String symbol;
	private List<Price> items;
}
