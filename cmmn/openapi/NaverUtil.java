package cmmn.openapi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cmmn.util.NaverApiUtil;
import cmmn.util.PropertiesUtil;
import cmmn.util.StringUtil;
import cmmn.vo.NaverApiVO;
import cmmn.vo.ParamVO;

/**
 * 네이버 API 유틸
 */
public class NaverUtil {

	private static final String NAVER_API_URL = PropertiesUtil.getProperty("Globals.naverApiUrl");

	private static final String NAVER_CLIENT_ID = PropertiesUtil.getProperty("Globals.naverClientId");

	private static final String NAVER_CLIENT_SECRET = PropertiesUtil.getProperty("Globals.naverClientSecret");

	protected static final Logger LOG = LoggerFactory.getLogger(NaverUtil.class);


	public static Map<String, Object> getNaverShortUrl(String url) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(NAVER_API_URL).path("/v1/util/shorturl");

		Map<String, Object> resultMap = new HashMap<String, Object>();

		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();

		//url = url.replace("http://localhost:8080/", PropertiesUtil.SYSTEM_URL).replace("%26", "&");

		url = url.replace("http://localhost:8080/", "https://jungnanglib.yesjnet.com/").replace("%26", "&");


		LOG.debug("url=>" + url);
		paramMap.add("url", url);


		String responseBody = naverApiRequestPost(uriBuilder.build().toUriString(), paramMap);

		if (StringUtil.isNotEmpty(responseBody)) {
			resultMap = naverShortUrlResultParsing(responseBody);
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> naverShortUrlResultParsing(String responseBody) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		JSONObject jsonObject;

		try {
			JSONParser jsonParser = new JSONParser();
			jsonObject = (JSONObject) jsonParser.parse(responseBody);

			if (jsonObject != null) {

				JSONObject resultObj = (JSONObject) jsonObject.get("result");

				Set<String> key = resultObj.keySet();
				Iterator<String> iter = key.iterator();
				while (iter.hasNext()) {
					String keyName = iter.next();
					resultMap.put(keyName, resultObj.get(keyName));
				}
			}
		} catch (ParseException e) {
			LOG.error("ParseException");
		}

		return resultMap;
	}

	public static Map<String, Object> getNaverBookList(ParamVO requestParam) {

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(NAVER_API_URL).path("/v1/search/book.json");

		Map<String, Object> resultMap = new HashMap<String, Object>();

		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();

		paramMap.add("query", requestParam.getSearchKeyword());
		paramMap.add("start", String.valueOf(requestParam.getCurrentPageNo()));
		paramMap.add("display", "10");

		String responseBody = naverApiRequest(uriBuilder.build().toUriString(), paramMap);

		if (StringUtil.isNotEmpty(responseBody)) {
			resultMap = responseResultParsing(responseBody);
		}
		return resultMap;
	}

	public static Map<String, Object> getNaverBookDetail(String isbn){

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(NAVER_API_URL).path("/v1/search/book.json");

		Map<String, Object> resultMap = new HashMap<String, Object>();

		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();

		paramMap.add("query", isbn);
		paramMap.add("start", "1");
		paramMap.add("display", "1");
		paramMap.add("d_isbn", isbn);

		String responseBody = naverApiRequest(uriBuilder.build().toUriString(), paramMap);
		//LOG.debug("responseBody=>" + responseBody);
		if (StringUtil.isNotEmpty(responseBody)) {
			resultMap = responseResultParsing(responseBody);
		}
		return resultMap;
	}

	public static String naverApiRequestPost(String searchUrl, MultiValueMap<String, String> paramMap) {

		String responseBody = "";

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
		headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
		headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
		HttpEntity<?> entity = new HttpEntity<>(paramMap, headers);

		try {
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
			factory.setReadTimeout(5000);
			factory.setConnectTimeout(5000);
			RestTemplate restTemplate = new RestTemplate(factory);

			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			ResponseEntity<String> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, String.class);

			responseBody = responseEntity.getBody();
		} catch (RestClientException e) {
			LOG.error("RestClientException");
		}
		return responseBody;
	}

	public static String naverApiRequest(String searchUrl, MultiValueMap<String, String> paramMap) {

		String responseBody = "";

		NaverApiVO naverApiVO = NaverApiUtil.getNaverApi();
		String clientId = naverApiVO.getClientId();
		String clientSecret = naverApiVO.getClientSecret();

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
		HttpEntity<?> entity = new HttpEntity<>(paramMap, headers);

		try {
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
			factory.setReadTimeout(15000);
			factory.setConnectTimeout(15000);
			RestTemplate restTemplate = new RestTemplate(factory);

			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

			String requestUrl = UriComponentsBuilder.fromHttpUrl(searchUrl).queryParams(paramMap).build().toUriString();
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);

			responseBody = responseEntity.getBody();

		} catch (RestClientException e) {
			LOG.error("RestClientException");
		}
		return responseBody;
	}

	public static Map<String, Object> responseResultParsing(String responseBody) {

		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			resultMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException e) {
			LOG.error("IOException");
		}

		responseMap.put("resultTotal", resultMap.get("total"));
		responseMap.put("resultItems", resultMap.get("items"));

		return responseMap;
	}
}
