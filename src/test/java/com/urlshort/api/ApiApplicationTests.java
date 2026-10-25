package com.urlshort.api;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createsRedirectsAndDeletesShortUrl() throws Exception {
		MvcResult result = mockMvc.perform(post("/shorten")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"url\":\"https://example.com/path\"}"))
				.andExpect(status().isCreated())
				.andReturn();

		String shortenedId = URI.create(result.getResponse().getHeader("Location")).getPath().substring(1);

		mockMvc.perform(get("/{shortenedId}", shortenedId))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", "https://example.com/path"));

		mockMvc.perform(delete("/{shortenedId}", shortenedId))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/{shortenedId}", shortenedId))
				.andExpect(status().isNotFound());
	}

	@Test
	void rejectsInvalidUrl() throws Exception {
		mockMvc.perform(post("/shorten")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"url\":\"javascript:alert(1)\"}"))
				.andExpect(status().isBadRequest());
	}

}
