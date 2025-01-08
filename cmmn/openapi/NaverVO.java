package cmmn.openapi;

import cmmn.vo.ParamVO;

public class NaverVO extends ParamVO {

	private static final long serialVersionUID = 1L;

	private String clientId; // 네이버 API Client ID

	private String clientSecret; // 네이버 API Client Secret

	/**
	 * 책 제목 검색 파라메터
	 * API: 검색 > 책 > 책상세검색
	 * 예: d_titl=검색어
	 * 선택 파라미터
	 */
	private String dTitl;

	/**
	 * isbn 검색 파라메터
	 * API: 검색 > 책 > 책상세검색
	 * 예: d_isbn=9788912345678
	 * 선택 파라미터
	 */
	private String dIsbn;

	public NaverVO() {

	}

	public NaverVO(String clientId, String clientSecret) {

		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public String getClientId() {

		return clientId;
	}

	public void setClientId(String clientId) {

		this.clientId = clientId;
	}

	public String getClientSecret() {

		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {

		this.clientSecret = clientSecret;
	}

	public String getdTitl() {

		return dTitl;
	}

	public void setdTitl(String dTitl) {

		this.dTitl = dTitl;
	}

	public String getdIsbn() {

		return dIsbn;
	}

	public void setdIsbn(String dIsbn) {

		this.dIsbn = dIsbn;
	}

}
