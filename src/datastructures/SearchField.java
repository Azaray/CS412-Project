package datastructures;

public enum SearchField {
	
	DOCNO("docno", "Document Number"),
	DOCPARENT("parent", "Document Parent"),
	USDEPT("usdept", "US Department"),
	USBUREAU("usbureau", "US Bureau"),
	DOCCONTENT("text", "Document Content"),
	CFRNO("cfrno", "Code of Federal Regulations"),
	RINDOCK("rindock", "Docket Number"),
	AGENCY("agency", "Agency"),
	ACTION("action", "Action"),
	SUMMARY("summary", "Summary"),
	FURTHER("further", "Further"),
	SUPPLEM("supplem", "Supplementary Information"),
	SIGNER("signer", "Signer"),
	SIGNJOB("signjob", "Signer Job"),
	FRFILING("frfiling", "FR Filing"),
	BILLING("billing", "Billing"),
	ADDRESS("address", "Address"),
	FOOTNAME("footname", "Foot Name"),
	FOOTNOTE("footnote", "Foot Note"),
	FOOTCITE("footnote", "Foot Citation"),
	DATE("date", "Date"),
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
