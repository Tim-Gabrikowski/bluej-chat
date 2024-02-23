import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * A basic ChatMessage with its structure
 */
public class ChatMessage {

	private String _message;
	private String _senderName;
	private long _timestamp;

	public ChatMessage(String message, String name) {
		_message = message;
		_senderName = name;
		_timestamp = Instant.now().getEpochSecond();
	}

	public ChatMessage(String message, String name, long timestamp) {
		_message = message;
		_senderName = name;
		_timestamp = timestamp;
	}

	public static ChatMessage fromString(String msg) {
		String pattern = "^(\\d+);([^;]+);(.+)$";
		Pattern regex = Pattern.compile(pattern);
		Matcher matcher = regex.matcher(msg);

		if (matcher.matches()) {
			String timestamp = matcher.group(1);
			String name = matcher.group(2);
			String message = matcher.group(3);

			return new ChatMessage(message, name, Long.parseLong(timestamp));
		} else {
			// Handle the case where the input doesn't match the expected pattern
			System.out.println("Invalid input format");
			return null;
		}
	}

	@Override
	public String toString() {
		return _timestamp + ";" + _senderName + ";" + _message;
	}

	public String displayString() {
		Instant instant = Instant.ofEpochSecond(_timestamp);
		LocalDateTime localDateTime = LocalDateTime.ofInstant(
			instant,
			ZoneOffset.UTC
		);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
			"dd-MM-yyyy HH:mm:ss"
		);
		return (
			"[" +
			localDateTime.format(formatter) +
			"] " +
			_senderName +
			": " +
			_message
		);
	}
}
