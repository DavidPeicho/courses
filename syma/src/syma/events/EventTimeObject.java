package syma.events;

public class EventTimeObject extends AEventObject {
	
	public enum Type {
		MORNING_HOUR,
		HOUR,
		YEAR
	};
	
	public int hour;
	public Type type;
	
	public EventTimeObject(Type type) {
		this.type = type;
	}

}
