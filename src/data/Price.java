package data;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Price {
	private String broaker;
	private String bid;
	private String ask;
	private String spread;
	private Date acceptTime;
}
