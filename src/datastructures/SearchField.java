package datastructures;

public enum SearchField {
	
	DOCNO("docno", "Document Number"),
	DOCPARENT("parent", "Document Parent"),
	USDEPT("usdept", "US Department"),
	USBUREAU("usbureau", "US Bureau"),
	DOCCONTENT("text", "Document Content"),
	LASTMODIFIED("modified", "Last Modified"),
	PATH("path", "Document Path"),
	ALL("all", "All Fields");
	
	private String mField;
	private String mDescription;
	
	SearchField(String field, String desc) {
		this.mField = field;
		this.mDescription = desc;
	}
	
	public String field() {
		return mField;
	}
	
	public String description() {
		return mDescription;
	}
}
