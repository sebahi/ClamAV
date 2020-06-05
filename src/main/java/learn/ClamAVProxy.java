package learn;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fi.solita.clamav.ClamAVClient;

@RestController
public class ClamAVProxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClamAVProxy.class);

	@Value("${clamd.host}")
	private String hostname;

	@Value("${clamd.port}")
	private int port;

	@Value("${clamd.timeout}")
	private int timeout;

	/**
	 * @return Clamd status.
	 */
	@RequestMapping("/")
	public String ping() throws IOException {
		LOGGER.info("hostname {}", hostname);
		ClamAVClient a = new ClamAVClient(hostname, port, timeout);
		return "Clamd responding: " + a.ping() + "\n";
	}

	/**
	 * @return Clamd scan result
	 */
	@RequestMapping(value = "/scan", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) throws IOException {
		LOGGER.info("Start scanning file: {}", name);
		if (!file.isEmpty()) {
			ClamAVClient a = new ClamAVClient(hostname, port, timeout);
			byte[] r = a.scan(file.getInputStream());
			return "Everything ok : " + ClamAVClient.isCleanReply(r) + "\n";
		} else
			throw new IllegalArgumentException("empty file");
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public @ResponseBody Boolean checkClamavHealth() throws IOException {
		ClamAVClient a = new ClamAVClient(hostname, port, timeout);
		return a.ping();
	}

	/**
	 * @return Clamd scan reply
	 */
	@RequestMapping(value = "/scanReply", method = RequestMethod.POST)
	public @ResponseBody String handleFileUploadReply(@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) throws IOException {
		LOGGER.info("Start scanning file: {}", name);
		if (!file.isEmpty()) {
			ClamAVClient a = new ClamAVClient(hostname, port, timeout);
			return new String(a.scan(file.getInputStream()));
		} else
			throw new IllegalArgumentException("empty file");
	}
}
