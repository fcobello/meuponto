package br.com.cobello.meuponto;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class RestService
{
	@Autowired
	private Environment env;
	final RestTemplate rt = new RestTemplate();
	List<String> cookies;
	String captcha = new String();

	@GetMapping("/")
	public String home()
	{
		return "{\"status\":\"up\"}";
	}

	/**
	 * Bate o ponto
	 * @param cpf
	 * @param senha
	 */
	@GetMapping("/hitpoint")
	public String hitPoint(@RequestParam final String cpf, @RequestParam final String senha) 
	{
		log.info(env.getProperty("url.hit"));
		log.info(env.getProperty("url.captcha"));

		final String urlHit = env.getProperty("url.hit");
		final HttpHeaders headers = new HttpHeaders();
		final MultiValueMap<String, String> form;
		final HttpEntity<MultiValueMap<String, String>> request;
		final HttpEntity<String> response;

		resolveCaptcha();
		form = fillForm(captcha, cpf, senha);
		headers.put("Cookie", cookies);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString());
		request = new HttpEntity<MultiValueMap<String, String>>(form, headers);
		response = rt.exchange(urlHit, HttpMethod.POST, request, String.class);
		log.info("Response: {}", response.getBody());

		return response.getBody();
	}

	/**
	 * Resolve o captcha com base no cookie de resposta
	 */
	private void resolveCaptcha() 
	{
		final String urlCaptcha = env.getProperty("url.captcha");
		final HttpEntity<String> response = rt.exchange(urlCaptcha, HttpMethod.GET, null, String.class);
		final HttpHeaders headersResp = response.getHeaders();

		for (final Map.Entry<String, List<String>> entry : headersResp.entrySet()) {
			if (entry.getKey().equals(HttpHeaders.SET_COOKIE)) {
				cookies = entry.getValue();

				for (final String cookie : entry.getValue()) {
					if (cookie.startsWith("ASPCAPTCHA")) {
						captcha = cookie.split("=")[1];
					}
				}
			}
		}
	}

	private MultiValueMap<String, String> fillForm(final String captcha, final String cpf, final String senha)
	{
		final MultiValueMap<String, String> form;

		form = new LinkedMultiValueMap<String, String>();
		form.add("acao", "1");
		form.add("txtValor", cpf);
		form.add("txtSenha", senha);
		form.add("cboCampo", "2");
		form.add("chkAdicPer", "1");
		form.add("chkAdicIns", "1");
		form.add("chkAdicEmb", "1");
		form.add("captchacode", captcha);
		form.add("cboLocal", "3629");
		
		return form;
	}
}
