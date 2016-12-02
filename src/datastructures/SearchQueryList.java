package datastructures;

import java.util.List;

public class SearchQueryList {

	private List<SearchQuery> mList;

	public SearchQueryList(List<SearchQuery> list) {
		this.mList = list;
	}

	public List<SearchQuery> getContents() {
		return this.mList;
	}

	@Override
	public String toString() {

		StringBuilder strb = new StringBuilder();
		for (SearchQuery sq : mList) {

			if (sq.getQueryString().equals("") || sq.getQueryString().equals(" ") || sq.getQueryString().isEmpty() || sq.getQueryString() == null)
				strb.append("Blank Query");
			else {
				strb.append(sq.getSearchField().description() + ": ");
				
				List<String> queryList = sq.getHighlightQueryList();
				for(String str : queryList) {
					strb.append(str);
					
					if (!sq.equals(queryList.get(queryList.size() - 1))) {
						strb.append(" ");
					}
				}
				
			}
			if (!sq.equals(mList.get(mList.size() - 1))) {
				strb.append(", ");
			}
		}

		return strb.toString();
	}
}
